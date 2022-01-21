package com.example.personalassistant.services.conv_agent

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

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

data class SemanticRole(
    val question: String, // "unde"
    val determiner: String, // "ajung"
    val pre: String?,
    val value: String,
    val lemma: String?,
    @Json(name = "ext_value") val extendedValue: String,
)

data class NluProcessedMessage(
    val intent: NluIntent,
    val entities: List<Any>,
    val text: String,
    @Json(name = "semantic_roles") val semanticRoles: List<SemanticRole>,
    @Json(name = "intent_ranking") val intentRanking: List<NluIntent>
)

data class NluResult(
    @Json(name = "latest_message") val message: NluProcessedMessage
)

data class ConvAgentResponse(
    @Json(name = "recipient_id") val recipientId: String,
    val text: String,
)

@Parcelize
data class Journey(
    val srcLat: Double,
    val srcLng: Double,
    val dstLat: Double,
    val dstLng: Double,
    val src: String,
    val dest: String,
) : Parcelable