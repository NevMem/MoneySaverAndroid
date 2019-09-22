package com.nevmem.moneysaver.app.controllers

import com.nevmem.moneysaver.ui.actions.SnackbarAction
import com.nevmem.moneysaver.ui.useCases.SnackbarUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarUseCaseImpl @Inject constructor() : SnackbarUseCase {
    private val listeners = ArrayList<SnackbarUseCase.Callback>()

    override fun execute(action: SnackbarAction) {
        listeners.forEach {
            it.onActionReceived(action)
        }
    }

    override fun subscribe(subscriber: SnackbarUseCase.Callback) {
        listeners.add(subscriber)
    }

    override fun unsubscribe(subscriber: SnackbarUseCase.Callback) {
        listeners.remove(subscriber)
    }
}