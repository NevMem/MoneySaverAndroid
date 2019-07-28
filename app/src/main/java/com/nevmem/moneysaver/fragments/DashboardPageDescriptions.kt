package com.nevmem.moneysaver.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.Info
import com.nevmem.moneysaver.data.repositories.InfoRepository
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
                    value.text = it.sum7Days.toString()
                    averageValue.text = it.average7Days.toString()
                }
                InfoMode.ThirtyDays -> run {
                    _30daysButton.isActivated = true
                    description.text = "Your last 30 days info"
                    value.text = it.sum30Days.toString()
                    averageValue.text = it.average30Days.toString()
                }
                InfoMode.Full -> run {
                    fullButton.isActivated = true
                    description.text = "All outcomes"
                    value.text = info?.totalSpend.toString()
                    averageValue.text = info?.average.toString()
                }
                InfoMode.FullDaily -> run {
                    fullDailyButton.isActivated = true
                    description.text = "All daily outcomes"
                    value.text = info?.dailySum.toString()
                    averageValue.text = info?.dailyAverage.toString()
                }
            }
        }
    }
}