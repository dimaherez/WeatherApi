package com.example.weatherapi.location

import android.location.Location

interface LocationTracker {
    suspend fun getCurrentLocation(): Location?
}