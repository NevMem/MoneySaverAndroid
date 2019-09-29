package com.nevmem.moneysaver.network

interface Request<T> {
    fun success(cb: (T) -> Unit)
    fun cancel()
    fun isCanceled(): Boolean
    fun resolve(result: T)
}