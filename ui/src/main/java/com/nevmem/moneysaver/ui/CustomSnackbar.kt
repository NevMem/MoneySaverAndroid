package com.nevmem.moneysaver.ui

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.BaseTransientBottomBar
import kotlinx.android.synthetic.main.custom_snackbar_layout.view.*

class CustomSnackbar private constructor(
    parent: ViewGroup,
    content: View,
    callback: com.google.android.material.snackbar.ContentViewCallback
) : BaseTransientBottomBar<CustomSnackbar>(parent, content, callback) {

    init {
        view.background = null
    }

    companion object {
        fun make(parent: ViewGroup, text: String, duration: Int): CustomSnackbar {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_snackbar_layout, parent, false)
            val height = view.height +
                (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin +
                (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin
            view.snackbarText.text = text

            val snack = CustomSnackbar(parent, view, object : com.google.android.material.snackbar.ContentViewCallback {
                override fun animateContentOut(delay: Int, duration: Int) {
                    val animator = ValueAnimator.ofFloat(0f, view.height.toFloat() + height)
                    animator.addUpdateListener {
                        view.translationY = it.animatedValue as Float
                    }
                    animator.start()
                }

                override fun animateContentIn(delay: Int, duration: Int) {
                    val animator = ValueAnimator.ofFloat(view.height.toFloat() + height, 0f)
                    animator.addUpdateListener {
                        view.translationY = it.animatedValue as Float
                    }
                    animator.start()
                }
            })

            return snack
        }
    }

    fun setAction(buttonCaption: String, onClick: ()->Unit): CustomSnackbar {
        view.actionButton.visibility = View.VISIBLE
        view.actionButton.text = buttonCaption
        view.actionButton.setOnClickListener {
            onClick()
            dismiss()
        }
        return this
    }
}