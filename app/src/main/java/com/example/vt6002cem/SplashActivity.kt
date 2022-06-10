package com.example.vt6002cem

import android.Manifest
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationRequest

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java ))
            finish()
        }, 100)


    }


}