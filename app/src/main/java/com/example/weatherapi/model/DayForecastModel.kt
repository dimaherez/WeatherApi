package com.example.weatherapi.model


data class DayForecastModel (
    val time: String,
    val apparentTemperatureMax: Double,
    val apparentTemperatureMin: Double,
    val windspeed10mMax: Double,
    val precipitationSum: Double
        )