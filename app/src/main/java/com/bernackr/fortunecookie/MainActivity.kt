package com.bernackr.fortunecookie

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import org.json.JSONArray
import java.io.InputStream

data class FortunePair(val tr: String, val en: String)

class MainActivity : AppCompatActivity() {

    private lateinit var imgCookie: ImageView
    private lateinit var tvFortune: TextView
    private lateinit var langSwitch: SwitchCompat

    private var fortunes: List<FortunePair> = emptyList()
    private var isBroken = false
    private var isAnimating = false  // ✅ Tıklamayı kontrol için eklendi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val langCode = LocaleHelper.getLanguage(this)
        LocaleHelper.setLocale(this, langCode)

        setContentView(R.layout.activity_main)

        imgCookie = findViewById(R.id.imgCookie)
        tvFortune = findViewById(R.id.tvFortune)
        langSwitch = findViewById(R.id.langSwitch)

        langSwitch.isChecked = langCode == "en"

        langSwitch.setOnCheckedChangeListener { _, isChecked ->
            val newLang = if (isChecked) "en" else "tr"
            LocaleHelper.setLocale(this, newLang)
            val intent = Intent(this, MainActivity::class.java)
            finish()
            startActivity(intent)
        }

        fortunes = loadFortunesFromJson()

        imgCookie.setOnClickListener {
            if (isAnimating) return@setOnClickListener  // ✅ Animasyon sırasında tıklamayı engelle

            isAnimating = true

            if (!isBroken) {
                // ✅ Kurabiyeyi kırma animasyonu
                imgCookie.animate().rotationBy(20f).setDuration(300).withEndAction {
                    imgCookie.setImageResource(R.drawable.cookie_broken)

                    val fortune = fortunes.random()
                    val fortuneText = if (LocaleHelper.getLanguage(this) == "en") fortune.en else fortune.tr

                    tvFortune.text = fortuneText
                    tvFortune.alpha = 0f
                    tvFortune.visibility = View.VISIBLE
                    tvFortune.animate().alpha(1f).setDuration(1000).withEndAction {
                        isAnimating = false  // ✅ Animasyon tamamlandıktan sonra tıklama açılır
                    }.start()
                }

                isBroken = true

            } else {
                // ✅ Eski haline dönme animasyonu
                tvFortune.animate().alpha(0f).setDuration(250).withEndAction {
                    tvFortune.visibility = View.GONE
                }.start()

                imgCookie.animate().rotationBy(-20f).setDuration(300).withEndAction {
                    imgCookie.setImageResource(R.drawable.cookie_closed)
                    isAnimating = false  // ✅ Animasyon tamamlandıktan sonra tıklama açılır
                }

                isBroken = false
            }
        }
    }

    private fun loadFortunesFromJson(): List<FortunePair> {
        val fortuneList = mutableListOf<FortunePair>()
        try {
            val inputStream: InputStream = assets.open("fortunes_tr_en.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val trText = obj.optString("tr", "")
                val enText = obj.optString("en", "")
                if (trText.isNotEmpty() && enText.isNotEmpty()) {
                    fortuneList.add(FortunePair(tr = trText, en = enText))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Veriler yüklenirken bir hata oluştu", Toast.LENGTH_LONG).show()
        }
        return fortuneList
    }
}
