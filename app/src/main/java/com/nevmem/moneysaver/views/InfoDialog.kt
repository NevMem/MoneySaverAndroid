package com.nevmem.moneysaver.views

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.view.View
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.confirmation_dialog_popup.view.*

class InfoDialog(ctx: Context, message: String) : ConstraintLayout(ctx) {
    private lateinit var okCallback: () -> Unit

    init {
        inflate(ctx, R.layout.info_dialog_popup, this)

        okButton.setOnClickListener {
            okCallback()
        }

        popupMessage.text = message
    }

    fun setOkListener(cb: () -> Unit) {
        okCallback = cb
    }
}