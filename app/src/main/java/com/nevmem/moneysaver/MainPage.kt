package com.nevmem.moneysaver

import android.app.Application
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.nevmem.moneysaver.data.User
import com.nevmem.moneysaver.fragments.HistoryFragment
import com.nevmem.moneysaver.fragments.HomeFragment
import kotlinx.android.synthetic.main.main_page_layout.*

class MainPage : AppCompatActivity() {
    lateinit var app: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_layout)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)
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
                R.id.homePageNavigation -> {
                    val fragment: Fragment = HomeFragment()
                    switchFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

        mainPageNavigation.selectedItemId = R.id.homePageNavigation
    }

    fun reloadButtonClicked(view: View) {
        System.out.println("Reload button was clicked")
    }

    fun logoutButtonClicked(view: View) {
        User.clearCredentials(this)
        finish()
    }

    fun switchFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.anchor, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}