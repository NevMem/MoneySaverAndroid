package com.nevmem.moneysaver.app.data.repositories

import androidx.lifecycle.LiveData
import com.nevmem.moneysaver.app.data.Info
import com.nevmem.moneysaver.app.data.MonthDescription
import com.nevmem.moneysaver.app.data.util.RequestState

interface InfoRepository {
    fun tryUpdate()
    fun monthDescriptions(): LiveData<List<MonthDescription>>
    fun info(): LiveData<Info>
    fun state(): LiveData<RequestState>
}