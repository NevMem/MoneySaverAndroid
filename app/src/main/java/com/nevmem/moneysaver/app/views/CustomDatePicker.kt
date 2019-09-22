package com.nevmem.moneysaver.app.views

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.common.data.RecordDate
import com.nevmem.moneysaver.common.utils.DateHelper
import kotlinx.android.synthetic.main.date_picker.view.*

class CustomDatePicker(ctx: Context, date: RecordDate? = null) : ConstraintLayout(ctx) {
    private var onCancel: (() -> Unit)? = null
    private var onOk: ((Int, Int, Int) -> Unit)? = null

    init {
        inflate(ctx, R.layout.date_picker, this)
        year.minValue = 2018
        year.maxValue = 2019

        month.minValue = 1
        month.maxValue = 12

        month.setOnValueChangedListener { _, _, _ -> run { recalculateDays() } }
        year.setOnValueChangedListener { _, _, _ -> run { recalculateDays() } }

        day.minValue = 1
        day.maxValue = 31

        ok.setOnClickListener {
            onOk?.invoke(year.value, month.value, day.value)
        }

        cancel.setOnClickListener {
            onCancel?.invoke()
        }

        if (date != null) {
            year.value = date.year
            month.value = date.month
            day.value = date.day
            recalculateDays()
        }
    }

    private fun recalculateDays() {
        day.maxValue = DateHelper.getAmountOfDaysInMonth(year.value, month.value)
    }

    fun setOnCancel(cb: () -> Unit) {
        this.onCancel = cb
    }

    fun setOnOk(cb: (Int, Int, Int) -> Unit) {
        this.onOk = cb
    }
}
