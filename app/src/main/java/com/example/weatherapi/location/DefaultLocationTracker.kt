package com.example.weatherapi.location

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.example.weatherapi.R
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class DefaultLocationTracker(
    private val activity: Activity,
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    override suspend fun getCurrentLocation(): Location? {

        GpsUtils(activity).turnOnGPS(activity)

        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager =
            application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)


        if (!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission) {
            return null
        }
        //GpsUtils(activity).turnOnGPS()



        return suspendCancellableCoroutine { cont ->
            locationClient.lastLocation.apply {
                val pb = activity.findViewById<ProgressBar>(R.id.pbLoading)
                pb.visibility = View.VISIBLE
                if (isComplete) {
                    if (isSuccessful) {
                        cont.resume(result)
                    } else {
                        cont.resume(null)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    pb.visibility = View.GONE
                    cont.resume(it)
                }
                addOnFailureListener {
                    pb.visibility = View.GONE
                    cont.resume(null)
                }
                addOnCanceledListener {
                    cont.cancel()
                }

            }
        }

    }


}