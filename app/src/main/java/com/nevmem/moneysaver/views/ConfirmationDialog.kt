package com.nevmem.moneysaver.views

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.confirmation_dialog_popup.view.*

class ConfirmationDialog(ctx: Context, message: String) : ConstraintLayout(ctx) {
    private lateinit var okCallback: () -> Unit
    private lateinit var dismissCallback: () -> Unit

    init {
        inflate(ctx, R.layout.confirmation_dialog_popup, this)

        okButton.setOnClickListener {
            okCallback()
        }

        dismissButton.setOnClickListener {
            dismissCallback()
        }

        popupMessage.text = message
    }

    fun setOkListener(cb: () -> Unit) {
        okCallback = cb
    }

    fun setDismissListener(cb: () -> Unit) {
        dismissCallback = cb
    }
}