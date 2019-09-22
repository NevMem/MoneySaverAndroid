package com.nevmem.moneysaver.app.views

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.common.data.RecordDate
import kotlinx.android.synthetic.main.time_picker.view.*

class CustomTimePicker(private var ctx: Context, private var currentDate: RecordDate) : ConstraintLayout(ctx) {
    enum class PickMode {
        HOUR_AM, HOUR_PM, MINUTE, NONE
    }

    private var onOk: ((RecordDate) -> Unit)? = null
    private var onCancel: (() -> Unit)? = null

    private var mode = PickMode.HOUR_AM
        set(value) {
            field = value
            modeChanged()
        }

    init {
        inflate(ctx, R.layout.time_picker, this)
        viewChanged()

        currentMinute.setOnClickListener { mode = PickMode.MINUTE }
        currentHour.setOnClickListener {
            mode = if (currentDate.hour in 0..12) {
                PickMode.HOUR_AM
            } else {
                PickMode.HOUR_PM
            }
        }
        AMBtn.setOnClickListener {
            mode = PickMode.HOUR_AM
        }
        PMBtn.setOnClickListener {
            mode = PickMode.HOUR_PM
        }

        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        minutePicker.setOnValueChangedListener { _, _, value ->
            run {
                currentDate.minute = value
                viewChanged()
            }
        }
        hourPicker.setOnValueChangedListener { value ->
            run {
                if (value in 0..12) {
                    when (mode) {
                        PickMode.HOUR_AM -> currentDate.hour = value
                        PickMode.HOUR_PM -> currentDate.hour = value + 12
                        else -> {
                        }
                    }
                }
                viewChanged()
            }
        }

        okButton.setOnClickListener {
            onOk?.invoke(currentDate)
        }

        cancelButton.setOnClickListener {
            onCancel?.invoke()
        }
    }

    private fun viewChanged() {
        currentHour.text = currentDate.hour.toString().padStart(2, '0')
        currentMinute.text = currentDate.minute.toString().padStart(2, '0')
        minutePicker.value = currentDate.minute
        if (currentDate.hour in 0..12 && mode == PickMode.HOUR_AM) {
            hourPicker.value = currentDate.hour
        } else if (currentDate.hour in 12..23 && mode == PickMode.HOUR_PM) {
            hourPicker.value = currentDate.hour - 12
        } else {
            hourPicker.value = -1
        }
    }

    fun setOnOk(cb: (RecordDate) -> Unit) {
        onOk = cb
    }

    fun setOnCancel(cb: () -> Unit) {
        onCancel = cb
    }

    private fun modeChanged() {
        when (mode) {
            PickMode.HOUR_AM -> {
                currentHour.setTextColor(ContextCompat.getColor(ctx, R.color.default_white_color))
                currentMinute.setTextColor(ContextCompat.getColor(ctx, R.color.secondary_white_color))
                pickerHolder.visibility = View.VISIBLE
                minutePicker.visibility = View.GONE
                AMBtn.setTextColor(ContextCompat.getColor(ctx, R.color.default_white_color))
                PMBtn.setTextColor(ContextCompat.getColor(ctx, R.color.secondary_white_color))
            }
            PickMode.HOUR_PM -> {
                currentHour.setTextColor(ContextCompat.getColor(ctx, R.color.default_white_color))
                currentMinute.setTextColor(ContextCompat.getColor(ctx, R.color.secondary_white_color))
                pickerHolder.visibility = View.VISIBLE
                minutePicker.visibility = View.GONE
                AMBtn.setTextColor(ContextCompat.getColor(ctx, R.color.secondary_white_color))
                PMBtn.setTextColor(ContextCompat.getColor(ctx, R.color.default_white_color))
            }
            PickMode.MINUTE -> {
                currentHour.setTextColor(ContextCompat.getColor(ctx, R.color.secondary_white_color))
                currentMinute.setTextColor(ContextCompat.getColor(ctx, R.color.default_white_color))
                minutePicker.visibility = View.VISIBLE
                pickerHolder.visibility = View.GONE
                AMBtn.setTextColor(ContextCompat.getColor(ctx, R.color.secondary_white_color))
                PMBtn.setTextColor(ContextCompat.getColor(ctx, R.color.secondary_white_color))
            }
            else -> {}
        }
        viewChanged()
    }
}