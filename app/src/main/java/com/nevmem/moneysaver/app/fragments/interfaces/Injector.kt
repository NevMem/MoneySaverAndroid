package com.nevmem.moneysaver.app.fragments.interfaces

interface Injector<T> {
    fun inject(objectInjectTo: T)
}