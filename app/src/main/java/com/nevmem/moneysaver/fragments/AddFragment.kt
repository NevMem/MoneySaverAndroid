package com.nevmem.moneysaver.fragments

import android.os.Bundle
import android.util.Log.i
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.RecordDate
import com.nevmem.moneysaver.data.util.ErrorState
import com.nevmem.moneysaver.data.util.LoadingState
import com.nevmem.moneysaver.data.util.NoneState
import com.nevmem.moneysaver.data.util.SuccessState
import com.nevmem.moneysaver.views.InfoDialog
import com.nevmem.moneysaver.views.OneStringDialog
import kotlinx.android.synthetic.main.add_record_fragment.*


class AddFragment : Fragment() {
    lateinit var app: App
    private lateinit var viewModel: AddFragmentViewModel

    private var popupWindow: PopupWindow? = null

    init {
        i("ADD_FRAGMENT", "initialising AddFragment")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_record_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity!!.applicationContext as App
        viewModel = ViewModelProviders.of(this).get(AddFragmentViewModel::class.java)

        app.appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.tags.observe(this, Observer {
            if (it != null) {
                val strings = ArrayList<String>()
                it.forEach { tag ->
                    strings.add(tag.name)
                }
                tags.adapter =
                    ArrayAdapter<String>(app, R.layout.default_spinner_item_layout, strings)
            }
        })

        viewModel.wallets.observe(this, Observer {
            if (it != null) {
                val strings = ArrayList<String>()
                it.forEach { wallet ->
                    strings.add(wallet.name)
                }
                chooseWallet.adapter =
                    ArrayAdapter<String>(app, R.layout.default_spinner_item_layout, strings)
            }
        })

        viewModel.addingState.observe(this, Observer {
            when (it) {
                is NoneState -> {
                    hideLoading()
                    showAddButton()
                }
                is LoadingState -> {
                    hideAddButton()
                    showLoading()
                }
                is ErrorState -> {
                    hideLoading()
                    showAddButton()
                    showError(it.error)
                    viewModel.receivedInfo()
                }
                is SuccessState -> {
                    hideLoading()
                    showAddButton()
                    showSuccess(it.success)
                    viewModel.receivedInfo()
                }
            }
        })

        viewModel.tagAddingState.observe(this, Observer {
            when (it) {
                null, is NoneState, is SuccessState -> {
                    processingTags.visibility = View.GONE
                    createTagError.visibility = View.GONE
                    createTagButton.visibility = View.VISIBLE
                }
                is LoadingState -> {
                    createTagButton.visibility = View.GONE
                    createTagError.visibility = View.GONE
                    processingTags.visibility = View.VISIBLE
                }
                is ErrorState -> {
                    createTagButton.visibility = View.GONE
                    processingTags.visibility = View.GONE
                    createTagError.visibility = View.VISIBLE
                    createTagError.setOnClickListener { _ ->
                        run {
                            showError(it.error)
                            viewModel.receivedTagAddingError()
                        }
                    }
                }
            }
        })

        viewModel.walletAddingState.observe(this, Observer {
            when (it) {
                null, is NoneState, is SuccessState -> {
                    processingWallets.visibility = View.GONE
                    createWalletError.visibility = View.GONE
                    createWalletButton.visibility = View.VISIBLE
                }
                is LoadingState -> {
                    createWalletButton.visibility = View.GONE
                    createWalletError.visibility = View.GONE
                    processingWallets.visibility = View.VISIBLE
                }
                is ErrorState -> {
                    createWalletButton.visibility = View.GONE
                    processingWallets.visibility = View.GONE
                    createWalletError.visibility = View.VISIBLE
                    createWalletError.setOnClickListener { _ ->
                        run {
                            showError(it.error)
                            viewModel.receivedWalletAddingError()
                        }
                    }
                }
            }
        })

        createTagButton.setOnClickListener {
            createTag()
        }

        createWalletButton.setOnClickListener {
            createWallet()
        }

        addRecord.setOnClickListener {
            add()
        }
    }

    private fun createTag() {
        popupWindow?.dismiss()
        val createTag = OneStringDialog(app)
        createTag.headerString = "Create tag"
        createTag.descriptionString = "Please choose special unique name for new tag"
        popupWindow =
            PopupWindow(createTag, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow?.showAtLocation(createTagButton, Gravity.CENTER, 0, 0)
        createTag.setOnDismissListener {
            popupWindow?.dismiss()
        }
        createTag.setOnOkListener {
            viewModel.addTag(it)
            popupWindow?.dismiss()
        }
    }

    private fun createWallet() {
        popupWindow?.dismiss()
        val createTag = OneStringDialog(app)
        createTag.headerString = "Create wallet"
        createTag.descriptionString = "Please choose special unique name for new wallet"
        popupWindow =
            PopupWindow(createTag, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow?.showAtLocation(createTagButton, Gravity.CENTER, 0, 0)
        createTag.setOnDismissListener {
            popupWindow?.dismiss()
        }
        createTag.setOnOkListener {
            viewModel.addWallet(it)
            popupWindow?.dismiss()
        }
    }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
    }

    private fun hideAddButton() {
        addRecord.visibility = View.GONE
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
    }

    private fun showAddButton() {
        addRecord.visibility = View.VISIBLE
    }

    private fun showError(error: String) {
        val dialog = InfoDialog("Error happened", error)
        dialog.show(activity!!.supportFragmentManager, "error_info_dialog")
    }

    private fun showSuccess(success: String?) {
        if (success != null) {
            val dialog = InfoDialog("Success", success)
            dialog.show(activity!!.supportFragmentManager, "success_info_dialog")
        }
    }

    private fun add() {
        val name = recordNameField.text.toString()
        val value = recordValueField.text.toString()
        val walletItem = chooseWallet.selectedItem
        val tagItem = tags.selectedItem
        if (walletItem == null || tagItem == null) {
            showError("Tag or wallet is empty, please create at least one before")
            return
        }
        val wallet = walletItem.toString()
        val tag = tagItem.toString()
        try {
            val doubleValue = value.toDouble()
            val record = Record()
            record.daily = true
            record.name = name
            record.value = doubleValue
            record.wallet = wallet
            record.tag = tag
            record.date = RecordDate.currentDate()
            viewModel.add(record)
        } catch (e: NumberFormatException) {
            showError("Please enter correct value")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.addingState.removeObservers(this)
    }
}