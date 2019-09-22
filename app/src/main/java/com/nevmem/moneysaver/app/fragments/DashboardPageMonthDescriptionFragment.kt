package com.nevmem.moneysaver.app.fragments

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.activity.MonthDescriptionActivity
import com.nevmem.moneysaver.app.data.repositories.InfoRepository
import com.nevmem.moneysaver.app.utils.TransitionsLocker
import com.nevmem.moneysaver.app.views.PieChart
import kotlinx.android.synthetic.main.dashboard_page_month_description.*
import kotlinx.android.synthetic.main.label_row.view.*
import javax.inject.Inject

class DashboardPageMonthDescriptionFragment : Fragment() {
    companion object {
        const val OVERVIEW_SYNC = 0
    }

    private val transitionsLocker = TransitionsLocker()

    @Inject
    lateinit var infoRepo: InfoRepository

    private lateinit var viewModel: DashboardPageMonthDescriptionFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.applicationContext as App).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this).get(DashboardPageMonthDescriptionFragmentViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dashboard_page_month_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {
            openMonthDescriptionActivity()
        }
        viewModel.monthDescription.observe(this, Observer {
            if (it != null) {
                descriptionHeading.text = it.monthId
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
        prevButton.setOnClickListener { viewModel.prev() }
        nextButton.setOnClickListener { viewModel.next() }
    }

    private fun createLabelRow(ctx: Context, parent: ViewGroup, name: String, color: Int): View {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.label_row, parent, false)
        view.labelName.text = name
        view.labelBadge.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        return view
    }

    private fun openMonthDescriptionActivity() {
        if (!transitionsLocker.canRunTransition()) return
        transitionsLocker.lockTransitions()
        val intent = Intent(activity!!, MonthDescriptionActivity::class.java)
        intent.putExtra("monthIndex", viewModel.getMonthIndex())
        if (descriptionCard != null) {
            val options = ActivityOptions.makeSceneTransitionAnimation(activity)
            startActivityForResult(intent, OVERVIEW_SYNC, options.toBundle())
        } else {
            Toast.makeText(activity, "Card is null", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        transitionsLocker.unlockTransitions()
        when (requestCode) {
            OVERVIEW_SYNC -> {
                if (data != null) {
                    val extras = data.extras
                    if (extras != null) {
                        val index = extras.getInt("monthIndex", Int.MAX_VALUE)
                        viewModel.setMonthIndex(index)
                    }
                }
            }
            else -> {}
        }
    }
}