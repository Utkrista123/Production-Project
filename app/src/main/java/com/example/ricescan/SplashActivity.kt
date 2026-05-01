package com.example.ricescan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val splashDelayMs = 1400L
    private val handler = Handler(Looper.getMainLooper())
    private val launchMainRunnable = Runnable {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        playIntroAnimation()
        handler.postDelayed(launchMainRunnable, splashDelayMs)
    }

    override fun onDestroy() {
        handler.removeCallbacks(launchMainRunnable)
        super.onDestroy()
    }

    private fun playIntroAnimation() {
        val group = findViewById<View>(R.id.brandGroup)
        val logo = findViewById<View>(R.id.splashLogo)
        val title = findViewById<View>(R.id.splashTitle)
        val tagline = findViewById<View>(R.id.splashTagline)

        group.alpha = 0f
        group.scaleX = 0.92f
        group.scaleY = 0.92f
        group.translationY = 36f
        group.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationY(0f)
            .setDuration(520L)
            .setInterpolator(OvershootInterpolator(1.15f))
            .start()

        logo.alpha = 0f
        logo.scaleX = 0.75f
        logo.scaleY = 0.75f
        logo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setStartDelay(180L)
            .setDuration(380L)
            .start()

        title.alpha = 0f
        title.translationY = 10f
        title.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(280L)
            .setDuration(300L)
            .start()

        tagline.alpha = 0f
        tagline.translationY = 8f
        tagline.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(360L)
            .setDuration(280L)
            .start()
    }
}
