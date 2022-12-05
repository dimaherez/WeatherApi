package com.example.weatherapi.repository

import androidx.lifecycle.LiveData
import com.example.weatherapi.model.ForecastModel
import com.example.weatherapi.retrofit.RetrofitInstance
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object Repository {
    var job: CompletableJob? = null

    fun getForecast(latitude: Double, longitude: Double): LiveData<ForecastModel> {
        job = Job()
        return object : LiveData<ForecastModel>() {
            override fun onActive() {
                super.onActive()
                job?.let { theJob ->
                    CoroutineScope(Main + theJob).launch {
                        val forecast = RetrofitInstance.api.getForecast(latitude, longitude)
                        value = forecast
                        theJob.complete()
                    }
                }
            }
        }
    }

    fun cancelJobs() {
        job?.cancel()
    }
}