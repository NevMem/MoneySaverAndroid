package com.nevmem.moneysaver.fragments.interfaces

interface Injecter<T> {
    fun inject(objectInjectTo: T)
}