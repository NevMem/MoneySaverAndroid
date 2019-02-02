package com.nevmem.moneysaver

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log.i
import kotlinx.android.synthetic.main.full_description.*

class FullDescriptionActivity: FragmentActivity() {
    init {
        i("description", "hello from full description activity")
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.full_description)
        i("description", "Hello from on create method")
        val app = applicationContext as App
        val index = intent.extras["index"].toString().toInt()
        recordNameField.text = app.records[index].name
        recordValueField.text = app.records[index].value.toString()
    }
}