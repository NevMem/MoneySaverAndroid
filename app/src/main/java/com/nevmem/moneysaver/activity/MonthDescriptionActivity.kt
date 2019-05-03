package com.nevmem.moneysaver.activity

import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.repositories.InfoRepository
import com.nevmem.moneysaver.views.PieChart
import kotlinx.android.synthetic.main.month_description_page.*
import javax.inject.Inject

class MonthDescriptionActivity : AppCompatActivity() {
    @Inject
    lateinit var infoRepo: InfoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.month_description_page)
        (applicationContext as App).appComponent.inject(this)
        window.statusBarColor = ContextCompat.getColor(this, R.color.cardColor)

        headerText.text = "Last month description"

        window.enterTransition = Fade()
        window.exitTransition = Fade()

//        window.sharedElementEnterTransition = TransitionInflater.from(this)
//            .inflateTransition(R.transition.month_description_enter_shared_element)
//        window.sharedElementExitTransition = TransitionInflater.from(this)
//            .inflateTransition(R.transition.month_description_exit_shared_element)

        infoRepo.lastMonthDescription.observe(this, Observer {
            if (it != null) {
                setupChart(it.byTagTotal)
                monthSpend.text = it.total.toString()
                monthDailySpend.text = it.totalDaily.toString()
            }
        })
    }

    private fun setupChart(byTagTotal: HashMap<String, Double>) {
        val labels = ArrayList<String>()
        val values = ArrayList<Double>()
        for (key in byTagTotal.keys) {
            labels.add(key)
            values.add(byTagTotal[key]!!)
        }
        chart.setData(values, PieChart.baseColors)
    }
}