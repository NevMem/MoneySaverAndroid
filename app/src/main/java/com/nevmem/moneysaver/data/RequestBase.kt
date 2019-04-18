package com.nevmem.moneysaver.data

interface RequestBase<T> {
    fun success(cb: (T) -> Unit)
    fun cancel()
    fun isCanceled(): Boolean
    fun resolve(result: T)
}