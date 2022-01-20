package com.example.personalassistant.conv_agent_interaction

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import com.example.personalassistant.BuildConfig
import com.example.personalassistant.database.PADatabaseDao
import com.example.personalassistant.database.PrefferedLocation
import com.example.personalassistant.services.conv_agent.ConvAgentApi
import com.example.personalassistant.services.conv_agent.ConvAgentRequest
import com.example.personalassistant.services.conv_agent.Journey
import com.example.personalassistant.services.conv_agent.NluRequest
import com.example.personalassistant.services.transport.StbInfoApi
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class ConvAgentChatViewModel(val dataSource: PADatabaseDao) : ViewModel() {

    val chatMessages = MutableLiveData<MutableList<String>>(mutableListOf())

    var agentResponseStatus = MutableLiveData<String?>()
    val showActionSelector = MutableLiveData<Boolean>(false)

    val showAssets = MutableLiveData<Pair<String, List<String>>?>(null)
    var latestAssetId: String? = null

    val transportationLoc: MutableLiveData<Journey?> = MutableLiveData(null)

    init {
        initializePrefferedLocations()
    }

    private fun initializePrefferedLocations() {
        viewModelScope.launch {
            val prefferedLocations = dataSource.getAll()
            if (prefferedLocations.isEmpty()) {
                // Initialize preffered locations with some defaults
                dataSource.insert(PrefferedLocation(name = "home", lat = 44.427513, lng = 26.101826))
            }
        }
    }

    /**
     * Send a new message to the conversational agent and wait for its reply.
     */
    fun postAgentMessage(text: String) {
        showActionSelector.value = false
        chatMessages.value?.add(text)
        chatMessages.value = chatMessages.value

        viewModelScope.launch {
            try {
                val nluRes = ConvAgentApi.retrofitService.nluProcess(NluRequest("user", text))
                val result = ConvAgentApi.retrofitService.postMessageAndGetReply(ConvAgentRequest("user", text))

                val reply = result[0].text
                Log.d(">>>>>>>> CONV AGENT", reply)

                showAssets.value = null
                transportationLoc.value = null

                if (nluRes.message.intent.name == "mem_assistant.store_following_attr") {
                    // Link an asset to the mentioned description
                    showActionSelector.value = true
                } else if (nluRes.message.intent.name == "mem_assistant.get_attr") {
                    // Show the user the list of assets linked to the mentioned description

                    val who = nluRes.message.semanticRoles.filter { it.question == "cine" }
                    val description = if (who.isNotEmpty()) who[0].extendedValue.replaceFirstChar { it.uppercase() } else "Linked assets"

                    if (reply.contains("\n")) {
                        val assets = reply.split("\n").map { it.split("➜")[1].trim() }
                        showAssets.value = Pair(description, assets)
                    } else {
                        showAssets.value = Pair(description, listOf(reply))
                    }
                } else if (nluRes.message.intent.name == "mem_assistant.get_transport") {
                    // Find transportation routes for the mentioned destination
                    val locations = nluRes.message.semanticRoles.filter { it.question == "unde" }
                    if (locations.isNotEmpty()) {
                        val destination = locations[locations.size - 1].extendedValue
                        Log.d(">>>>>>>> ROUTES to", destination)

                        // Find exact destination for the mentioned one
                        val placesRes = StbInfoApi.retrofitService.getPlacesForKeyword("ro", destination)

                        viewModelScope.launch {
                            val homeLoc = dataSource.getByName("home")

                            if (placesRes.places.isNotEmpty() && homeLoc != null) {
                                val dest = placesRes.places[0]
                                transportationLoc.value = Journey(homeLoc.lat, homeLoc.lng, dest.lat, dest.lng)
                            }
                        }
                    }
                } else {
                    // Add the agent's reply to the chat list
                    chatMessages.value?.add(reply)
                }
                chatMessages.value = chatMessages.value
                agentResponseStatus.value = null
            } catch (e: Exception) {
                agentResponseStatus.value = "Conv agent API failure: ${e.message}"
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
                chatMessages.value?.add(result[0].text)
                showActionSelector.value = false
                chatMessages.value = chatMessages.value
            } catch (e: Exception) {
                agentResponseStatus.value = "Conv agent API failure: ${e.message}"
            }
        }
    }

    fun getTmpFileUri(context: Context): Uri {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val id = UUID.randomUUID().toString()
        val file = File(storageDir, "$id.jpg")
        file.createNewFile()

        latestAssetId = id

        return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", file)
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun showAssetsPageDone() {
        showAssets.value = null
    }

    fun showTransportationPageDone() {
        transportationLoc.value = null
    }
}
