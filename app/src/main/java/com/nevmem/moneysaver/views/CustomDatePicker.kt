package com.nevmem.moneysaver.views

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.RecordDate
import com.nevmem.moneysaver.data.util.DateHelper
import kotlinx.android.synthetic.main.date_picker.view.*

class CustomDatePicker(ctx: Context, date: RecordDate? = null) : ConstraintLayout(ctx) {
    private var onCancel: (() -> Unit)? = null
    private var onOk: ((RecordDate) -> Unit)? = null

    init {
        inflate(ctx, R.layout.date_picker, this)
        year.minValue = 2018
        year.maxValue = 2019

        month.minValue = 1
        month.maxValue = 12

        month.setOnValueChangedListener { _, _, _ -> run { recalcDays() } }
        year.setOnValueChangedListener { _, _, _ -> run { recalcDays() } }

        day.minValue = 1
        day.maxValue = 31

        minute.minValue = 0
        minute.maxValue = 59

        hour.minValue = 0
        hour.maxValue = 23

        ok.setOnClickListener {
            onOk?.invoke(RecordDate(year.value, month.value, day.value, hour.value, minute.value))
        }

        cancel.setOnClickListener {
            onCancel?.invoke()
        }

        if (date != null) {
            year.value = date.year
            month.value = date.month
            day.value = date.day
            hour.value = date.hour
            minute.value = date.minute
            recalcDays()
        }
    }

    private fun recalcDays() {
        day.maxValue = DateHelper.getAmountOfDaysInMonth(year.value, month.value)
    }

    fun setOnCancel(cb: () -> Unit) {
        this.onCancel = cb
    }

    fun setOnOk(cb: (RecordDate) -> Unit) {
        this.onOk = cb
    }
}
