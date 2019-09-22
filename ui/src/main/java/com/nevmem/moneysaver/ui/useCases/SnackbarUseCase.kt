package com.nevmem.moneysaver.ui.useCases

import com.nevmem.moneysaver.ui.actions.SnackbarAction

interface SnackbarUseCase {
    fun execute(action: SnackbarAction)

    fun subscribe(subscriber : Callback)
    fun unsubscribe(subscriber: Callback)

    interface Callback {
        fun onActionReceived(action: SnackbarAction)
    }
}