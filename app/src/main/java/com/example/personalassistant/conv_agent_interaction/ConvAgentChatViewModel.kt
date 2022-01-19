package com.example.personalassistant.conv_agent_interaction

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import com.example.personalassistant.BuildConfig
import com.example.personalassistant.services.conv_agent.ConvAgentApi
import com.example.personalassistant.services.conv_agent.ConvAgentRequest
import com.example.personalassistant.services.conv_agent.NluRequest
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class ConvAgentChatViewModel : ViewModel() {

    val chatMessages = MutableLiveData<MutableList<String>>(mutableListOf())

    var agentResponseStatus = MutableLiveData<String?>()
    val showActionSelector = MutableLiveData<Boolean>(false)

    val showAssets = MutableLiveData<List<String>?>(null)

    var latestAssetUri: Uri? = null
    var latestAssetId: String? = null

    /**
     * Sets the value of the response LiveData to the Mars API status or the successful number of
     * Mars properties retrieved.
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

                if (nluRes.message.intent.name == "mem_assistant.store_following_attr") {
                    showActionSelector.value = true
                    showAssets.value = null
                } else if (nluRes.message.intent.name == "mem_assistant.get_attr") {
                    if (reply.contains("\n")) {
                        showAssets.value = reply.split("\n").map { it.split("➜")[1].trim() }
                    } else {
                        showAssets.value = listOf(reply)
                    }
                } else {
                    chatMessages.value?.add(reply)
                    showAssets.value = null
                }
                chatMessages.value = chatMessages.value
                agentResponseStatus.value = null
            } catch (e: Exception) {
                agentResponseStatus.value = "Conv agent API failure: ${e.message}"
            }
        }
    }

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
}
