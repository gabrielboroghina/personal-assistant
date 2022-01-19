package com.example.personalassistant.transport_indications

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalassistant.services.transport.Route
import com.example.personalassistant.services.transport.RouteRequest
import com.example.personalassistant.services.transport.StbInfoApi
import kotlinx.coroutines.launch


class TransportViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    private val lineIdForLineName: HashMap<String, Int> = HashMap()

    private val _routes: MutableLiveData<List<Route>> = MutableLiveData(listOf())
    val routes: LiveData<List<Route>>
        get() = _routes

    init {
        // Get the list of lines to extract the mapping between the line name and its ID
        fetchLines()
    }

    private fun fetchLines() {
        viewModelScope.launch {
            try {
                val result = StbInfoApi.retrofitService.getLines()
                _response.value = "STB response: ${result.lines[0].name}"

                for (line in result.lines) {
                    lineIdForLineName[line.name] = line.id
                }


                // TODO use these APIs where needed
                val routeRequest = RouteRequest(
                    start_lat = 44.427513,
                    start_lng = 26.101826,
                    stop_lat = 44.418478,
                    stop_lng = 26.007745,
                )

                val routesRes = StbInfoApi.retrofitService.getRoutes(routeRequest)
                _routes.value = routesRes.routes
                if (routesRes.routes.isNotEmpty()) // empty means no route exists at this time
                    Log.d("===========", routesRes.routes[0].segments[0].transportName)

                val placesRes = StbInfoApi.retrofitService.getPlacesForKeyword("ro", "gara de")
                if (placesRes.places.isNotEmpty())
                    Log.d("===========", placesRes.places[0].name)

            } catch (e: Exception) {
                _response.value = "STB API failure: ${e.message}"
            }
        }
    }
}
