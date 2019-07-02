package com.nevmem.moneysaver.views

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.confirmation_dialog_popup.view.*

class ConfirmationDialog(private var header: String, private var message: String) : AppCompatDialogFragment() {
    private var okCallback: (() -> Unit)? = null
    private var dismissCallback: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity?.layoutInflater
            ?: throw IllegalStateException("Something went wrong, layout inflater have to be not null")
        val view = inflater.inflate(R.layout.confirmation_dialog_popup, null)
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