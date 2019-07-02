package com.nevmem.moneysaver.views

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDialogFragment
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.one_string_dialog.view.*

class OneStringDialog(private var header: String, private var description: String) : AppCompatDialogFragment() {
    private var onOkListener: ((String) -> Unit)? = null
    private var onDismissListener: (() -> Unit)? = null

    private fun createView(): View {
        val inflater = activity!!.layoutInflater ?: throw IllegalStateException("Layout inflater cannot be null")
        return inflater.inflate(R.layout.one_string_dialog, null)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = createView()
        view.description.text = description

        val builder = AlertDialog.Builder(activity!!, R.style.CustomDialogStyle)
        return builder
            .setTitle(header)
            .setView(view)
            .setPositiveButton("Ok") { _, _ -> onOkListener?.invoke(view.editText.text.toString()) }
            .setNegativeButton("Cancel") { _, _ -> onDismissListener?.invoke() }
            .create()
    }

    fun setOnOkListener(cb: (String) -> Unit) {
        onOkListener = cb
    }

    fun setOnDismissListener(cb: () -> Unit) {
        onDismissListener = cb
    }
}