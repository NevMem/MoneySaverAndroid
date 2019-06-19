package com.nevmem.moneysaver.views

import android.content.Context
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.choose_one_from_list.view.*

class ChooseOneFromListDialog(ctx: Context, headerText: String, values: List<String>) : ConstraintLayout(ctx) {
    private var okListener: ((String) -> Unit)? = null
    private var dismissListener: (() -> Unit)? = null

    init {
        inflate(ctx, R.layout.choose_one_from_list, this)

        header.text = headerText

        spinner.adapter = ArrayAdapter<String>(ctx, R.layout.default_spinner_item_layout, values)

        okButton.setOnClickListener {
            val selected = spinner.selectedItem
            if (selected != null)
                okListener?.invoke(selected.toString())
        }

        dismissButton.setOnClickListener {
            dismissListener?.invoke()
        }
    }

    fun setOkListener(cb: (String) -> Unit) {
        okListener = cb
    }

    fun setDismissListener(cb: () -> Unit) {
        dismissListener = cb
    }
}