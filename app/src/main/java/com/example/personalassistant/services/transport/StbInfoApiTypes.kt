package com.example.personalassistant.services.transport

import com.squareup.moshi.Json

// GPS-positioned vehicle
data class LiveVehicle(
    val code: String,
    val id: Int,
    val lat: Float,
    val lng: Float,
    val transport_type: String,
)

data class LineData(
    val id: Int,
    val name: String,
    val type: String,
    val color: String,
)

data class LinesResponse(
    val lines: List<LineData>,
    @Json(name = "ticket_info") val ticketInfo: Any
)

// Segment of a route that will be traveled with a single transport way
data class RouteSegment(
    val direction: Int,
    val direction_name: String,
    val duration: Int,
    val id: String,
    @Json(name = "transport_line_id") val transportLineId: Int?,
    @Json(name = "transport_name") val transportName: String, // i.e. line number
    @Json(name = "transport_type") val transportType: String // SUBWAY, TRAM, BUS, WALK
)

// Possible route offered
data class Route(
    val duration: Int,
    val segments: List<RouteSegment>,
)

data class RouteResponse(
    val routes: List<Route> // List of possible routes to be taken
)

data class TransportType(
    val type: String,
    val name: String,
    val selected: Boolean,
)

data class RouteRequest(
    val lang: String = "ro",
    val max_walk_distance: Int = 1000,
    val start_lat: Double,
    val start_lng: Double,
    val start_time: Long = 1642614172962,
    val stop_lat: Double,
    val stop_lng: Double,
    val transport_types: List<TransportType> = listOf(
        TransportType(type = "BUS", name = "Autobuz", selected = true),
        TransportType(type = "CABLE_CAR", name = "Troleibuz", selected = true),
        TransportType(type = "TRAM", name = "Tramvai", selected = true),
        TransportType(type = "SUBWAY", name = "Metrou", selected = true),
    )
)

data class Place(
    val description: String?,
    val lat: Double,
    val lng: Double,
    val name: String,
    val stop_id: Int?,
    val type: String // "SUBWAY_STATION", "STATION", "TICKET_OFFICE", "POI"
)

data class PlacesResponse(
    val places: List<Place>
)