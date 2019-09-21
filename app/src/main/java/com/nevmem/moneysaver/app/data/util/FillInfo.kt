package com.nevmem.moneysaver.app.data.util

sealed class FillInfo

object FilledWell: FillInfo()
class BadFilled(val reason: String): FillInfo()