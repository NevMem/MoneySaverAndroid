package com.nevmem.moneysaver.data.repositories

import androidx.lifecycle.LiveData
import com.nevmem.moneysaver.data.Info
import com.nevmem.moneysaver.data.MonthDescription
import com.nevmem.moneysaver.data.util.RequestState

interface InfoRepository {
    fun tryUpdate()
    fun monthDescriptions(): LiveData<List<MonthDescription>>
    fun info(): LiveData<Info>
    fun state(): LiveData<RequestState>
}