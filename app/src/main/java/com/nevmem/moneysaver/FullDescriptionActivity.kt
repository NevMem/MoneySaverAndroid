package com.nevmem.moneysaver

import android.app.Activity
import android.app.SharedElementCallback
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.transition.Explode
import android.transition.Transition
import android.transition.TransitionListenerAdapter
import android.util.Log.i
import android.view.View
import kotlinx.android.synthetic.main.full_description.*
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter
import android.support.v4.view.ViewCompat



class FullDescriptionActivity: FragmentActivity() {
    init {
        i("description", "hello from full description activity")
    }

    private var index: Int = 0
    private lateinit var app: App

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.full_description)
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundColor)

        i("description", "Hello from on create method")
        app = applicationContext as App
        index = intent.extras["index"].toString().toInt()

        recordNameField.text = app.records[index].name
        recordValueField.text = app.records[index].value.toString()

        window.sharedElementEnterTransition.duration = 200
        tag.text = app.records[index].tags[0]
        wallet.text = app.records[index].wallet
        Handler().postDelayed({
            fadeInValues()
        }, 400)
    }

    fun fadeInValues() {
        tag.animate().alpha(1f).setDuration(200).start()
        wallet.animate().alpha(1f).setDuration(200).start()
    }
}