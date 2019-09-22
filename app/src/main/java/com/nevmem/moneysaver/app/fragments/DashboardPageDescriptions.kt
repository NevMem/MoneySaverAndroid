package com.nevmem.moneysaver.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.data.Info
import com.nevmem.moneysaver.app.data.repositories.InfoRepository
import com.nevmem.moneysaver.app.utils.TypeUtils
import kotlinx.android.synthetic.main.dashboard_page_descriptions.*
import javax.inject.Inject

class DashboardPageDescriptions : Fragment() {
    @Inject
    lateinit var infoRepo: InfoRepository

    var info: Info? = null
        set(value) {
            field = value
            infoChanged()
        }

    enum class InfoMode {
        Week, ThirtyDays, Full, FullDaily
    }

    private var mode: InfoMode =
        InfoMode.Week
        set(value) {
            field = value
            infoChanged()
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_page_descriptions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        weekButton.isActivated = true
        weekButton.setOnClickListener {
            mode = InfoMode.Week
        }
        _30daysButton.setOnClickListener {
            mode = InfoMode.ThirtyDays
        }
        fullButton.setOnClickListener {
            mode = InfoMode.Full
        }
        fullDailyButton.setOnClickListener {
            mode = InfoMode.FullDaily
        }
        (activity?.applicationContext as App).appComponent.inject(this)
        infoRepo.info().observe(this, Observer {
            info = it
            infoChanged()
        })
    }

    private fun deactivateAllButtons() {
        weekButton.isActivated = false
        _30daysButton.isActivated = false
        fullButton.isActivated = false
        fullDailyButton.isActivated = false
    }

    private fun infoChanged() {
        info?.let {
            deactivateAllButtons()
            when (mode) {
                InfoMode.Week -> run {
                    weekButton.isActivated = true
                    description.text = "Your last week info"
                    value.text = TypeUtils.formatDouble(it.sum7Days)
                    averageValue.text = TypeUtils.formatDouble(it.average7Days)
                }
                InfoMode.ThirtyDays -> run {
                    _30daysButton.isActivated = true
                    description.text = "Your last 30 days info"
                    value.text = TypeUtils.formatDouble(it.sum30Days)
                    averageValue.text = TypeUtils.formatDouble(it.average30Days)
                }
                InfoMode.Full -> run {
                    fullButton.isActivated = true
                    description.text = "All outcomes"
                    value.text = TypeUtils.formatDouble(it.totalSpend)
                    averageValue.text = TypeUtils.formatDouble(it.average)
                }
                InfoMode.FullDaily -> run {
                    fullDailyButton.isActivated = true
                    description.text = "All daily outcomes"
                    value.text = TypeUtils.formatDouble(it.dailySum)
                    averageValue.text = TypeUtils.formatDouble(it.dailyAverage)
                }
            }
        }
    }
}