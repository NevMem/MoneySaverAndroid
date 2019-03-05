package com.nevmem.moneysaver

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.fragments.AddFragment
import com.nevmem.moneysaver.fragments.HistoryFragment
import com.nevmem.moneysaver.fragments.DashboardFragment
import com.nevmem.moneysaver.fragments.TemplatesFragment
import com.nevmem.moneysaver.structure.Callback
import kotlinx.android.synthetic.main.add_record_activity.*
import kotlinx.android.synthetic.main.login_page.*
import kotlinx.android.synthetic.main.main_page_layout.*
import kotlinx.android.synthetic.main.user_profile.*
import java.lang.Exception
import java.util.ArrayList

class MainPage : AppCompatActivity() {
    lateinit var app: App
    lateinit var viewModel: MainPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_layout)
        viewModel = ViewModelProviders.of(this).get(MainPageViewModel::class.java)
        window.statusBarColor = Color.parseColor("#101010")
        app = applicationContext as App

        app.loadAll()

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