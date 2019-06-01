package com.nevmem.moneysaver.activity

import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.activity.adapters.MonthDescriptionLabelsAdapter
import com.nevmem.moneysaver.data.repositories.InfoRepository
import com.nevmem.moneysaver.views.PieChart
import kotlinx.android.synthetic.main.month_description_page.*
import javax.inject.Inject

class MonthDescriptionActivity : AppCompatActivity() {
    @Inject
    lateinit var infoRepo: InfoRepository

    lateinit var viewModel: MonthDescriptionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.month_description_page)
        (applicationContext as App).appComponent.inject(this)
        window.statusBarColor = ContextCompat.getColor(this, R.color.cardColor)

        window.enterTransition = Fade()
        window.exitTransition = Fade()

        labelsInfoRecycler.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProviders.of(this).get(MonthDescriptionViewModel::class.java)
        viewModel.monthDescription.observe(this, Observer {
            if (it != null) {
                setupChart(it.byTagTotal)
                monthSpend.text = it.total.toString()
                monthDailySpend.text = it.totalDaily.toString()
                labelsInfoRecycler.adapter = MonthDescriptionLabelsAdapter(this, it.byTagTotal)
                labelsInfoRecycler.scheduleLayoutAnimation()
                headerText.text = it.monthId
            }
        })

        prevButton.setOnClickListener { viewModel.prev() }
        nextButton.setOnClickListener { viewModel.next() }
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