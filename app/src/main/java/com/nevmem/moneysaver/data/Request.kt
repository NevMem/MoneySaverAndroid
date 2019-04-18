package com.nevmem.moneysaver.data

import android.util.Log.i

class Request<T> : RequestBase<T> {
    private var tag = "REQUEST_BASE"
    private var canceled = false
    private var cb: ((T) -> Unit)? = null

    override fun cancel() {
        i(tag, "Canceled one request")
        canceled = true
    }

    override fun isCanceled() = canceled

    override fun success(cb: (T) -> Unit) {
        this.cb = cb
    }

    override fun resolve(result: T) {
        cb?.invoke(result)
    }
}