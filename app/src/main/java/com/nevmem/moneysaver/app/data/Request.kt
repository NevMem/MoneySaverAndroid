package com.nevmem.moneysaver.app.data

import android.util.Log.i

class Request<T> : RequestBase<T> {
    private var tag = "NET_REQUEST"
    private var canceled = false
    private var cb: ((T) -> Unit)? = null

    override fun cancel() {
        i(tag, "Canceled one request")
        canceled = true
    }

    override fun isCanceled() = canceled

    override fun success(cb: (T) -> Unit) {
        i(tag, "Setting callback")
        this.cb = cb
    }

    override fun resolve(result: T) {
        if (cb == null) {
            i(tag, "Callback is null")
        } else {
            i(tag, "Callback is not null")
        }
        cb?.invoke(result)
    }
}