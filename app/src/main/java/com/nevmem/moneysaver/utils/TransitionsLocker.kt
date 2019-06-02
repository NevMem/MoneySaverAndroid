package com.nevmem.moneysaver.utils

class TransitionsLocker {
    private var transition = false

    fun lockTransitions() {
        transition = true
    }

    fun unlockTransitions() {
        transition = false
    }

    fun canRunTransition() = !transition
}