package com.nevmem.moneysaver.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.nevmem.moneysaver.R
import kotlinx.android.synthetic.main.loading_overlay.view.*
import kotlinx.android.synthetic.main.loading_overlay.view.errorImage
import kotlinx.android.synthetic.main.loading_overlay.view.successImage

class LoadingOverlay(private var ctx: Context, attrs: AttributeSet) : ConstraintLayout(ctx, attrs) {
    init {
        inflate(ctx, R.layout.loading_overlay, this)
    }

    fun setLoading(message: String = "loading") {
        hideError()
        hideSuccess()
        showLoading()
        setMessage(message)
    }

    fun setError(message: String = "Error occurred") {
        hideSuccess()
        hideLoading()
        showError()
        setMessage(message)
    }

    fun setSuccess(message: String = "Success") {
        hideError()
        hideLoading()
        showLoading()
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