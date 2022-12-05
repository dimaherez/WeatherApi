package com.example.weatherapi.location

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat


object Permissions {
    private const val permissionId = 2

    fun permissionsGranted(context: Context?): Boolean{
        return (checkPermissions(context) && isLocationEnabled(context))
    }

    private fun isLocationEnabled(context: Context?): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(LOCATION_SERVICE) as LocationManager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        ) return true
        turnOnLocation(context)
        return false
    }

    private fun checkPermissions(context: Context?): Boolean {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        requestPermissions(context)
        return false
    }

    private fun turnOnLocation(context: Context?) {
        Toast.makeText(context, "Please turn on location", Toast.LENGTH_SHORT)
            .show()
        context?.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun requestPermissions(context: Context?) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

}