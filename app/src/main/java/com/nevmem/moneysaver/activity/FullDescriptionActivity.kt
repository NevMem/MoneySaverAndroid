package com.nevmem.moneysaver.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log.i
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.util.ErrorState
import com.nevmem.moneysaver.data.util.LoadingState
import com.nevmem.moneysaver.data.util.SuccessState
import com.nevmem.moneysaver.views.CustomDatePicker
import kotlinx.android.synthetic.main.full_description.*


class FullDescriptionActivity : FragmentActivity() {
    init {
        i("description", "hello from full description activity")
    }

    private val fadeOutTime: Long = 100
    private val fadeInTime: Long = 200

    private var index: Int = 0
    private lateinit var app: App
    private lateinit var viewModel: FullDescriptionActivityViewModel

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.full_description)
        viewModel = ViewModelProviders.of(this).get(FullDescriptionActivityViewModel::class.java)

        i("FDA", "Hello from on create method")
        app = applicationContext as App
        val intentBundle = intent.extras
        if (intentBundle != null && intentBundle.containsKey("index")) {
            index = intentBundle["index"].toString().toInt()
        }

        /* Dagger2 injection */
        app.appComponent.inject(this)

        viewModel.index = index

        setupViewModelObservers()
        setupSaveButtonListener()
        setupDate()
        setupCancelButtonListener()

        Handler().postDelayed({
            fadeInFields()
        }, 100)
    }

    private fun setupDate() {
        editDateButton.setOnClickListener {
            val datePicker = CustomDatePicker(this, viewModel.currentDate())
            val popupWindow = PopupWindow(datePicker, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            popupWindow.showAtLocation(saveChangesButton, Gravity.BOTTOM, 0, 0)
            datePicker.setOnCancel {
                popupWindow.dismiss()
            }
            datePicker.setOnOk {
                viewModel.setDate(it)
                date.text = it.toString()
                popupWindow.dismiss()
            }
        }
    }

    private fun setupSaveButtonListener() {
        saveChangesButton.setOnClickListener {
            viewModel.setName(recordNameField.text.toString())
            viewModel.setWallet(wallet.text.toString())
            viewModel.setTag(tag.text.toString())
            viewModel.setDaily(dailySwitch.isChecked)
            viewModel.save()
        }
    }

    private fun setupCancelButtonListener() {
        cancelButton.setOnClickListener {
            end()
        }
    }

    private fun setupRecord(record: Record) {
        recordNameField.text = record.name
        recordValueField.text = record.value.toString()
        dailySwitch.isChecked = record.daily
        wallet.text = record.wallet
        tag.text = record.tag
        date.text = record.date.toString()
    }

    private fun showError(error: String) {
        // TODO: (do some error showing)
        Snackbar.make(coordinator, error, Snackbar.LENGTH_LONG)
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(success: String){
        // TODO: (do some work stuff)
        Snackbar.make(coordinator, success, Snackbar.LENGTH_LONG)
        Toast.makeText(this, success, Toast.LENGTH_LONG).show()
    }

    private fun end() {
        fadeOutFields()
        Handler(Looper.getMainLooper()).postDelayed({ finishAfterTransition() }, 0)
    }

    override fun onBackPressed() {
        end()
    }

    private fun hideSaveButton() {
        saveChangesButton.visibility = View.GONE
    }

    private fun showSaveButton() {
        saveChangesButton.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
    }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    private fun setupViewModelObservers() {
        viewModel.editingState().observe(this, Observer {
            when (it) {
                null -> {}
                is LoadingState -> {
                    hideSaveButton()
                    showLoading()
                }
                is ErrorState -> {
                    hideLoading()
                    showSaveButton()
                    showError(it.error)
                }
                is SuccessState -> {
                    hideLoading()
                    showSaveButton()
                    if (it.success != null)
                        showSuccess(it.success)
                }
            }
        })
        viewModel.record.observe(this, Observer {
            if (it != null)
                setupRecord(it)
        })
    }

    private fun fadeOutFields() {
        recordNameField.animate().alpha(0f).setDuration(fadeOutTime).start()
        recordValueField.animate().alpha(0f).setDuration(fadeOutTime).start()
        tag.animate().alpha(0f).setDuration(fadeOutTime).start()
        wallet.animate().alpha(0f).setDuration(fadeOutTime).start()
        info_text_1.animate().alpha(0f).setDuration(fadeOutTime).start()
        dailySwitch.animate().alpha(0f).setDuration(fadeOutTime).start()
        cancelButton.animate().alpha(0f).setDuration(fadeOutTime).start()
        saveChangesButton.animate().alpha(0f).setDuration(fadeOutTime).start()
        loading.animate().alpha(0f).setDuration(fadeOutTime).start()
    }

    private fun fadeInFields() {
        recordNameField.animate().alpha(1f).setDuration(fadeInTime).start()
        recordValueField.animate().alpha(1f).setDuration(fadeInTime).start()
        tag.animate().alpha(1f).setDuration(fadeInTime).start()
        wallet.animate().alpha(1f).setDuration(fadeInTime).start()
        info_text_1.animate().alpha(1f).setDuration(fadeInTime).start()
        dailySwitch.animate().alpha(1f).setDuration(fadeInTime).start()
        cancelButton.animate().alpha(1f).setDuration(fadeInTime).start()
        saveChangesButton.animate().alpha(1f).setDuration(fadeInTime).start()
        loading.animate().alpha(1f).setDuration(fadeInTime).start()
    }
}