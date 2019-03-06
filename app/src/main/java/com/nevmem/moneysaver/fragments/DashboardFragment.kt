package com.nevmem.moneysaver.fragments

import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.MainPage
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.structure.Callback
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.user_profile.*
import org.reactivestreams.Subscription
import java.lang.NullPointerException

class DashboardFragment : Fragment() {
    lateinit var app: App
    lateinit var parent: MainPage

    lateinit var infoFlow: Disposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userProfileProgressBar.visibility = View.VISIBLE
        try {
            app = activity!!.applicationContext as App
            parent = activity!! as MainPage
            app.checkData(Callback<String> {
                System.out.println("Result string $it")
            })
            infoFlow = app.infoFlow.subscribe{ value -> run {
                if (!value.ready)
                    userProfileProgressBar.visibility = View.VISIBLE
                else
                    userProfileProgressBar.visibility = View.GONE
                average30Days.text = value.average30Days.toString()
                average7Days.text = value.average7Days.toString()
                trackedDays.text = value.trackedDays.toString()
                totalSpend.text = value.totalSpend.toString()
                averageSpend.text = value.average.toString()
                sum30Days.text = value.sum30Days.toString()
                sum7Days.text = value.sum7Days.toString()
                sumDayChart.values = value.sumDay

                val animator = ValueAnimator.ofFloat(0f,  1f)
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
            }}
        } catch (_: KotlinNullPointerException) {
            System.out.println("Kotlin Null Pointer Exceptions")
        }
        userName.text = app.user.first_name
        reloadButton.setOnClickListener {
            reload()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        i("Dashboard Fragment", "disposing from infoFlow")
        infoFlow.dispose()
    }

    fun reload() {
        app.loadInfo(Callback {  }, Callback {  })
    }
}
