package com.example.foragingapplicationv2.interfaces

import com.example.foragingapplicationv2.models.WeatherResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPIService {
    @GET("weather")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") applicationID: String,
        @Query("units") unitType: String
    ): Call<WeatherResult>
}