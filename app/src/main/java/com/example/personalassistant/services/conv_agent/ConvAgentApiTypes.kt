package com.example.personalassistant.services.conv_agent

import com.squareup.moshi.Json

data class ConvAgentRequest(
    val sender: String,
    val message: String,
)

data class NluRequest(
    val sender: String,
    val text: String,
)

data class NluIntent(
    val name: String,
    val confidence: Double
)

data class NluProcessedMessage(
    val intent: NluIntent,
    val entities: List<Any>,
    val text: String,
    @Json(name = "intent_ranking") val intentRanking: List<NluIntent>
)

data class NluResult(
    @Json(name = "latest_message") val message: NluProcessedMessage
)

data class ConvAgentResponse(
    @Json(name = "recipient_id") val recipientId: String,
    val text: String,
)