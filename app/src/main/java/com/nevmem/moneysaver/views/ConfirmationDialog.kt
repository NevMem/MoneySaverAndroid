package com.nevmem.moneysaver.views

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.View
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.confirmation_dialog_popup.view.*

class ConfirmationDialog(ctx: Context, message: String, okCallback: () -> Unit, dismissCallback: () -> Unit) : ConstraintLayout(ctx) {

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
}