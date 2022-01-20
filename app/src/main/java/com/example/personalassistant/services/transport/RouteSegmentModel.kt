package com.example.personalassistant.services.transport

data class RouteSegmentModel(val routeSegment: RouteSegment) {
    val transportType: String = routeSegment.transportType
    val transportName: String = routeSegment.transportName
    val duration: Int = routeSegment.duration
}
