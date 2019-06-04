package com.nevmem.moneysaver.activity

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.activity.adapters.MainPageViewPager2Adapter
import com.nevmem.moneysaver.fragments.AddFragment
import com.nevmem.moneysaver.fragments.DashboardFragment
import com.nevmem.moneysaver.fragments.HistoryFragment
import com.nevmem.moneysaver.fragments.TemplatesFragment
import kotlinx.android.synthetic.main.main_page_layout.*

class MainPage : AppCompatActivity() {
    lateinit var app: App

    private var historyFragment: HistoryFragment? = null
    private var addFragment: AddFragment? = null
    private var templatesFragment: TemplatesFragment? = null
    private var dashboardFragment: DashboardFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_layout)
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

        mainPageNavigation.selectedItemId = R.id.dashboardPageNavigation
    }

    fun onLogout() {
        finish()
    }

    private fun switchFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.anchor, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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