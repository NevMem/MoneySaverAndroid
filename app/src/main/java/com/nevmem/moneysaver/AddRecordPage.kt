package com.nevmem.moneysaver

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.transition.Explode
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import android.util.Log.i
import android.view.View
import android.widget.ArrayAdapter
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.nevmem.moneysaver.exceptions.AddRecordPageViewModel
import com.nevmem.moneysaver.structure.Callback
import kotlinx.android.synthetic.main.add_record_activity.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class AddRecordPage : FragmentActivity() {
    lateinit var viewModel: AddRecordPageViewModel

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        val application = applicationContext as App
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

        viewModel.error.observe(this, Observer {
            if (!it!!.isEmpty()) {
                headerText.text = it
                headerText.setTextColor(ContextCompat.getColor(this, R.color.dangerColor))
            } else {
                headerText.setTextColor(ContextCompat.getColor(this, R.color.mainFontColor))
            }
        })
        viewModel.success.observe(this, Observer {
            if (!it!!.isEmpty()) {
                headerText.text = it
                headerText.setTextColor(ContextCompat.getColor(this, R.color.specialColor))
            } else {
                headerText.setTextColor(ContextCompat.getColor(this, R.color.mainFontColor))
            }
        })
        viewModel.loading.observe(this, Observer {
            if (it!!) {
                loadingBar.visibility = View.VISIBLE
                headerText.text = "Processing..."
                headerText.setTextColor(ContextCompat.getColor(this, R.color.mainFontColor))
            } else {
                loadingBar.visibility = View.GONE
                headerText.text = getString(R.string.addRecordHeader)
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

        tags.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, application.tags)
        chooseWallet.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, application.wallets)

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

    private fun sendAddRequest(name: String, value: String, tag: String?, wallet: String?) {
        var app = applicationContext as App
        viewModel.loading.value = true
        viewModel.error.value = ""
        viewModel.success.value = ""
        app.makeAddRequest(name, value, tag, wallet, Callback {
            viewModel.loading.value = false
            viewModel.success.value = it
        }, Callback {
            viewModel.loading.value = false
            viewModel.error.value = it
        })
    }

    fun addButtonClicked(view: View) {
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
        sendAddRequest(name, value, tags.selectedItem.toString(), chooseWallet.selectedItem.toString())
    }
}