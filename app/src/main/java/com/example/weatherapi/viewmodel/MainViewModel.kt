package com.example.weatherapi.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.weatherapi.model.ForecastModel
import com.example.weatherapi.repository.Repository

class MainViewModel : ViewModel() {
    var coordinates: MutableLiveData<Location> = MutableLiveData()

    val forecast: LiveData<ForecastModel> = Transformations
        .switchMap(coordinates) {
            Repository.getForecast(it.latitude, it.longitude)
        }

    fun setCoordinates(location: Location) {
        coordinates.value = location
    }

    fun cancelJobs() {
        Repository.cancelJobs()
    }

}

