package com.nevmem.moneysaver.fragments

import android.app.ActivityOptions
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log.i
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.FullDescriptionActivity
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.structure.Callback
import kotlinx.android.synthetic.main.history_layout.*
import kotlinx.android.synthetic.main.home_page_activity.*
import kotlinx.android.synthetic.main.record_layout.view.*

class HistoryFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_layout, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        i("HistoryFragment", "onCreate")
        try {
            val app = activity!!.applicationContext as App
            app.loadData(Callback {}, Callback {})
            app.recordsLoading.observe(this, Observer {
                System.out.println("Hello from observer " + it.toString())
                historyWrapper.removeAllViews()
                val inflater = activity!!.application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                for (index in 0 until(app.records.size)) {
                    val recordRow = inflater.inflate(R.layout.record_layout, historyWrapper, false)
                    recordRow.recordNameField.text = app.records[index].name
                    recordRow.recordValue.text = app.records[index].value.toString()
                    recordRow.dateField.text = app.records[index].date.toString()
                    recordRow.walletField.text = app.records[index].wallet
                    recordRow.setOnClickListener {
                        val intent = Intent(activity, FullDescriptionActivity::class.java)
                        intent.putExtra("index", index)
                        val options = ActivityOptions.makeSceneTransitionAnimation(activity,
                            Pair<View, String>(recordRow.recordNameField, "recordNameTransition"),
                            Pair<View, String>(recordRow.recordValue, "recordValueTransition")
                        )
                        startActivity(intent, options.toBundle())
                    }
                    historyWrapper.addView(recordRow)
                }
            })
        } catch (_: KotlinNullPointerException) {
            System.out.println("Null pointer")
        }
    }
}