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
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import kotlin.math.sqrt
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var velocityTextView: TextView
    lateinit var mAdView : AdView
    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager
    private lateinit var textView: TextView
    private lateinit var StartButton: Button
    private lateinit var ResetButton: Button
    private lateinit var PauseButton: Button
    private var WORKING = false
    private var ONPAUSE = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        itemsInit()
        buttonsFunctionalityInit()
        adInit()

    }

    fun itemsInit()
    {
        WORKING = false
        ONPAUSE = false
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
            Toast.makeText(this@MainActivity, "Start meassuring.", Toast.LENGTH_SHORT).show()
            WORKING = !WORKING
            StartButton.setEnabled(!WORKING)
            ResetButton.setEnabled(WORKING)
            PauseButton.setEnabled(WORKING)
        }
        ResetButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Reset meassuring.", Toast.LENGTH_SHORT).show()
            WORKING = false
            ONPAUSE = false
            PauseButton.text = "Pause"
            StartButton.setEnabled(!WORKING)
            ResetButton.setEnabled(WORKING)
            PauseButton.setEnabled(WORKING)
        }
        PauseButton.setOnClickListener {

            ONPAUSE = !ONPAUSE
            if (ONPAUSE) {
                PauseButton.text = "Resume"
                Toast.makeText(this@MainActivity, "Resume meassuring.", Toast.LENGTH_SHORT).show()
            } else {
                PauseButton.text = "Pause"
                Toast.makeText(this@MainActivity, "Pause meassuring.", Toast.LENGTH_SHORT).show()
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
}

