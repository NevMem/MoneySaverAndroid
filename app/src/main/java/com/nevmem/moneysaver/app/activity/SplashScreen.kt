package com.nevmem.moneysaver.app.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.auth.UserHolder
import kotlinx.android.synthetic.main.splash_screen.*
import javax.inject.Inject

class SplashScreen : AppCompatActivity() {
    @Inject
    lateinit var userHolder: UserHolder

    companion object {
        const val SPLASH_SCREEN_DELAY = 700L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val greetingAnimator = AnimatorInflater.loadAnimator(this, R.animator.fade_in)
        greetingAnimator.setTarget(greeting)
        greetingAnimator.start()

        val logoAnimator = AnimatorInflater.loadAnimator(this, R.animator.logo_animator) as AnimatorSet
        logoAnimator.setTarget(app_logo)
        logoAnimator.start()

        with (applicationContext as App) {
            appComponent.inject(this@SplashScreen)
        }
    }

    private fun navigate() {
        if (userHolder.ready) {
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper())
            .postDelayed({
                navigate()
            }, SPLASH_SCREEN_DELAY)
    }
}