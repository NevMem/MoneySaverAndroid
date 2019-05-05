package com.nevmem.moneysaver.views

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.one_string_dialog.view.*

class OneStringDialog(ctx: Context, attrs: AttributeSet?) : LinearLayout(ctx, attrs) {
    constructor(ctx: Context): this(ctx, null)

    private var onOkListener: ((String) -> Unit)? = null
    private var onDismissListener: (() -> Unit)? = null

    var headerString: String = ""
        set(value) {
            field = value
            header?.text = value
        }

    var descriptionString: String = ""
        set(value) {
            field = value
            description?.text = value
        }

    init {
        inflate(ctx, R.layout.one_string_dialog, this)
        okButton.setOnClickListener {
            onOkListener?.invoke(editText.text.toString())
        }
        cancelButton.setOnClickListener {
            onDismissListener?.invoke()
        }
    }

    fun setOnOkListener(cb: (String) -> Unit) {
        onOkListener = cb
    }

    fun setOnDismissListener(cb: () -> Unit) {
        onDismissListener = cb
    }
}