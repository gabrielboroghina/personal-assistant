package com.example.personalassistant.conv_agent_interaction

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import com.example.personalassistant.BuildConfig
import com.example.personalassistant.database.PADatabaseDao
import com.example.personalassistant.database.PreferredLocation
import com.example.personalassistant.services.conv_agent.ConvAgentApi
import com.example.personalassistant.services.conv_agent.ConvAgentRequest
import com.example.personalassistant.services.conv_agent.Journey
import com.example.personalassistant.services.conv_agent.NluRequest
import com.example.personalassistant.services.transport.StbInfoApi
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class ConvAgentChatViewModel(private val dataSource: PADatabaseDao) : ViewModel() {

    private val _chatMessages = MutableLiveData<MutableList<Pair<Int, String>>>(mutableListOf())
    val chatMessages: LiveData<MutableList<Pair<Int, String>>>
        get() = _chatMessages

    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?>
        get() = _statusMessage

    private val _showActionSelector = MutableLiveData<Boolean>(false)
    val showActionSelector: LiveData<Boolean>
        get() = _showActionSelector

    var latestAssetId: String? = null
    private val _showAssets = MutableLiveData<Pair<String, List<String>>?>(null)
    val showAssets: LiveData<Pair<String, List<String>>?>
        get() = _showAssets

    private val _transportationLoc: MutableLiveData<Journey?> = MutableLiveData(null)
    val transportationLoc: LiveData<Journey?>
        get() = _transportationLoc

    init {
        initializePreferredLocations()
    }

    /**
     * Initialize the list of preferred locations in the database
     */
    private fun initializePreferredLocations() {
        viewModelScope.launch {
            val prefferedLocations = dataSource.getAll()
            if (prefferedLocations.isEmpty()) {
                // Initialize preferred locations with some defaults
                dataSource.insert(PreferredLocation(name = "home", lat = 44.427513, lng = 26.101826))
            }
        }
    }

    /**
     * Send a new message to the conversational agent and wait for its reply.
     */
    fun postAgentMessage(text: String) {
        if (text.trim().isEmpty()) {
            return
        }

        _showActionSelector.value = false
        _chatMessages.value?.add(Pair(0, text))
        _chatMessages.value = chatMessages.value

        viewModelScope.launch {
            try {
                val nluRes = ConvAgentApi.retrofitService.nluProcess(NluRequest("user", text))
                val result = ConvAgentApi.retrofitService.postMessageAndGetReply(ConvAgentRequest("user", text))

                val reply = result[0].text
                Log.d(">>>>>>>> CONV AGENT", reply)

                // Reset observed variables
                _showAssets.value = null
                _transportationLoc.value = null
                _statusMessage.value = null

                if (nluRes.message.intent.name == "mem_assistant.store_following_attr") {
                    // Link an asset to the mentioned description
                    _showActionSelector.value = true
                } else if (nluRes.message.intent.name == "mem_assistant.get_attr") {
                    // Show the user the list of assets linked to the mentioned description

                    val who = nluRes.message.semanticRoles.filter { it.question == "cine" }
                    val description =
                        if (who.isNotEmpty()) who[0].extendedValue.replaceFirstChar { it.uppercase() } else "Linked assets"

                    if (reply.contains("\n")) {
                        val assets = reply.split("\n").map { it.split("➜")[1].trim() }
                        _showAssets.value = Pair(description, assets)
                    } else {
                        _showAssets.value = Pair(description, listOf(reply))
                    }
                } else if (nluRes.message.intent.name == "mem_assistant.get_transport") {
                    // Find transportation routes for the mentioned destination
                    val locations = nluRes.message.semanticRoles.filter { it.question == "unde" }
                    if (locations.isNotEmpty()) {
                        val destination = locations[locations.size - 1].extendedValue

                        // Find exact destination for the mentioned one
                        val placesRes = StbInfoApi.retrofitService.getPlacesForKeyword("ro", destination)

                        viewModelScope.launch {
                            val homeLoc = dataSource.getByName("home")

                            if (placesRes.places.isNotEmpty() && homeLoc != null) {
                                val dest = placesRes.places[0]
                                _transportationLoc.value =
                                    Journey(homeLoc.lat, homeLoc.lng, dest.lat, dest.lng, "home", dest.name)
                            } else {
                                _statusMessage.value = "No place with this name was found"
                            }
                        }
                    } else {
                        _statusMessage.value = "Could not extract the location from your utterance"
                    }
                } else {
                    // Add the agent's reply to the chat list
                    _chatMessages.value?.add(Pair(1, reply))
                }
                _chatMessages.value = chatMessages.value
                _statusMessage.value = null
            } catch (e: Exception) {
                _statusMessage.value = "Conv agent API failure: ${e.message}"
            }
        }
    }

    /**
     * Send the UUID of the new linked asset to be stored by the agent for the previously mentioned description.
     */
    fun postAssetId(id: String) {
        viewModelScope.launch {
            try {
                val result = ConvAgentApi.retrofitService.postMessageAndGetReply(ConvAgentRequest("user", id))
                _chatMessages.value?.add(Pair(1, result[0].text))
                _showActionSelector.value = false
                _chatMessages.value = chatMessages.value
            } catch (e: Exception) {
                _statusMessage.value = "Conv agent API failure: ${e.message}"
            }
        }
    }

    /**
     * Create new image file
     */
    fun getTmpFileUri(context: Context): Uri {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val id = UUID.randomUUID().toString()
        val file = File(storageDir, "$id.jpg")
        file.createNewFile()

        latestAssetId = id

        return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
    }

    /**
     * After the navigation has taken place, prevent another identical navigation
     */
    fun showAssetsPageDone() {
        _showAssets.value = null
    }

    fun showTransportationPageDone() {
        _transportationLoc.value = null
    }
}
