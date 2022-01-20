package com.example.personalassistant.conv_agent_interaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.personalassistant.database.PADatabaseDao


class ConvAgentViewModelFactory(
    private val dataSource: PADatabaseDao,
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConvAgentChatViewModel::class.java)) {
            return ConvAgentChatViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
