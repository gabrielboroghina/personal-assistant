package com.example.personalassistant.conv_agent_interaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalassistant.services.conv_agent.ConvAgentApi
import kotlinx.coroutines.launch


class ConvAgentChatViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    val chatMessages = MutableLiveData<MutableList<String>>(mutableListOf())

    var agentResponseStatus = MutableLiveData<String?>()

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
}
