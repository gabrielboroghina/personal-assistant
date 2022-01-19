package com.example.personalassistant.services.transport

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "https://info.stbsa.ro/rp/api/"

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


interface StbInfoApiService {
    @GET("lines")
    suspend fun getLines(): LinesResponse

    @GET("lines/68/vehicles/0")
    suspend fun getLiveVehicles(): List<LiveVehicle>

    @POST("routes")
    suspend fun getRoutes(@Body data: RouteRequest): RouteResponse

    @GET("places")
    suspend fun getPlacesForKeyword(
        @Query("lang") lang: String,
        @Query("query") keyword: String,
    ): PlacesResponse
}

/**
 * Public API object that exposes the lazy-initialized Retrofit service
 */
object StbInfoApi {
    val retrofitService: StbInfoApiService by lazy { retrofit.create(StbInfoApiService::class.java) }
}
