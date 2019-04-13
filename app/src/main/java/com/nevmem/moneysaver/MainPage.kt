package com.nevmem.moneysaver

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nevmem.moneysaver.data.User
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

        window.enterTransition = null

        app.appComponent.inject(this)

        mainPageNavigation.setOnNavigationItemSelectedListener {
            System.out.println(it.toString())
            when (it.itemId) {
                R.id.historyNavigation -> {
                    if (historyFragment == null)
                        historyFragment = HistoryFragment()
                    historyFragment?.let { fragment ->
                        switchFragment(fragment)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.dashBoardPageNavigation -> {
                    if (dashboardFragment == null)
                        dashboardFragment = DashboardFragment()
                    dashboardFragment?.let { fragment ->
                        switchFragment(fragment)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.newRecordNavigation -> {
                    if (addFragment == null)
                        addFragment = AddFragment()
                    addFragment?.let { fragment ->
                        switchFragment(fragment)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.templatesNavigation -> {
                    if (templatesFragment == null)
                        templatesFragment = TemplatesFragment()
                    templatesFragment?.let { fragment ->
                        switchFragment(fragment)
                    }
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        mainPageNavigation.selectedItemId = R.id.dashBoardPageNavigation
    }

    private fun switchFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.anchor, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun logoutButtonClicked(view: View) {
        User.clearCredentials(this)
        finish()
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