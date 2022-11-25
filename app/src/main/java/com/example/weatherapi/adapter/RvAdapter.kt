package com.example.weatherapi.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapi.R
import com.example.weatherapi.model.DayForecastModel
import com.example.weatherapi.model.WeatherModel

class RvAdapter : RecyclerView.Adapter<RvAdapter.WeatherItemViewHolder>() {

    private var weatherList: MutableList<DayForecastModel>? = mutableListOf()
    private val liveData: MutableLiveData<List<DayForecastModel>> by lazy {
        MutableLiveData<List<DayForecastModel>>()
    }

    inner class WeatherItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: DayForecastModel) {
            itemView.findViewById<TextView>(R.id.tvTime).text = data.time
                .split("-")
                .slice(1..2)
                .joinToString(separator = "-")
            itemView.findViewById<TextView>(R.id.tvTempMin).text = data.apparentTemperatureMin.toString() + '°'
            itemView.findViewById<TextView>(R.id.tvTempMax).text = data.apparentTemperatureMax.toString() + '°'
            itemView.findViewById<TextView>(R.id.tvPrecipitation).text = data.precipitationSum.toString() + "mm"
            itemView.findViewById<TextView>(R.id.tvWind).text = data.windspeed10mMax.toString() + "km/h"
        }
    }

    fun setWeatherList(weatherModel: WeatherModel?) {
        weatherList?.clear()
        liveData.value = weatherList
        if (weatherModel != null) {
            for (i in 0 until weatherModel.daily.time.size) {
                weatherList?.add(DayForecastModel(weatherModel.daily.time[i],
                    weatherModel.daily.apparentTemperatureMax[i],
                    weatherModel.daily.apparentTemperatureMin[i],
                    weatherModel.daily.windspeed10mMax[i],
                    weatherModel.daily.precipitationSum[i]
                ))
            }
        }
        liveData.value = weatherList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent, false)
        return WeatherItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherItemViewHolder, position: Int) {
        holder.bind(weatherList?.get(position)!!)
    }

    override fun getItemCount(): Int = weatherList?.size ?: 0


}