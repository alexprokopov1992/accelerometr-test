package com.example.accelerometr_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.admanager.AdManagerAdRequest

class MainActivity : AppCompatActivity() {

    lateinit var mAdManagerAdView : AdManagerAdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}

        mAdManagerAdView = findViewById(R.id.adManagerAdView)
        val adRequest = AdManagerAdRequest.Builder().build()
        mAdManagerAdView.loadAd(adRequest)
    }
}