package com.nevmem.moneysaver.fragments

import android.os.Bundle
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.MainPage
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.RecordDate
import com.nevmem.moneysaver.data.repositories.HistoryRepository
import com.nevmem.moneysaver.data.repositories.TagsRepository
import com.nevmem.moneysaver.data.repositories.WalletsRepository
import kotlinx.android.synthetic.main.add_record_activity.*
import javax.inject.Inject


class AddFragment : Fragment() {
    lateinit var app: App
    lateinit var parent: MainPage
    private lateinit var viewModel: AddFragmentViewModel

    @Inject
    lateinit var walletsRepo: WalletsRepository
    @Inject
    lateinit var tagsRepo: TagsRepository
    @Inject
    lateinit var historyRepo: HistoryRepository

    init {
        i("ADD_FRAGMENT", "initialising AddFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.nevmem.moneysaver.R.layout.add_record_activity, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity!!.applicationContext as App
        parent = activity as MainPage
        viewModel = ViewModelProviders.of(parent).get(AddFragmentViewModel::class.java)

        app.appComponent.inject(this)
        walletsRepo.tryUpdate()

        i("ADD_FRAGMENT", "onCreate method was called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerText.text = "Add new record"

        tagsRepo.tags.observe(this, Observer {
            if (it != null) {
                val strings = ArrayList<String>()
                it.forEach {tag -> 
                    strings.add(tag.name)
                }
                tags.adapter =
                    ArrayAdapter<String>(parent, android.R.layout.simple_spinner_dropdown_item, strings)
            }
        })

        walletsRepo.wallets.observe(this, Observer {
            if (it != null) {
                val strings = ArrayList<String>()
                it.forEach {wallet ->
                    strings.add(wallet.name)
                }
                chooseWallet.adapter =
                    ArrayAdapter<String>(parent, android.R.layout.simple_spinner_dropdown_item, strings)
            }
        })

        viewModel.error.observe(parent, Observer {
            headerText.setTextColor(ContextCompat.getColor(parent, R.color.errorColor))
            headerText.text = it
        })

        viewModel.success.observe(parent, Observer {
            headerText.setTextColor(ContextCompat.getColor(parent, R.color.specialColor))
            headerText.text = it
        })

        viewModel.loading.observe(parent, Observer {
            if (it!!) {
                loadingBar.visibility = View.VISIBLE
            } else {
                loadingBar.visibility = View.GONE
            }
        })

        addRecord.setOnClickListener {
            val name = recordNameField.text.toString()
            val value = recordValueField.text.toString()
            val wallet = chooseWallet.selectedItem.toString()
            val tag = tags.selectedItem.toString()
            System.out.println("$name $value $wallet $tag")

            viewModel.success.postValue("")
            viewModel.error.postValue("")
            viewModel.loading.postValue(true)

            try {
                val doubleValue = value.toDouble()
                val record = Record()

                record.daily = true
                record.name = name
                record.value = doubleValue
                record.wallet = wallet
                record.tag = tag
                record.date = RecordDate.currentDate()

                sendAddRequest(record)
            } catch (e: NumberFormatException) {
                viewModel.error.value = "Your value is bad"
                viewModel.loading.value = false
            }
        }
    }

    private fun sendAddRequest(record: Record) {
        viewModel.loading.postValue(true)
        viewModel.error.postValue("")
        viewModel.success.postValue("")
        historyRepo.addRecord(record) {
            viewModel.loading.postValue(false)
            when (it) {
                null -> viewModel.success.postValue("Record was added")
                else -> viewModel.error.postValue(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        System.out.println("View was destroyed")
        viewModel.loading.removeObservers(parent)
        viewModel.error.removeObservers(parent)
        viewModel.success.removeObservers(parent)
    }
}