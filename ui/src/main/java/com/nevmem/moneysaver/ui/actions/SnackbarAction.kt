package com.nevmem.moneysaver.ui.actions

sealed class SnackbarAction(val message: String)
class NotifySnackbarAction(message: String) : SnackbarAction(message)
class ExtendedSnackbarAction(message: String, val buttonText: String, val callback: ()->Unit)
    : SnackbarAction(message)