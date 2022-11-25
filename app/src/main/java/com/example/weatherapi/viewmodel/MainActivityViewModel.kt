package com.example.weatherapi.viewmodel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapi.MainActivity
import com.example.weatherapi.model.WeatherModel
import com.example.weatherapi.retrofit.RetrofitInstance
import com.example.weatherapi.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel : ViewModel() {
    var liveDataList: MutableLiveData<WeatherModel> = MutableLiveData()

    fun getLiveDataObserver(): MutableLiveData<WeatherModel> = liveDataList

    fun makeApiCall(activity: MainActivity, location: Location) {
        val retrofitInstance = RetrofitInstance.getRetrofitInstance()
        val retrofitService = retrofitInstance.create(RetrofitService::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            activity.showProgressBar()


            val call: Call<WeatherModel> =
                retrofitService.getWeather(location.latitude, location.longitude)

            call.enqueue(object : Callback<WeatherModel> {
                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    activity.hideProgressBar()
                    liveDataList.postValue(null)
                }

                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    activity.hideProgressBar()
                    liveDataList.postValue(response.body())
                }

            })
        }


    }

}

