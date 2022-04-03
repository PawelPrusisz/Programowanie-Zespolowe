package com.example.projekt_aplikacje_2.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.CancellationTokenSource

class GpsManager private constructor(context: Context){

    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            for (callback in listeners) {
                callback(result.lastLocation)
            }
        }
    }
    private val listeners = mutableListOf<(Location) -> Unit>()
    private var currentLocation = Location(LocationManager.GPS_PROVIDER)

    companion object {

        private var instance : GpsManager? = null

        fun getGpsManager(context: Context) : GpsManager {
            if (instance == null) {
                instance = GpsManager(context)
                instance!!.addLocationChangeListener {
                    instance!!.currentLocation = it
                }
            }
            return instance as GpsManager
        }
    }

    fun requestAllPermissions(activity: Activity) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)

        if (!checkAllPermissions(activity)) {
            ActivityCompat.requestPermissions(activity, permissions, 0)
        }
    }

    private fun checkAllPermissions(context : Context) : Boolean {
        return checkSinglePermission(context, Manifest.permission.ACCESS_FINE_LOCATION) &&
                checkSinglePermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun checkSinglePermission(context : Context, permission : String) : Boolean {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback : (Location) -> Unit) {
        Log.i("LOCATION", "Requested current location")
//        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
//            .addOnSuccessListener(callback)
        callback(currentLocation)
    }

    @SuppressLint("MissingPermission")
    fun addLocationChangeListener(callback : (Location) -> Unit) : Int {
        listeners.add(callback)
        if (listeners.size == 1) {
            val locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(0.0f)
                .setInterval(500)
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper())
        }
        return listeners.lastIndex
    }

    fun removeLocationChangeListener(callbackId : Int) {

        listeners.clear()
//        if (callbackId < listeners.size) {
//            listeners.removeAt(callbackId)
//        }
//
//        if (listeners.size == 0) {
//            fusedLocationClient.removeLocationUpdates(locationCallback)
//         }
    }

}