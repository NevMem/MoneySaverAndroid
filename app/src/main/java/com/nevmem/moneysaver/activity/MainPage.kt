package com.nevmem.moneysaver.activity

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.activity.adapters.MainPageViewPager2Adapter
import kotlinx.android.synthetic.main.main_page_layout.*

class MainPage : AppCompatActivity() {
    lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_layout)
        window.exitTransition = TransitionInflater.from(this).inflateTransition(R.transition.activity_slide_left)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)
        app = applicationContext as App

        app.appComponent.inject(this)
        val adapter = MainPageViewPager2Adapter(lifecycle, supportFragmentManager)
        anchor.adapter = adapter
        anchor.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> mainPageNavigation.selectedItemId = R.id.dashboardPageNavigation
                    1 -> mainPageNavigation.selectedItemId = R.id.templatesNavigation
                    2 -> mainPageNavigation.selectedItemId = R.id.newRecordNavigation
                    else -> mainPageNavigation.selectedItemId = R.id.historyNavigation
                }
            }
        })

        mainPageNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dashboardPageNavigation -> {
                    anchor.currentItem = 0
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.templatesNavigation -> {
                    anchor.currentItem = 1
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.newRecordNavigation -> {
                    anchor.currentItem = 2
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.historyNavigation -> {
                    anchor.currentItem = 3
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        anchor.isUserInputEnabled = false

        mainPageNavigation.selectedItemId = R.id.dashboardPageNavigation
    }

    private fun showDefaultToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 40)
        toast.show()
    }

    override fun onBackPressed() {
        showDefaultToast("Press logout button if you want get out from application")
    }
}