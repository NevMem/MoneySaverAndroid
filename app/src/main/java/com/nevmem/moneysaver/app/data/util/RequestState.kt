package com.nevmem.moneysaver.app.data.util

sealed class RequestState
object LoadingState : RequestState()
data class SuccessState(val success: String? = null) : RequestState()
data class ErrorState(val error: String) : RequestState()
object NoneState : RequestState()