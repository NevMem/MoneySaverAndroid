package com.nevmem.moneysaver.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.Info
import kotlinx.android.synthetic.main.main_page_week_month_info.view.*

class MainPageWeekMonthInfo(ctx: Context, attrs: AttributeSet) : ConstraintLayout(ctx, attrs) {

    var info: Info? = null
        set(value) {
            field = value
            infoChanged()
        }

    enum class InfoMode {
        Week, ThirtyDays
    }

    private var mode: InfoMode = InfoMode.Week
        set(value) {
            field = value
            infoChanged()
        }

    init {
        inflate(ctx, R.layout.main_page_week_month_info, this)
        weekButton.isActivated = true
        weekButton.setOnClickListener {
            mode = InfoMode.Week
        }
        _30daysButton.setOnClickListener {
            mode = InfoMode.ThirtyDays
        }
    }

    private fun infoChanged() {
        info?.let {
            when (mode) {
                InfoMode.Week -> run {
                    weekButton.isActivated = true
                    _30daysButton.isActivated = false
                    description.text = "Your last week info"
                    value.text = it.sum7Days.toString()
                    averageValue.text = it.average7Days.toString()
                }
                InfoMode.ThirtyDays -> run {
                    weekButton.isActivated = false
                    _30daysButton.isActivated = true
                    description.text = "Your last 30 days info"
                    value.text = it.sum30Days.toString()
                    averageValue.text = it.average30Days.toString()
                }
            }
        }
    }
}