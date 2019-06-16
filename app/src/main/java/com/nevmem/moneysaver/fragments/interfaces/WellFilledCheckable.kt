package com.nevmem.moneysaver.fragments.interfaces

import com.nevmem.moneysaver.data.util.FillInfo

interface WellFilledCheckable {
    fun isWellFilled() : FillInfo
}