package com.nevmem.moneysaver.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.data.repositories.InfoRepository
import kotlinx.android.synthetic.main.home_page_fragment.*
import kotlinx.android.synthetic.main.user_profile.*
import javax.inject.Inject

class DashboardFragment : Fragment() {
    lateinit var app: App

    @Inject
    lateinit var infoRepo: InfoRepository

    @Inject
    lateinit var userHolder: UserHolder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshLayout.setColorSchemeColors(
            ContextCompat.getColor(context!!, R.color.themeColor),
            ContextCompat.getColor(context!!, R.color.specialColor),
            ContextCompat.getColor(context!!, R.color.errorColor)
        )
        refreshLayout.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                context!!,
                R.color.backgroundColor
            )
        )

        app = activity!!.applicationContext as App
        app.appComponent.inject(this)

        infoRepo.info.observe(this, Observer {
            average30Days.text = it.average30Days.toString()
            average7Days.text = it.average7Days.toString()
            trackedDays.text = it.trackedDays.toString()
            totalSpend.text = it.totalSpend.toString()
            averageSpend.text = it.average.toString()
            sum30Days.text = it.sum30Days.toString()
            sum7Days.text = it.sum7Days.toString()
            sumDayChart.values = it.sumDay

            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.addUpdateListener {
                if (sumDayChart != null) {
                    sumDayChart.multiplier = it.animatedValue as Float
                    sumDayChart.invalidate()
                }
            }
            animator.duration = 500
            animator.start()
            sumDayChart.invalidate()
        })

        infoRepo.lastMonthDescription.observe(this, Observer {
            if (it != null) {
                monthDescription.setDescription("Your last month description")
                val values = ArrayList<Double>()
                val labels = ArrayList<String>()
                for (key in it.byTagTotal.keys) {
                    labels.add(key)
                    values.add(it.byTagTotal[key]!!)
                }
                monthDescription.setData(values, labels)
                monthDescription.invalidate()
            }
        })

        userName.text = userHolder.user.firstName

        infoRepo.loading.observe(this, Observer {
            when (it) {
                null -> {
                }
                else -> refreshLayout.isRefreshing = it
            }
        })
        refreshLayout.setOnRefreshListener {
            infoRepo.tryUpdate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        i("Dashboard Fragment", "disposing from infoFlow")
    }
}
