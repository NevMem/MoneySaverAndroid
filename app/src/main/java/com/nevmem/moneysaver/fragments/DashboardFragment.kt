package com.nevmem.moneysaver.fragments

import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.nevmem.moneysaver.activity.SettingsActivity
import com.nevmem.moneysaver.data.UserHolder
import com.nevmem.moneysaver.data.repositories.InfoRepository
import kotlinx.android.synthetic.main.dashboard_page_fragment.*
import kotlinx.android.synthetic.main.user_profile.*
import javax.inject.Inject

class DashboardFragment : Fragment() {
    lateinit var app: App

    @Inject
    lateinit var infoRepo: InfoRepository

    @Inject
    lateinit var userHolder: UserHolder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_page_fragment, container, false)
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
            trackedDays.text = it.trackedDays.toString()
            sumDayChart.values = it.sumDay

            val animator = ValueAnimator.ofFloat(0f, 1f)
            animator.interpolator = AccelerateDecelerateInterpolator()
            animator.addUpdateListener { anim ->
                if (sumDayChart != null) {
                    sumDayChart.multiplier = anim.animatedValue as Float
                    sumDayChart.invalidate()
                }
            }
            animator.duration = 500
            animator.start()
            sumDayChart.invalidate()
        })

        userName.text = userHolder.user.firstName

        infoRepo.loading.observe(this, Observer {
            if (it != null)
                refreshLayout.isRefreshing = it
        })

        settingsButton.setOnClickListener {
            settings()
        }

        refreshLayout.setOnRefreshListener {
            infoRepo.tryUpdate()
        }
    }

    private fun settings() {
        val currentRotation= settingsButton.rotation
        settingsButton.animate().rotation(currentRotation + 180f).duration = 150
        Handler(Looper.getMainLooper())
            .postDelayed({
            val intent = Intent(activity, SettingsActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }, 150)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        i("Dashboard Fragment", "disposing from infoFlow")
    }
}
