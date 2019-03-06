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
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.RecordChangeableWrapper
import com.nevmem.moneysaver.structure.Callback
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.history_layout.*
import kotlinx.android.synthetic.main.home_page_activity.*
import kotlinx.android.synthetic.main.record_layout.view.*

class HistoryFragment : Fragment() {
    lateinit var app: App
    lateinit var recordsFlow: Disposable

    var currentShown = 0
    val step = 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_layout, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        i("HistoryFragment", "onCreate")
        try {
            app = activity!!.applicationContext as App
            app.loadData(Callback {}, Callback {})
        } catch (_: KotlinNullPointerException) {
            System.out.println("Null pointer")
        }
    }

    fun createRow(record: Record, index: Int, inflater: LayoutInflater): View {
        val recordRow = inflater.inflate(R.layout.record_layout, historyWrapper, false)
        recordRow.recordNameField.text = record.name
        recordRow.recordValue.text = record.value.toString()
        recordRow.dateField.text = record.date.toString()
        recordRow.walletField.text = record.wallet
        recordRow.setOnClickListener {
            app.changeFlow.onNext(RecordChangeableWrapper(app.records[index]))
            val intent = Intent(activity, FullDescriptionActivity::class.java)
            intent.putExtra("index", index)
            val options = ActivityOptions.makeSceneTransitionAnimation(activity,
                Pair<View, String>(recordRow.recordNameField, "recordNameTransition"),
                Pair<View, String>(recordRow.recordValue, "recordValueTransition")
            )
            startActivity(intent, options.toBundle())
        }
        return recordRow
    }

    fun populateHistory(records: ArrayList<Record>, amount: Int) {
        var realAmount = amount
        if (realAmount > records.size)
            realAmount = records.size
        val inflater = activity!!.application.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for (index in currentShown until(realAmount)) {
            historyWrapper.addView(createRow(records[index], index, inflater))
        }
        currentShown = realAmount
    }

    private fun clearAll() {
        currentShown = 0
        historyWrapper.removeAllViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordsFlow = app.recordsFlow.subscribe { value -> run {
            clearAll()
            populateHistory(value, step)
        } }

        loadMoreButton.setOnClickListener {
            println(currentShown + step)
            if (app.recordsFlow.value != null)
                populateHistory(app.recordsFlow.value!!, currentShown + step)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recordsFlow.dispose()
    }
}