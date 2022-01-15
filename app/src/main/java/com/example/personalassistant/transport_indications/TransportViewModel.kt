package com.example.personalassistant.transport_indications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalassistant.services.transport.StbInfoApi
import kotlinx.coroutines.launch


class TransportViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    private val lineIdForLineName: HashMap<String, Int> = HashMap()

    init {
        // Get the list of lines to extract the mapping between the line name and its ID
        fetchLines()
    }

    /**
     * Sets the value of the response LiveData to the Mars API status or the successful number of
     * Mars properties retrieved.
     */
    private fun fetchLines() {
        viewModelScope.launch {
            try {
                val result = StbInfoApi.retrofitService.getLines()
                _response.value = "STB response: ${result.lines[0].name}"

                for (line in result.lines) {
                    lineIdForLineName[line.name] = line.id
                }
            } catch (e: Exception) {
                _response.value = "STB API failure: ${e.message}"
            }
        }
    }
}
