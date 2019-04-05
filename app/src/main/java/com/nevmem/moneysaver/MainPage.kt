package com.nevmem.moneysaver

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.dagger.components.AppComponent
import com.nevmem.moneysaver.dagger.components.DaggerAppComponent
import com.nevmem.moneysaver.dagger.modules.DataModule
import com.nevmem.moneysaver.dagger.modules.NetworkModule
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.fragments.AddFragment
import com.nevmem.moneysaver.fragments.DashboardFragment
import com.nevmem.moneysaver.fragments.HistoryFragment
import com.nevmem.moneysaver.fragments.TemplatesFragment
import kotlinx.android.synthetic.main.main_page_layout.*

class MainPage : AppCompatActivity() {
    lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_page_layout)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)
        app = applicationContext as App

        app.appComponent.inject(this)

        mainPageNavigation.setOnNavigationItemSelectedListener {
            System.out.println(it.toString())
            when (it.itemId) {
                R.id.historyNavigation -> {
                    val fragment: Fragment = HistoryFragment()
                    switchFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.dashBoardPageNavigation -> {
                    val fragment: Fragment = DashboardFragment()
                    switchFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.newRecordNavigation -> {
                    val fragment: Fragment = AddFragment()
                    switchFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.templatesNavigation -> {
                    val fragment = TemplatesFragment()
                    switchFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        mainPageNavigation.selectedItemId = R.id.dashBoardPageNavigation
    }

    fun switchFragment(fragment: Fragment) {
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