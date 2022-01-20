package com.example.personalassistant.transport_indications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.personalassistant.services.conv_agent.Journey


class TransportViewModelFactory(
    private val journey: Journey,
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransportViewModel::class.java)) {
            return TransportViewModel(journey) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
