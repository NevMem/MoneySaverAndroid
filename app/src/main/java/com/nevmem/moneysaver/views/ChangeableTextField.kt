package com.nevmem.moneysaver.views

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log.i
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.changeable_text_field.view.*

class ChangeableTextField(private var ctx: Context, attrs: AttributeSet) : FrameLayout(ctx, attrs) {
    companion object {
        const val defaultTextSize = 24
    }

    private var onTextChanged: ((String) -> Unit)? = null

    var value: String = ""
        set(value) {
            if (field == value) return
            field = value
            editText.setText(field)
            textView.text = field
        }
        get() = editText?.text.toString()

    init {
        inflate(ctx, R.layout.changeable_text_field, this)
        setupAttributes(attrs)
        setupListeners()
    }

    private fun setupAttributes(attrs: AttributeSet) {
        val attributeGetter = ctx.theme.obtainStyledAttributes(attrs, R.styleable.CustomText,  0, 0)
        val hint = attributeGetter.getString(R.styleable.CustomText_hint) ?: ""
        editText.hint = hint
        textView.hint = hint
        val inputType = attributeGetter.getInt(R.styleable.CustomText_inputType, 0)
        when (inputType) {
            0 -> { editText.inputType = InputType.TYPE_CLASS_TEXT } // text
            1 -> { editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD } // password
            2 -> { editText.inputType = InputType.TYPE_CLASS_NUMBER } // number
            else -> { editText.inputType = InputType.TYPE_CLASS_TEXT }
        }

        val textSize = attributeGetter.getDimensionPixelSize(R.styleable.CustomText_textSize, defaultTextSize)
        editText.textSize = textSize.toFloat()
        textView.textSize = textSize.toFloat()
    }

    private fun setupListeners() {
        textView.setOnClickListener {
            textView.visibility = View.INVISIBLE
            editText.setText(textView.text)
            editText.visibility = View.VISIBLE
            val cx = editText.width / 2
            val cy = editText.height / 2
            val radius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat() + 3
            val anim = ViewAnimationUtils.createCircularReveal(editText, cx, cy, 0f, radius)
            anim.start()
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { valueChanged() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun valueChanged() {
        onTextChanged?.invoke(value)
    }

    fun setOnTextChanged(cb: (String) -> Unit) {
        onTextChanged = cb
    }
}