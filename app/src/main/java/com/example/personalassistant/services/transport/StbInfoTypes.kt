package com.example.personalassistant.services.transport

import com.squareup.moshi.Json

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
    val type: String, // BUS/TRAM/
    val color: String,
//    val has_notifications: Boolean,
//    val price_ticket_sms: null,
//    ticket_sms": "U",
//    organization: {
//        "id": 1,
//        "logo": "https://info.stbsa.ro/src/img/avl/stbsa/logos/logo.png"
//    }
)

data class LinesResponse(
    val lines: List<LineData>,
    @Json(name = "ticket_info") val ticketInfo: Any
)