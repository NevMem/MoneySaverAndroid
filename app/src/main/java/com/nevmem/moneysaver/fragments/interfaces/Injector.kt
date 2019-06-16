package com.nevmem.moneysaver.fragments.interfaces

interface Injector<T> {
    fun inject(objectInjectTo: T)
}