package com.example.weatherapi

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapi.adapter.RvAdapter
import com.example.weatherapi.databinding.FragmentCurrentLocationBinding
import com.example.weatherapi.location.Permissions
import com.example.weatherapi.viewmodel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.util.*


class CurrentLocationFragment : Fragment() {
    private lateinit var binding: FragmentCurrentLocationBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var recyclerAdapter: RvAdapter
    private lateinit var viewModel: MainViewModel

    private var job: CompletableJob? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrentLocationBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFusedLocationClient =
            context?.let { LocationServices.getFusedLocationProviderClient(it) }!!


        binding.btnRefresh.setOnClickListener {
            it.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.rotate
                )
            )
            makeApiCallByCurrentLocation()
        }

        initRV()
        initVM()

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
        job?.cancel()
    }


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun makeApiCallByCurrentLocation() {
        job = Job()
        job?.let { theJob ->
            CoroutineScope(Main + theJob).launch {
                if (Permissions.permissionsGranted(context)) {
                    activity?.let { it ->
                        mFusedLocationClient.lastLocation.addOnCompleteListener(it) { task ->
                            val location: Location? = task.result
                            if (location != null) {
                                val list: List<Address> =
                                    Geocoder(context, Locale.getDefault()).getFromLocation(
                                        location.latitude,
                                        location.longitude,
                                        1
                                    )
                                binding.tvCityName.text =
                                    "${list[0].locality}, ${list[0].adminArea}, ${list[0].countryName}"

                                viewModel.setCoordinates(location)
                            }
                        }
                    }
                }
                theJob.complete()
            }
        }
    }

    private fun initRV() {
        binding.rvWeatherList.layoutManager = LinearLayoutManager(context)
        recyclerAdapter = RvAdapter()
        binding.rvWeatherList.adapter = recyclerAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initVM() {
        viewModel =
            ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.forecast.observe(viewLifecycleOwner) {
            if (it != null) {
                recyclerAdapter.setForecast(it)
                recyclerAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "Error in getting list", Toast.LENGTH_SHORT).show()
            }
        }

        makeApiCallByCurrentLocation()
    }


}