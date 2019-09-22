package com.nevmem.moneysaver.common.utils

import android.os.Handler
import android.os.Looper

abstract class ThreadUtils {
    companion object {
        fun runOnUi(lambda: ()->Unit) {
            Handler(Looper.getMainLooper()).post {
                lambda()
            }
        }
    }
}