package com.nevmem.moneysaver.app.activity

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import androidx.appcompat.app.AppCompatActivity
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.activity.adapters.ManageTagsWalletsAdapter
import com.nevmem.moneysaver.auth.User
import com.nevmem.moneysaver.auth.UserHolder
import kotlinx.android.synthetic.main.settings_activity.*
import javax.inject.Inject

class SettingsActivity : AppCompatActivity() {
    @Inject
    lateinit var userHolder: com.nevmem.moneysaver.auth.UserHolder

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        window.enterTransition = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_right)
        window.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_left)

        val pManagerInfo = packageManager.getPackageInfo(packageName, 0)
        versionCode.text = "Version: ${pManagerInfo.versionName}"

        (application as App).appComponent.inject(this)

        setupTagsAdapter()

        devSettingsButton.setOnClickListener {
            openDevSettings()
        }

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun openDevSettings() {
        val intent = Intent(this, DevSettingsActivity::class.java)
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun setupTagsAdapter() {
        manageTags.setAdapter(ManageTagsWalletsAdapter(this, this, application as App))
    }

    private fun logout() {
        userHolder.ready = false
        com.nevmem.moneysaver.auth.User.clearCredentials(this)
        val intent = Intent(this, SplashScreen::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}