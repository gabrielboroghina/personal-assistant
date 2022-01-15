package com.example.personalassistant.services.conv_agent

import com.squareup.moshi.Json


data class NluIntent(
    val name: String
)

data class NluProcessedMessage(
    val intent: NluIntent
)

data class NluResult(
    @Json(name="message") val latest_message: NluProcessedMessage
)

data class ConvAgentResponse(
    val id: String,
    val text: String,
)