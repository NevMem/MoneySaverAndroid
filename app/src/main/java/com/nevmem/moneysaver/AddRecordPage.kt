package com.nevmem.moneysaver

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.graphics.ColorFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.FragmentActivity
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import android.util.Log.i
import android.view.View
import android.widget.ArrayAdapter
import com.nevmem.moneysaver.exceptions.AddRecordPageViewModel
import kotlinx.android.synthetic.main.add_record_activity.*

class AddRecordPage : FragmentActivity() {
    lateinit var viewModel: AddRecordPageViewModel

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        viewModel = ViewModelProviders.of(this).get(AddRecordPageViewModel::class.java)
        with(window) {
            enterTransition = Fade()
            exitTransition = Fade()
        }
        setContentView(R.layout.add_record_activity)

        viewModel.valueError.observe(this, Observer {
            System.out.println(it)
            if (!it!!.isEmpty()) {
                System.out.println("BAD VALUE")
                recordValueField.background = getDrawable(R.drawable.edit_text_error)
            } else {
                System.out.println("GOOD VALUE")
                recordValueField.background = getDrawable(R.drawable.edit_text)
            }
        })

        viewModel.nameError.observe(this, Observer {
            System.out.println(it)
            if (!it!!.isEmpty()) {
                System.out.println("BAD NAME")
                recordNameField.background = getDrawable(R.drawable.edit_text_error)
            } else {
                System.out.println("GOOD NAME")
                recordNameField.background = getDrawable(R.drawable.edit_text)
            }
        })

        var allTags: ArrayList<String> = ArrayList()
        allTags.add("Food")
        allTags.add("Transport")
        allTags.add("Health")
        tags.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, allTags)

        i("ADD_RECORD_PAGE", "onCreate method was called")
    }

    private fun isValidName(name: String): Boolean { // TODO: Add complex name validation
        if (name.isEmpty())
            return false
        return true
    }

    private fun isValidValue(value: String): Boolean {
        try {
            val doubleValue = value.toDouble()
            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }

    fun addButtonClicked(view: View) {
        System.out.println("We're going to send information to the server")
        val name = recordNameField.text.toString()
        val value = recordValueField.text.toString()

        viewModel.nameError.value = ""
        viewModel.valueError.value = ""

        var valid = true

        if (!isValidName(name)) {
            viewModel.nameError.value = "Bad name"
            valid = false
        }
        if (!isValidValue(value)) {
            viewModel.valueError.value = "Bad value"
            valid = false
        }
        if (!valid) return
    }
}