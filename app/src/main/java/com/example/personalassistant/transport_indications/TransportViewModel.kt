package com.example.personalassistant.transport_indications

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalassistant.services.conv_agent.Journey
import com.example.personalassistant.services.transport.Route
import com.example.personalassistant.services.transport.RouteRequest
import com.example.personalassistant.services.transport.StbInfoApi
import kotlinx.coroutines.launch


class TransportViewModel(val journey: Journey) : ViewModel() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val lineIdForLineName: HashMap<String, Int> = HashMap()

    private val _routes: MutableLiveData<List<Route>> = MutableLiveData(listOf())
    val routes: LiveData<List<Route>>
        get() = _routes

    init {
        // Get the list of lines to extract the mapping between the line name and its ID
        getRoutesForJourney()
    }

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

    private fun getRoutesForJourney() {
        viewModelScope.launch {
            try {
                val routeRequest = RouteRequest(
                    start_lat = journey.srcLat,
                    start_lng = journey.srcLng,
                    stop_lat = journey.dstLat,
                    stop_lng = journey.dstLng
                )

                val routesRes = StbInfoApi.retrofitService.getRoutes(routeRequest)
                _routes.value = routesRes.routes
                if (routesRes.routes.isNotEmpty()) // empty means no route exists at this time
                    Log.d("=========== ROUTES", routesRes.routes[0].segments[0].transportName)
            } catch (e: Exception) {
                _response.value = "STB API failure: ${e.message}"
            }
        }
    }
}
