package com.nevmem.moneysaver.utils

import android.util.DisplayMetrics
import android.util.TypedValue
import com.nevmem.moneysaver.fragments.HistoryFragment

abstract class UnitsHelper {
    companion object {
        fun fromDp(dp: Float, displayMetrics: DisplayMetrics): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)

        fun fromSp(dp: Float, displayMetrics: DisplayMetrics): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, displayMetrics)
    }
}