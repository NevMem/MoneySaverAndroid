package com.nevmem.moneysaver.app.views

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.app.views.utils.DialogsHelper
import kotlinx.android.synthetic.main.confirmation_dialog_popup.view.*

class ConfirmationDialog(private var header: String, private var message: String) : AppCompatDialogFragment() {
    private var okCallback: (() -> Unit)? = null
    private var dismissCallback: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity, R.style.CustomDialogStyle)
        val view = DialogsHelper.createView(activity!!, R.layout.confirmation_dialog_popup)
        view.popupMessage.text = message
        return builder.setView(view)
            .setTitle(header)
            .setNegativeButton("Cancel") { _, _ -> dismissCallback?.invoke() }
            .setPositiveButton("OK") { _, _ -> okCallback?.invoke() }
            .create()
    }

    fun setOkListener(cb: () -> Unit) {
        okCallback = cb
    }

    fun setDismissListener(cb: () -> Unit) {
        dismissCallback = cb
    }
}