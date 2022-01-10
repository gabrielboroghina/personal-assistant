package com.example.personalassistant.services

import com.squareup.moshi.Json


data class ConvAgentResponse(
    val id: String,
    @Json(name = "") val message: String,
)