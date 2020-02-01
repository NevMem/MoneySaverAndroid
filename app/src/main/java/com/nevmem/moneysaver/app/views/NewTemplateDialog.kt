package com.nevmem.moneysaver.app.views

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.data.TemplateBase
import com.nevmem.moneysaver.app.data.repositories.TagsRepository
import com.nevmem.moneysaver.app.data.repositories.WalletsRepository
import com.nevmem.moneysaver.app.views.utils.DialogsHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.new_template_dialog.view.*
import javax.inject.Inject

class NewTemplateDialog : AppCompatDialogFragment() {
    private var okCallback: ((TemplateBase) -> Unit)? = null
    private var dismissCallback: (() -> Unit)? = null
    private var errorCallback: ((String) -> Unit)? = null

    @Inject
    lateinit var walletsRepo: WalletsRepository
    @Inject
    lateinit var tagsRepo: TagsRepository

    private val subscriptions = CompositeDisposable()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!, R.style.CustomDialogStyle)
        val view = DialogsHelper.createView(activity!!, R.layout.new_template_dialog)

        setupObservers(view)

        return builder.setView(view)
            .setTitle("Create new template")
            .setNegativeButton("Cancel") { _, _ -> dismissCallback?.invoke() }
            .setPositiveButton("Create") { _, _ ->
                run {
                    try {
                        okCallback?.invoke(assembleTemplateBase(view))
                    } catch (e: IllegalStateException) {
                        errorCallback?.invoke(e.message.toString())
                    }
                }
            }
            .create()
    }

    override fun onResume() {
        super.onResume()
        view?.let { setupObservers(it) }
    }

    override fun onPause() {
        super.onPause()
        subscriptions.dispose()
    }

    private fun setupObservers(v: View) {
        (activity!!.applicationContext as App).appComponent.inject(this)
        subscriptions.add(tagsRepo.tags
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                if (it != null) {
                    val buffer = ArrayList<String>()
                    it.forEach { value -> buffer.add(value.name) }
                    v.newTemplateTag.adapter =
                        ArrayAdapter(activity!!, R.layout.default_spinner_item_layout, buffer)
                }
            })
        walletsRepo.wallets.observe(activity!!, Observer {
            if (it != null) {
                val buffer = ArrayList<String>()
                it.forEach { value -> buffer.add(value.name) }
                v.newTemplateWallet.adapter =
                    ArrayAdapter(activity!!, R.layout.default_spinner_item_layout, buffer)
            }
        })
    }

    private fun assembleTemplateBase(v: View): TemplateBase {
        var value = 0.0
        try {
            value = v.newTemplateValue.text.toString().toDouble()
        } catch (e: NumberFormatException) {
        }
        val tagName = v.newTemplateTag.selectedItem
        val walletName = v.newTemplateWallet.selectedItem
        val name = v.newTemplateName.text.toString()
        if (tagName != null && walletName != null && name.isNotEmpty())
            return TemplateBase(name, value, tagName.toString(), walletName.toString())
        throw IllegalStateException("Name or tag or wallet is empty")
    }

    fun setOkListener(cb: (TemplateBase) -> Unit) {
        okCallback = cb
    }

    fun setErrorListener(cb: (String) -> Unit) {
        errorCallback = cb
    }

    fun setDismissListener(cb: () -> Unit) {
        dismissCallback = cb
    }
}
