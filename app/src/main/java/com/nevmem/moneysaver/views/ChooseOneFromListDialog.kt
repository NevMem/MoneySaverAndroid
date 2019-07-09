package com.nevmem.moneysaver.views

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.views.utils.DialogsHelper
import kotlinx.android.synthetic.main.choose_one_from_list.view.*

class ChooseOneFromListDialog(private var headerText: String, private var values: List<String>) :
    AppCompatDialogFragment() {
    private var okListener: ((String) -> Unit)? = null
    private var dismissListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = DialogsHelper.createView(activity!!, R.layout.choose_one_from_list)
        val builder = AlertDialog.Builder(activity!!, R.style.CustomDialogStyle)
        view.spinner.adapter = ArrayAdapter(activity, R.layout.default_spinner_item_layout, values)
        return builder.setView(view)
            .setTitle(headerText)
            .setPositiveButton("Ok") { _, _ -> okListener?.invoke(view.spinner.selectedItem.toString()) }
            .setNegativeButton("Cancel") { _, _ -> dismissListener?.invoke() }
            .create()
    }

    fun setOkListener(cb: (String) -> Unit) {
        okListener = cb
    }

    fun setDismissListener(cb: () -> Unit) {
        dismissListener = cb
    }
}