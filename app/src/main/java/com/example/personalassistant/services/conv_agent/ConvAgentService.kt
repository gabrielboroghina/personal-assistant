package com.example.personalassistant.services.conv_agent

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL = "http://165.22.91.162/pepper/"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface ConvAgentApiService {
    @POST("conversations/0/messages")
    suspend fun nluProcess(@Body data: NluRequest): NluResult

    @POST("webhooks/rest/webhook")
    suspend fun postMessageAndGetReply(@Body data: ConvAgentRequest): List<ConvAgentResponse>
}

/**
 * Public API object that exposes the lazy-initialized Retrofit service
 */
object ConvAgentApi {
    val retrofitService: ConvAgentApiService by lazy { retrofit.create(ConvAgentApiService::class.java) }
}
