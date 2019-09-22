package com.nevmem.moneysaver.app.activity

import android.animation.AnimatorInflater
import android.app.Activity
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
import com.nevmem.moneysaver.app.activity.viewModels.FullDescriptionActivityViewModel
import com.nevmem.moneysaver.common.data.Record
import com.nevmem.moneysaver.app.data.util.ErrorState
import com.nevmem.moneysaver.app.data.util.LoadingState
import com.nevmem.moneysaver.app.data.util.SuccessState
import com.nevmem.moneysaver.app.views.CustomDatePicker
import com.nevmem.moneysaver.app.views.CustomTimePicker
import kotlinx.android.synthetic.main.full_description.*


class FullDescriptionActivity : FragmentActivity() {
    init {
        i("description", "hello from full description activity")
    }

    private val fadeOutTime: Long = 100
    private val fadeInTime: Long = 200

    private var id: String = ""
    private lateinit var app: App
    private lateinit var viewModel: FullDescriptionActivityViewModel

    private var popupWindow: PopupWindow? = null

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.full_description)
        viewModel = ViewModelProviders.of(this).get(FullDescriptionActivityViewModel::class.java)

        i("FDA", "Hello from on create method")
        app = applicationContext as App
        val intentBundle = intent.extras
        if (intentBundle != null && intentBundle.containsKey("id")) {
            id = intentBundle["id"].toString()
        }

        /* Dagger2 injection */
        app.appComponent.inject(this)

        viewModel.id = id

        setupViewModelObservers()
        setupSaveButtonListener()
        setupDate()
        setupCancelButtonListener()

        Handler().postDelayed({
            fadeInFields()
        }, 100)
    }

    private fun setupListeners() {
        dailySwitch.setOnCheckedChangeListener { _, isChecked -> viewModel.setDaily(isChecked) }
    }

    private fun setupChangers() {
        recordNameField.setOnTextChanged {
            viewModel.setName(it)
        }
        recordValueField.setOnTextChanged {
            try {
                val value = it.toDouble()
                viewModel.setValue(value)
            } catch (e: NumberFormatException) {
            }
        }
    }

    private fun animateInPopupContent() {
        popupWindow?.let {
            val animator = AnimatorInflater.loadAnimator(this, R.animator.slide_in_up)
            animator.setTarget(it.contentView)
            animator.duration = 200
            animator.start()
        }
    }

    private fun animateOutPopupContentAndDismissPopupWindow() {
        popupWindow?.let {
            val animator = AnimatorInflater.loadAnimator(this, R.animator.slide_out_down)
            animator.setTarget(it.contentView)
            animator.duration = 200
            animator.start()
            Handler(Looper.getMainLooper()).postDelayed({
                popupWindow?.dismiss()
            }, 200)
        }
    }

    private fun setupDate() {
        editDateButton.setOnClickListener {
            val datePicker = CustomDatePicker(this, viewModel.currentDate())
            if (popupWindow == null) {
                popupWindow =
                    PopupWindow(datePicker, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            } else {
                popupWindow?.contentView = datePicker
            }
            popupWindow?.let {
                it.showAtLocation(saveChangesButton, Gravity.BOTTOM, 0, 0)
                animateInPopupContent()
            }
            datePicker.setOnCancel {
                animateOutPopupContentAndDismissPopupWindow()
            }
            datePicker.setOnOk { year, month, day ->
                run {
                    viewModel.setYear(year)
                    viewModel.setMonth(month)
                    viewModel.setDay(day)
                    animateOutPopupContentAndDismissPopupWindow()
                }
            }
        }

        editTimeButton.setOnClickListener {
            val timePicker = CustomTimePicker(this, viewModel.currentDate())
            if (popupWindow == null) {
                popupWindow =
                    PopupWindow(timePicker, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            } else {
                popupWindow?.contentView = timePicker
            }
            popupWindow?.let {
                it.isClippingEnabled = false
                it.elevation = 100f
                it.showAtLocation(saveChangesButton, Gravity.CENTER, 0, 0)
                animateInPopupContent()
            }
            timePicker.setOnOk {
                viewModel.setHour(it.hour)
                viewModel.setMinute(it.minute)
                animateOutPopupContentAndDismissPopupWindow()
            }
            timePicker.setOnCancel {
                animateOutPopupContentAndDismissPopupWindow()
            }
        }
    }

    private fun setupSaveButtonListener() {
        saveChangesButton.setOnClickListener {
            viewModel.save()
        }
    }

    private fun setupCancelButtonListener() {
        cancelButton.setOnClickListener {
            end()
        }
    }

    private fun setupRecord(record: Record) {
        recordNameField.value = record.name
        recordValueField.value = record.value.toString()
        dailySwitch.isChecked = record.daily
        wallet.text = record.wallet
        tag.text = record.tag
        date.text = record.date.dateString()
        time.text = record.date.timeString()
        setupListeners()
        setupChangers()
    }

    private fun showError(error: String) {
        // TODO: (do some error showing)
        Snackbar.make(coordinator, error, Snackbar.LENGTH_LONG)
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    private fun showSuccess(success: String) {
        // TODO: (do some work stuff)
        Snackbar.make(coordinator, success, Snackbar.LENGTH_LONG)
        Toast.makeText(this, success, Toast.LENGTH_LONG).show()
    }

    private fun end() {
        viewModel.stopEditing()
        popupWindow?.dismiss()
        fadeOutFields()
        setResult(Activity.RESULT_OK)
        Handler(Looper.getMainLooper()).postDelayed({ finishAfterTransition() }, 0)
    }

    override fun onBackPressed() {
        if (popupWindow == null) {
            end()
        } else {
            animateOutPopupContentAndDismissPopupWindow()
        }
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
                null -> {
                }
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
            if (it != null) {
                setupRecord(it)
            }
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
        dateLayout.animate().alpha(0f).setDuration(fadeOutTime).start()
        timeLayout.animate().alpha(0f).setDuration(fadeOutTime).start()
        header.animate().alpha(0f).setDuration(fadeOutTime).start()
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
        dateLayout.animate().alpha(1f).setDuration(fadeInTime).start()
        timeLayout.animate().alpha(1f).setDuration(fadeInTime).start()
        header.animate().alpha(1f).setDuration(fadeInTime).start()
    }
}
