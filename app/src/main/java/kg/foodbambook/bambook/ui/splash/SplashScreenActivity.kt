package kg.foodbambook.kg.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kg.foodbambook.kg.R
import kg.foodbambook.kg.ui.auth.AuthActivity

class SplashScreenActivity : AppCompatActivity() {
    val SPLASH_DISPLAY_LENGTH = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        startGifAnim()

        Handler().postDelayed({

            startActivity(Intent(this@SplashScreenActivity, AuthActivity::class.java))
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }


    private fun startGifAnim(){
        Glide.with(this)
            .load(R.drawable.animation)
            .into(findViewById(R.id.animated_image))
    }
}
