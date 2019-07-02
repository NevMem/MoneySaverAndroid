package com.nevmem.moneysaver.views

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.data.TemplateBase
import com.nevmem.moneysaver.data.repositories.TagsRepository
import com.nevmem.moneysaver.data.repositories.WalletsRepository
import kotlinx.android.synthetic.main.new_template_dialog.view.*
import javax.inject.Inject

class NewTemplateDialog : AppCompatDialogFragment() {
    var okCallback: ((TemplateBase) -> Unit)? = null
    var dismissCallback: (() -> Unit)? = null

    @Inject
    lateinit var walletsRepo: WalletsRepository
    @Inject
    lateinit var tagsRepo: TagsRepository

    private fun createView(): View {
        val inflater = activity?.layoutInflater ?: throw IllegalStateException("Layout inflater have to be not null")
        return inflater.inflate(R.layout.new_template_dialog, null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!, R.style.CustomDialogStyle)
        val view = createView()

        setupObservers(view)

        return builder.setView(view)
            .setTitle("Create new template")
            .setNegativeButton("Cancel") { _, _ -> dismissCallback?.invoke() }
            .setPositiveButton("Create") { _, _ ->
                run {
                    try {
                        okCallback?.invoke(assembleTemplateBase(view))
                    } catch (e: IllegalStateException) {
                        // TODO: (handling errors)
                    }
                }
            }
            .create()
    }

    private fun setupObservers(v: View) {
        (activity!!.applicationContext as App).appComponent.inject(this)
        tagsRepo.tags.observe(activity!!, Observer {
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

    fun setDismissListener(cb: () -> Unit) {
        dismissCallback = cb
    }
}