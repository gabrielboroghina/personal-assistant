package com.example.personalassistant.services.conv_agent

import com.squareup.moshi.Json


data class ConvAgentRequest(
    val sender: String,
    val text: String,
)