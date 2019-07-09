package com.nevmem.moneysaver.views

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.nevmem.moneysaver.R
import com.nevmem.moneysaver.views.utils.DialogsHelper
import kotlinx.android.synthetic.main.info_dialog_popup.view.*

class InfoDialog(private var header: String, private var message: String) : AppCompatDialogFragment() {
    private var okCallback: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = DialogsHelper.createView(activity!!, R.layout.info_dialog_popup)
        view.popupMessage.text = message
        val builder = AlertDialog.Builder(activity!!, R.style.CustomDialogStyle)

        return builder
            .setTitle(header)
            .setView(view)
            .setPositiveButton("Ok") { _, _ -> okCallback?.invoke() }
            .create()
    }

    fun setOkListener(cb: () -> Unit) {
        okCallback = cb
    }
}