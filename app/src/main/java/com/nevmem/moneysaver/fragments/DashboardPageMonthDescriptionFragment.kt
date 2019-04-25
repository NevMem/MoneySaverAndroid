package com.nevmem.moneysaver.fragments

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.activity.MonthDescriptionActivity
import com.nevmem.moneysaver.data.repositories.InfoRepository
import com.nevmem.moneysaver.views.PieChart
import kotlinx.android.synthetic.main.dashboard_page_month_description.*
import kotlinx.android.synthetic.main.label_row.view.*
import javax.inject.Inject

class DashboardPageMonthDescriptionFragment : Fragment() {
    @Inject
    lateinit var infoRepo: InfoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.applicationContext as App).appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_page_month_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {
            openMonthDescriptionActivity()
        }
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
                for (index in 0 until (labels.size)) {
                    labelsAnchor.addView(
                        createLabelRow(
                            activity!!,
                            labelsAnchor,
                            labels[index],
                            PieChart.baseColors[index % PieChart.baseColors.size]
                        )
                    )
                }
                chart.setData(values, PieChart.baseColors)
            }
        })
    }

    private fun createLabelRow(ctx: Context, parent: ViewGroup, name: String, color: Int): View {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.label_row, parent, false)
        view.labelName.text = name
        view.labelBadge.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        return view
    }

    private fun openMonthDescriptionActivity() {
        val intent = Intent(activity!!, MonthDescriptionActivity::class.java)
        if (descriptionCard != null) {
            val options = ActivityOptions.makeSceneTransitionAnimation(
                activity,
                android.util.Pair<View, String>(descriptionHeading, "headerTransition"),
                android.util.Pair<View, String>(chart, "chartTransition"),
                android.util.Pair<View, String>(descriptionCard, "cardTransition")
            )
            startActivity(intent, options.toBundle())
        } else {
            Toast.makeText(activity, "Card is null", Toast.LENGTH_LONG).show()
        }
    }
}