package com.example.accelerometr_test

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import kotlin.math.sqrt
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    lateinit var mAdView : AdView
    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager
    private var PERM_FINE_LOCATION = false
    private var PERM_COARSE_LOCATION = false
    private var PERM_OK = false
    private var Requested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.textView3)
        textView.text = "Hello"

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val gpsSpeed = location.speed
//                updateVelocity(gpsSpeed)
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }
        var tryes = 0
        while (PERM_OK !== true)
        {
            checkPermissions()
            Thread.sleep(1000)
            tryes++
            if(tryes > 60) break
        }
        if (PERM_OK) {
            Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show()
            textView.text = "Perm OK!"
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        } else {
            Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
            Thread.sleep(1000)
            finishAffinity()
        }
    }

    fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if(Requested !== true) {
                Toast.makeText(this, "Waiting for permissions...", Toast.LENGTH_SHORT).show()
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
                    )
                }
            }
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (Requested !== true) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this@MainActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
                    )
                }
            }
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            PERM_FINE_LOCATION = true
        }
        if (ContextCompat.checkSelfPermission(this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            PERM_COARSE_LOCATION = true
        }
        if (PERM_OK == false) {
            if (PERM_COARSE_LOCATION !== true && PERM_FINE_LOCATION !== true) {
            } else {
                PERM_OK = true
            }
        }
        Requested = true
    }

}

