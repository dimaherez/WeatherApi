package com.example.weatherapi

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapi.adapter.RvAdapter
import com.example.weatherapi.databinding.ActivityMainBinding
import com.example.weatherapi.location.DefaultLocationTracker
import com.example.weatherapi.viewmodel.MainActivityViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var recyclerAdapter: RvAdapter

    private lateinit var binding: ActivityMainBinding
    private lateinit var tracker: DefaultLocationTracker
    private lateinit var location: Location
    private lateinit var geocoder: Geocoder

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        location = Location("")
        geocoder = Geocoder(this, Locale.getDefault())

        tracker = DefaultLocationTracker(
            this,
            LocationServices.getFusedLocationProviderClient(this),
            this.application
        )

        getCurrentLocation()

        binding.btnCurrentLocation.setOnClickListener {
            getCurrentLocation()
            hideKeyboard()
        }

        binding.btnGetForecast.setOnClickListener {
            if (binding.etLocationName.text.isEmpty()) return@setOnClickListener
            val city = binding.etLocationName.text.toString()
            val addresses = geocoder.getFromLocationName(city, 1)
            if (addresses.isEmpty()) return@setOnClickListener
            val address = addresses[0]
            location.latitude = address.latitude
            location.longitude = address.longitude
            initRV()
            initVM()
            hideKeyboard()
        }

        initRV()

    }

    private fun getCurrentLocation() {
        launch(Dispatchers.Main) {
            try {
                val currentLocation: Location = tracker.getCurrentLocation()!!
                location.latitude = currentLocation.latitude
                location.longitude = currentLocation.longitude
                val city = geocoder.getFromLocation(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    1
                )[0].getAddressLine(0).split(",")[2]
                binding.etLocationName.setText(city)

                initVM()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }

        }
    }

    private fun initRV() {
        binding.rvWeatherList.layoutManager = LinearLayoutManager(this)
        recyclerAdapter = RvAdapter()
        binding.rvWeatherList.adapter = recyclerAdapter
    }

    private fun initVM() {
        val viewModel: MainActivityViewModel =
            ViewModelProvider(this)[MainActivityViewModel::class.java]

        viewModel.getLiveDataObserver().observe(this) {
            if (it != null) {
                recyclerAdapter.setWeatherList(it)
                recyclerAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Error in getting list", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.makeApiCall(this, location)
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showProgressBar() {
        binding.pbLoading.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        binding.pbLoading.visibility = View.GONE
    }
}