package com.example.youtubepoc.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.youtubepoc.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Splash screen
        lifecycleScope.launch {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            delay(2000)
            startActivity(intent)
            finish()
        }
    }
}