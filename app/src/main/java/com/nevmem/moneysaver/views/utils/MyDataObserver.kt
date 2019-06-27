package com.nevmem.moneysaver.views.utils

class MyDataObserver {
    private var mObserver: (() -> Unit)? = null

    fun observe(cb: () -> Unit) {
        mObserver = cb
    }

    fun fireNotifications() {
        mObserver?.invoke()
    }
}