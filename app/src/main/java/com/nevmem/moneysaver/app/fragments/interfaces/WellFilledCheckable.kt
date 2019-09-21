package com.nevmem.moneysaver.app.fragments.interfaces

import com.nevmem.moneysaver.app.data.util.FillInfo

interface WellFilledCheckable {
    fun isWellFilled() : FillInfo
}