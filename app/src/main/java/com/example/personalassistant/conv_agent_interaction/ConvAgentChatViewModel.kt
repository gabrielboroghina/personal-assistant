package com.example.personalassistant.conv_agent_interaction

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.*
import com.example.personalassistant.BuildConfig
import com.example.personalassistant.services.conv_agent.ConvAgentApi
import kotlinx.coroutines.launch
import java.io.File

class ConvAgentChatViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    val chatMessages = MutableLiveData<MutableList<String>>(mutableListOf())

    var agentResponseStatus = MutableLiveData<String?>()

    var latestTmpUri: Uri? = null;

    /**
     * Sets the value of the response LiveData to the Mars API status or the successful number of
     * Mars properties retrieved.
     */
    fun postAgentMessage(text: String) {
        chatMessages.value?.add(text)

        viewModelScope.launch {
            try {
//                val nluRes = ConvAgentApi.retrofitService.nluProcess()
//                val result = ConvAgentApi.retrofitService.postMessageAndGetReply()
                chatMessages.value?.add("TODO result")
                agentResponseStatus.value = null
            } catch (e: Exception) {
                agentResponseStatus.value = "Conv agent API failure: ${e.message}"
            }
        }
    }

    fun getTmpFileUri(context: Context): Uri {
        // Create an image file name
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val tmpFile = File.createTempFile("tmp_image_file", ".png", storageDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }
}
