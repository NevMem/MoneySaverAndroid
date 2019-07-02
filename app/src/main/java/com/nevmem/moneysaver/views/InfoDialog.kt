package com.nevmem.moneysaver.views

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.info_dialog_popup.view.*

class InfoDialog(private var header: String, private var message: String) : AppCompatDialogFragment() {
    private var okCallback: (() -> Unit)? = null

    private fun createView(): View {
        val inflater = activity?.layoutInflater ?: throw IllegalStateException("Layout inflater cannot be null")
        return inflater.inflate(R.layout.info_dialog_popup, null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = createView()
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