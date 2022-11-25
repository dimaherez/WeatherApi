package com.example.weatherapi.location

import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

class GpsUtils(context: Context) {
    private val TAG = "GPS"
    private val mContext: Context = context


    private var mSettingClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    private var mLocationManager: LocationManager? = null
    private var mLocationRequest: LocationRequest? = null


    init {
        mLocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

        mSettingClient = LocationServices.getSettingsClient(mContext)

        mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1).build()

        if (mLocationRequest != null) {
            val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(mLocationRequest!!)
            mLocationSettingsRequest = builder.build()
        }
    }

    fun turnOnGPS(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            101
        )

        if (mLocationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            mLocationSettingsRequest?.let {
                mSettingClient?.checkLocationSettings(it)
                    ?.addOnSuccessListener(mContext as Activity) {
                        Log.d(TAG, "turnOnGPS: Already Enabled")
                    }
                    ?.addOnFailureListener { ex ->
                        if ((ex as ApiException).statusCode
                            == LocationSettingsStatusCodes.RESOLUTION_REQUIRED
                        ) {
                            try {
                                val resolvableApiException = ex as ResolvableApiException
                                resolvableApiException.startResolutionForResult(
                                    mContext,
                                    0x1
                                )
                            } catch (e: Exception) {
                                Log.d(TAG, "turnOnGPS: Unable to start default functionality of GPS")
                            }

                        } else {
                            if (ex.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                                val errorMessage =
                                    "Location settings are inadequate, and cannot be " +
                                            "fixed here. Fix in Settings."
                                Log.e(TAG, errorMessage)
                                Toast.makeText(
                                    mContext,
                                    errorMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
            }
        }
    }
}