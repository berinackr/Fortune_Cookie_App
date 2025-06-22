package com.bernackr.fortunecookie

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Cookie Rain Animasyonu Başlat
        startCookieRain()

        // 2 saniye sonra MainActivity'e geç
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    private fun startCookieRain() {
        val container = findViewById<RelativeLayout>(R.id.splash_container)
        val screenWidth = resources.displayMetrics.widthPixels
        val cookieCount = 25

        for (i in 1..cookieCount) {
            val imageView = ImageView(this).apply {
                setImageResource(R.drawable.cookie_closed) // kurabiye logon
                layoutParams = RelativeLayout.LayoutParams(100, 100).apply {
                    leftMargin = (0 until screenWidth).random()
                    topMargin = -200
                }
            }

            container.addView(imageView)

            val animation = TranslateAnimation(
                0f, 0f,
                0f, 2000f
            ).apply {
                duration = (1000..3000).random().toLong()
                interpolator = AccelerateInterpolator()
            }

            imageView.startAnimation(animation)
        }
    }
}
