package com.nevmem.moneysaver.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextWatcher
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.MainPage
import kotlinx.android.synthetic.main.add_record_activity.*
import android.text.Editable
import android.widget.AdapterView
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.structure.Callback


class AddFragment : Fragment() {
    lateinit var app: App
    lateinit var parent: MainPage
    lateinit var viewModel: AddFragmentViewModel

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
        i("ADD_FRAGMENT", "onCreate method was called")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headerText.text = "Add new record"
        if (app.tags.size != 0)
            tags.adapter =
                ArrayAdapter<String>(parent, android.R.layout.simple_spinner_dropdown_item, app.tags)
        if (app.wallets.size != 0)
            chooseWallet.adapter = ArrayAdapter<String>(parent, android.R.layout.simple_spinner_dropdown_item, app.wallets)

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

            viewModel.success.value = ""
            viewModel.error.value = ""
            viewModel.loading.value = true

            try {
                val doubleValue = value.toDouble()
                app.makeAddRequest(name, doubleValue, tag, wallet, Callback {
                    viewModel.success.value = "Success"
                    viewModel.loading.value = false
                }, Callback {
                    viewModel.error.value = it
                    viewModel.loading.value = false
                })
            } catch (e: NumberFormatException) {
                viewModel.error.value = "Your value is bad"
                viewModel.loading.value = false
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