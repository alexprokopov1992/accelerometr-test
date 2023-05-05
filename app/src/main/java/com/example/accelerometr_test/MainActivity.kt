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
import android.os.Build
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Chronometer
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity(), LocationListener {
    lateinit var mAdView : AdView
    private lateinit var locationManager: LocationManager
    private lateinit var textView: TextView
    private lateinit var tvGpsLocation: TextView
    private lateinit var StartButton: Button
    private lateinit var ResetButton: Button
    private lateinit var PauseButton: Button
    private lateinit var chronometer: Chronometer
    private lateinit var Velocity: TextView
    private lateinit var Distance: TextView
    private var distance: Float = 0.0F
    private var messureTime: Long = 0
    private var gpsSpeed: Float = 0.0F
    private var elapsedTime : Long = 0
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
        chronometer = findViewById(R.id.chronoMeter)
        Velocity = findViewById(R.id.velocity)
        Distance = findViewById(R.id.distance)
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
                if(GPSPERMISSION)
                {
                    setLastLocation()
                    chronometer.base = SystemClock.elapsedRealtime()
                    chronometer.start()
                    messureTime = SystemClock.elapsedRealtime()
                    distance = 0.0f
                }
            }
        }
        ResetButton.setOnClickListener {
            if(GPSPERMISSION) {
                Toast.makeText(this@MainActivity, "Reset meassuring.", Toast.LENGTH_SHORT).show()
                WORKING = false
                ONPAUSE = false
                chronometer.stop()
                chronometer.base = SystemClock.elapsedRealtime()
                PauseButton.text = "Pause"
                tvGpsLocation.text = "Stopped"
                StartButton.setEnabled(!WORKING)
                ResetButton.setEnabled(WORKING)
                PauseButton.setEnabled(WORKING)
                Velocity.visibility = View.INVISIBLE
                Distance.visibility = View.INVISIBLE
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
                    elapsedTime = chronometer.getBase() - SystemClock.elapsedRealtime()
                    chronometer.stop()
                    tvGpsLocation.text = "PAUSED"
                } else {
                    chronometer.setBase(SystemClock.elapsedRealtime() + elapsedTime)
                    chronometer.start()
                    tvGpsLocation.text = "Loading"
                    if(GPSPERMISSION) setLastLocation()
                    messureTime = SystemClock.elapsedRealtime()
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
            if (!ONPAUSE && WORKING) {
                val timePassed:Float = (SystemClock.elapsedRealtime() - messureTime).toFloat() / 1000.0f
                messureTime = SystemClock.elapsedRealtime()
                val prevSpeed:Float = gpsSpeed / 3.6f
                distance += timePassed * prevSpeed
                gpsSpeed = location.speed * 3.6f
                Distance.text = distance.toString() + " m"
                Velocity.text = gpsSpeed.toString() + " km/h"
                textGps = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
            }
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
                messureTime = SystemClock.elapsedRealtime()
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
            gpsSpeed = loc.speed * 3.6f
            Velocity.text = gpsSpeed.toString() + " km/h"
            Distance.text = distance.toString() + " m"
            Velocity.visibility = View.VISIBLE
            Distance.visibility = View.VISIBLE
            textGps = "Latitude: " + loc.latitude + " , Longitude: " + loc.longitude
            tvGpsLocation.text = textGps
        }
    }
}

