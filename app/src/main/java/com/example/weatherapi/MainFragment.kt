package com.example.weatherapi

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapi.adapter.RvAdapter
import com.example.weatherapi.databinding.FragmentMainBinding
import com.example.weatherapi.viewmodel.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.util.*


class MainFragment : Fragment() {
    private lateinit var recyclerAdapter: RvAdapter
    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel
    private var job: CompletableJob? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGetForecast.setOnClickListener {
            makeApiCallByCoordinates()
            activity?.hideKeyboard()
        }

        initRV()
        initVM()

        if (binding.etLocationName.text.isNotEmpty()) {
            makeApiCallByCoordinates()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
        job?.cancel()
    }

    private fun makeApiCallByCoordinates() {
        val location = Location("")
        val geocoder = Geocoder(context, Locale.getDefault())
        job = Job()

        if (binding.etLocationName.text.isEmpty()) return
        val city = binding.etLocationName.text.toString()
        val addresses = geocoder.getFromLocationName(city, 1)
        if (addresses.isEmpty()) return
        val address = addresses[0]

        location.latitude = address.latitude
        location.longitude = address.longitude


        job?.let { theJob ->
            CoroutineScope(Main + theJob).launch {
                viewModel.setCoordinates(location)
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
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(context))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}