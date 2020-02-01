package com.nevmem.moneysaver.app.fragments

import android.os.Bundle
import android.util.Log.i
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.common.data.Record
import com.nevmem.moneysaver.common.data.RecordDate
import com.nevmem.moneysaver.app.data.util.ErrorState
import com.nevmem.moneysaver.app.data.util.LoadingState
import com.nevmem.moneysaver.app.data.util.NoneState
import com.nevmem.moneysaver.app.data.util.SuccessState
import com.nevmem.moneysaver.app.views.InfoDialog
import com.nevmem.moneysaver.app.views.OneStringDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.add_record_fragment.*


class AddFragment : Fragment() {
    lateinit var app: App
    private lateinit var viewModel: AddFragmentViewModel

    private val subscriptions = CompositeDisposable()

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
        viewModel.wallets.observe(this, Observer {
            if (it == null)
                return@Observer
            val strings = it.map { wallet -> wallet.name }
            val alreadyPicked = chooseWallet.selectedItem
            chooseWallet.adapter =
                ArrayAdapter<String>(app, R.layout.default_spinner_item_layout, strings)
            if (alreadyPicked != null) {
                val index = strings.indexOfFirst { value -> value == alreadyPicked }
                if (index != -1)
                    chooseWallet.setSelection(index)
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

    override fun onResume() {
        super.onResume()
        subscriptions.add(viewModel.tags
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val strings = it.map { tag -> tag.name }
                val alreadyPicked = tags.selectedItem
                tags.adapter =
                    ArrayAdapter<String>(app, R.layout.default_spinner_item_layout, strings)
                if (alreadyPicked != null) {
                    val index = strings.indexOfFirst { value -> value == alreadyPicked }
                    if (index != -1)
                        tags.setSelection(index)
                }
            })
    }

    override fun onPause() {
        super.onPause()
        subscriptions.dispose()
    }

    private fun createTag() {
        val dialog = OneStringDialog(
            R.string.create_tag_string, R.string.create_tag_description_string)
        dialog.show(activity!!.supportFragmentManager, "create_tag_dialog")
        dialog.setOnOkListener {
            viewModel.addTag(it)
        }
    }

    private fun createWallet() {
        val dialog = OneStringDialog(
            R.string.create_wallet_string, R.string.create_wallet_description_string)
        dialog.show(activity!!.supportFragmentManager, "create_wallet_dialog")
        dialog.setOnOkListener {
            viewModel.addWallet(it)
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
