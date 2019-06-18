package com.nevmem.moneysaver.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.data.UserHolder
import kotlinx.android.synthetic.main.settings_activity.*
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {
    @Inject
    lateinit var userHolder: UserHolder


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_right)
        window.enterTransition = enterTransition

        val pManagerInfo = packageManager.getPackageInfo(packageName, 0)
        versionCode.text = "Version: ${pManagerInfo.versionName}"

        (application as App).appComponent.inject(this)

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        userHolder.ready = false
        User.clearCredentials(this)
        val intent = Intent(this, LoginPageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}