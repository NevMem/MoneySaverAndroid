package com.nevmem.moneysaver.app.data

interface RequestBase<T> {
    fun success(cb: (T) -> Unit)
    fun cancel()
    fun isCanceled(): Boolean
    fun resolve(result: T)
}