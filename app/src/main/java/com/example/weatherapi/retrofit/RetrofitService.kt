package com.example.weatherapi.retrofit

import com.example.weatherapi.model.ForecastModel
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    @GET("v1/forecast?latitude=49.84&longitude=24.02&timezone=GMT&daily=apparent_temperature_max,apparent_temperature_min,windspeed_10m_max,precipitation_sum")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): ForecastModel
}