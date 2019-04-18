package com.nevmem.moneysaver.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.checkable_edit_text.view.*

class CheckableEditText(ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    init {
        inflate(ctx, R.layout.checkable_edit_text, this)
    }

    var changeHandler: ((String) -> Unit)? = null

    var loading = false
        set(value) {
            field = value
            sync()
        }
    var error = ""
        set(value) {
            field = value
            sync()
        }
    var success = false
        set(value) {
            field = value
            sync()
        }

    private fun sync() {
        when {
            loading -> {
                errorIcon.visibility = View.GONE
                successIcon.visibility = View.GONE
                processBar.visibility = View.VISIBLE
            }
            error.isNotEmpty() -> {
                processBar.visibility = View.GONE
                successIcon.visibility = View.GONE
                errorIcon.visibility = View.VISIBLE
            }
            success -> {
                processBar.visibility = View.GONE
                errorIcon.visibility = View.GONE
                successIcon.visibility = View.VISIBLE
            }
            else -> {
                errorIcon.visibility = View.GONE
                successIcon.visibility = View.GONE
                processBar.visibility = View.GONE
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                changeHandler?.let { it(s.toString()) }
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}