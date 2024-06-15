package cat.fpp.tiporhang.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Looper
import android.view.MotionEvent
import android.view.View.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cat.fpp.tiporhang.R

@SuppressLint("CustomSplashScreen")
//Unnecessary splash screen activity, android has it by default, just for learning purposes
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var intro: TextView
    private lateinit var splashText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        intro = findViewById(R.id.intro_text)
        splashText = findViewById(R.id.displayWord)
        splashText.text = "\n\n\n\n Un joc per al curs de Desenvolupament Android Avançat 2024 "
        intro.typeface = Typeface.create("sans-serif-condensed-bold", Typeface.BOLD)
        intro.startAnimation(AnimationUtils.loadAnimation(this, R.anim.splash_animation))
        val looper = Looper.myLooper()!!
        android.os.Handler(looper).postDelayed({
            splashText.visibility = VISIBLE
            splashText.typeface = Typeface.create("sans-serif-condensed-bold", Typeface.BOLD)
            splashText.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.splash_animation
                )
            )
            android.os.Handler(looper).postDelayed({
                splashText.text = buildString {
                    append(splashText.text.toString())
                    append("\n\n Desenvolupat per:\n")
                    append("                Dídac Llorens Bravo")
                }
                android.os.Handler(looper).postDelayed({
                    gameStart()
                }, 2000)// 2 Segundos para las instrucciones
            }, 1500) // 3 segundos de duración para la splash screen
        }, 1500)


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        gameStart()
        return super.onTouchEvent(event)

    }

    private fun gameStart() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("frame_title", title)
        startActivity(intent)
        finish()
    }
}