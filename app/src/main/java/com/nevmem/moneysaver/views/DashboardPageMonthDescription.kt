package com.nevmem.moneysaver.views

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.repositories.InfoRepository
import kotlinx.android.synthetic.main.dashboard_page_month_description.*
import kotlinx.android.synthetic.main.label_row.view.*
import javax.inject.Inject

class DashboardPageMonthDescription : Fragment() {
    @Inject
    lateinit var infoRepo: InfoRepository

    var colors = arrayListOf(
        Color.parseColor("#03f7eb"),
        Color.parseColor("#18ff6d"),
        Color.parseColor("#ff9505"),
        Color.parseColor("#5b2a86"),
        Color.parseColor("#f2545b"),
        Color.parseColor("#fffd82"),
        Color.parseColor("#a9e5bb"),
        Color.parseColor("#eee82c"),
        Color.parseColor("#32908f"),
        Color.parseColor("#26c485"),
        Color.parseColor("#ff6542"),
        Color.parseColor("#7d8cc4")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.applicationContext as App).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_page_month_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        infoRepo.lastMonthDescription.observe(this, Observer {
            if (it != null) {
                descriptionHeading.text = "Last month description"
                val labels = ArrayList<String>()
                val values = ArrayList<Double>()
                for (key in it.byTagTotal.keys) {
                    labels.add(key)
                    values.add(it.byTagTotal[key]!!)
                }
                labelsAnchor.removeAllViews()
                for (index in 0 until(labels.size)) {
                    labelsAnchor.addView(createLabelRow(activity!!, labelsAnchor, labels[index], colors[index % colors.size]))
                }
                chart.setData(values, colors)
            }
        })
    }

    private fun createLabelRow(ctx: Context, parent: ViewGroup, name: String, color: Int): View {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.label_row, parent, false)
        view.labelName.text = name
        view.labelBage.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        return view
    }
}