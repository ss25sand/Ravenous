package com.example.ravenous

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.ravenous.ui.shared.SharedViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionAccessCoarseLocationApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

        if (permissionAccessCoarseLocationApproved) {
            // Permission has already been granted
            startRecommendationRequest()
        } else {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MY_PERMISSIONS_REQUEST_COARSE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_COARSE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay!
                    startRecommendationRequest()
                }
                return
            }
        }
    }

    fun startRecommendationRequest() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val geocoder = Geocoder(this)
        val viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation: Location = locationResult.lastLocation
                val addresses: List<Address> = geocoder.getFromLocation(lastLocation.latitude, lastLocation.latitude, 1)
                Log.i(LOG_TAG, (addresses ?: "No last location value").toString())
                viewModel.lastLocation.value = addresses.first()
            }
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            Log.i(LOG_TAG, (location ?: "No last location value").toString())
            if (location != null) {
                val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.latitude, 1)
                viewModel.lastLocation.value = addresses.first()
            } else {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            }
        }
    }
}
