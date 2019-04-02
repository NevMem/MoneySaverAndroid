package com.nevmem.moneysaver.fragments

import android.os.Bundle
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.MainPage
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.repositories.HistoryRepository
import com.nevmem.moneysaver.fragments.adapters.HistoryFragmentAdapter
import kotlinx.android.synthetic.main.history_layout.*
import javax.inject.Inject

class HistoryFragment : Fragment() {
    lateinit var app: App

    @Inject
    lateinit var historyRepo: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        i("HistoryFragment", "onCreate")
        try {
            app = activity!!.applicationContext as App
        } catch (_: KotlinNullPointerException) {
            System.out.println("Null pointer")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historySwipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(context!!, R.color.themeColor),
            ContextCompat.getColor(context!!, R.color.specialColor),
            ContextCompat.getColor(context!!, R.color.errorColor)
        )
        historySwipeRefreshLayout.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                context!!,
                R.color.backgroundColor
            )
        )

        app.appComponent.inject(this)

        historySwipeRefreshLayout.setOnRefreshListener {
            historyRepo.tryUpdate()
        }
        historyRepo.loading.observe(this, Observer {
            if (it != null) {
                historySwipeRefreshLayout.isRefreshing = it
            }
        })

        wrapper.adapter = HistoryFragmentAdapter(activity!!, this, historyRepo)
        wrapper.layoutManager = LinearLayoutManager(activity)
    }
}