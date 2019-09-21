package com.nevmem.moneysaver.app.utils

import android.graphics.Paint
import android.graphics.Rect

abstract class TextHelper {
    companion object {
        private fun getTextRect(p: Paint, text: String): Rect {
            val rect = Rect()
            p.getTextBounds(text, 0, text.length, rect)
            return rect
        }

        fun getTextWidth(p: Paint, text: String): Int = getTextRect(p, text).width()
    }
}