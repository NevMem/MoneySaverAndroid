package com.nevmem.moneysaver.app.utils

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