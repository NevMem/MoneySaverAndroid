package com.nevmem.moneysaver.app.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.loading_overlay.view.*

class LoadingOverlay(ctx: Context) : ConstraintLayout(ctx) {
    constructor(ctx: Context, attrs: AttributeSet) : this(ctx)

    enum class State { LOADING, ERROR, SUCCESS, NONE }

    private var currentState = State.NONE

    private var afterSuccess: (() -> Unit)? = null
    private var afterError: (() -> Unit)? = null
    private var onInterrupt: (() -> Unit)? = null

    init {
        inflate(ctx, R.layout.loading_overlay, this)

        back.setOnClickListener {
            when (currentState) {
                State.SUCCESS -> { afterSuccess?.invoke() }
                State.ERROR -> { afterError?.invoke() }
                State.LOADING -> { onInterrupt?.invoke() }
                else -> {
                    throw IllegalStateException("Bad loading overlay state")
                }
            }
        }
    }

    fun setAfterSuccess(cb: () -> Unit) {
        afterSuccess = cb
    }

    fun setOnInterrupt(cb: () -> Unit) {
        onInterrupt = cb
    }

    fun setAfterError(cb: () -> Unit) {
        afterError = cb
    }

    fun setLoading(message: String = "loading") {
        currentState = State.LOADING
        hideError()
        hideSuccess()
        showLoading()
        setMessage(message)
    }

    fun setError(message: String = "Error occurred") {
        currentState = State.ERROR
        hideSuccess()
        hideLoading()
        showError()
        setMessage(message)
    }

    fun setSuccess(message: String = "Success") {
        currentState = State.SUCCESS
        hideError()
        hideLoading()
        showSuccess()
        setMessage(message)
    }

    private fun setMessage(message: String) {
        overlayMessage.text = message
    }

    private fun hideError() {
        errorImage.visibility = View.GONE
    }

    private fun showError() {
        errorImage.visibility = View.VISIBLE
    }

    private fun hideSuccess() {
        successImage.visibility = View.GONE
    }

    private fun showSuccess() {
        successImage.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        loading.visibility = View.GONE
    }

    private fun showLoading() {
        loading.visibility = View.VISIBLE
    }
}