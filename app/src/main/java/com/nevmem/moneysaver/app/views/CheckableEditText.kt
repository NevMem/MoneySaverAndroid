package com.nevmem.moneysaver.app.views

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.checkable_edit_text.view.*

class CheckableEditText(ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    private var hint = ""
    private var inputType: Int = 0

    init {
        inflate(ctx, R.layout.checkable_edit_text, this)
        val gt = ctx.theme.obtainStyledAttributes(attrs, R.styleable.CustomText, 0, 0)
        hint = gt.getString(R.styleable.CustomText_hint) ?: ""
        inputType = gt.getInt(R.styleable.CustomText_inputType, inputType)
        gt.recycle()
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
    val text: String
        get() = editText.text.toString()

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

        editText.hint = hint
        if (inputType == 1) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                changeHandler?.let { it(s.toString()) }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}