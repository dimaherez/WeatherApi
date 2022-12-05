package com.example.weatherapi.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ForecastModel(
    @SerializedName("daily")
    val daily: Daily,
    @SerializedName("daily_units")
    val dailyUnits: DailyUnits,
    @SerializedName("elevation")
    val elevation: Double,
    @SerializedName("generationtime_ms")
    val generationtimeMs: Double,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int
) {
    @Keep
    data class Daily(
        @SerializedName("apparent_temperature_max")
        val apparentTemperatureMax: List<Double>,
        @SerializedName("apparent_temperature_min")
        val apparentTemperatureMin: List<Double>,
        @SerializedName("precipitation_sum")
        val precipitationSum: List<Double>,
        @SerializedName("time")
        val time: List<String>,
        @SerializedName("windspeed_10m_max")
        val windspeed10mMax: List<Double>
    )

    @Keep
    data class DailyUnits(
        @SerializedName("apparent_temperature_max")
        val apparentTemperatureMax: String,
        @SerializedName("apparent_temperature_min")
        val apparentTemperatureMin: String,
        @SerializedName("precipitation_sum")
        val precipitationSum: String,
        @SerializedName("time")
        val time: String,
        @SerializedName("windspeed_10m_max")
        val windspeed10mMax: String
    )
}