package com.nevmem.moneysaver.app.views.utils

import android.app.Activity
import android.view.View
import androidx.annotation.LayoutRes

abstract class DialogsHelper {
    companion object {
        fun createView(activity: Activity, @LayoutRes resId: Int): View {
            return activity.layoutInflater.inflate(resId, null)
        }
    }
}