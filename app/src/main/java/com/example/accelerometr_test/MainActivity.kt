package com.example.accelerometr_test

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), LocationListener {
    lateinit var mAdView : AdView
    private lateinit var locationManager: LocationManager
    private lateinit var textView: TextView
    private lateinit var tvGpsLocation: TextView
    private lateinit var StartButton: Button
    private lateinit var ResetButton: Button
    private lateinit var PauseButton: Button
    private var textGps = "Loading"
    private var WORKING = false
    private var ONPAUSE = false
    private var GPSPERMISSION = false
    private val locationPermissionCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        title = "SpeedometerApp"
        itemsInit()
        buttonsFunctionalityInit()
        adInit()

    }

    fun itemsInit()
    {
        WORKING = false
        ONPAUSE = false
        tvGpsLocation = findViewById(R.id.gpsView)
        tvGpsLocation.text = "Stopped"
        textView = findViewById(R.id.textView3)
        StartButton = findViewById(R.id.startButton)
        ResetButton = findViewById(R.id.resetButton)
        PauseButton = findViewById(R.id.pauseButton)
        StartButton.setEnabled(true)
        ResetButton.setEnabled(false)
        PauseButton.setEnabled(false)
    }

    fun buttonsFunctionalityInit()
    {
        StartButton.setOnClickListener {
            WORKING = !WORKING
            StartButton.setEnabled(!WORKING)
            ResetButton.setEnabled(WORKING)
            PauseButton.setEnabled(WORKING)
            if (WORKING) {
                tvGpsLocation.text = "Loading"
                getLocation()
                if(GPSPERMISSION) setLastLocation()
            }
        }
        ResetButton.setOnClickListener {
            if(GPSPERMISSION) {
                Toast.makeText(this@MainActivity, "Reset meassuring.", Toast.LENGTH_SHORT).show()
                WORKING = false
                ONPAUSE = false
                PauseButton.text = "Pause"
                tvGpsLocation.text = "Stopped"
                StartButton.setEnabled(!WORKING)
                ResetButton.setEnabled(WORKING)
                PauseButton.setEnabled(WORKING)
            } else {
                tvGpsLocation.text = "Permission error"
                Toast.makeText(this@MainActivity, "No permissions!.", Toast.LENGTH_SHORT)
            }
        }
        PauseButton.setOnClickListener {
            ONPAUSE = !ONPAUSE
            if(GPSPERMISSION) {
                if (ONPAUSE) {
                    PauseButton.text = "Resume"
                    Toast.makeText(this@MainActivity, "Resume meassuring.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    PauseButton.text = "Pause"
                    Toast.makeText(this@MainActivity, "Pause meassuring.", Toast.LENGTH_SHORT)
                        .show()
                }
                if (ONPAUSE) {
                    tvGpsLocation.text = "PAUSED"
                } else {
                    tvGpsLocation.text = "Loading"
                    if(GPSPERMISSION) setLastLocation()
                }
            } else {
                tvGpsLocation.text = "Permission error"
                Toast.makeText(this@MainActivity, "No permissions!.", Toast.LENGTH_SHORT)
            }
        }
    }

    fun adInit()
    {
        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            GPSPERMISSION = true
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
        if(!GPSPERMISSION) tvGpsLocation.text = "Waiting for permission"
    }
    override fun onLocationChanged(location: Location) {
        if(GPSPERMISSION) {
            if (!ONPAUSE && WORKING)
                textGps = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
            tvGpsLocation.text = textGps
        } else {
            tvGpsLocation.text = "Waiting for permission"
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                tvGpsLocation.text = "Loading"
                setLastLocation()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                tvGpsLocation.text = "Permission error"
            }
        }
    }

    private fun getCurrentLocation(): Location? {
        // Get the LocationManager
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Get the last known location from the GPS provider
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    private fun setLastLocation() {
        var loc = getCurrentLocation()
        if (loc !== null) {
            textGps = "Latitude: " + loc.latitude + " , Longitude: " + loc.longitude
            tvGpsLocation.text = textGps
        }
    }
}

