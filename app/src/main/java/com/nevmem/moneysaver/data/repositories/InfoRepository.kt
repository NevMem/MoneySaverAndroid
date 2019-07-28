package com.nevmem.moneysaver.data.repositories

import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.data.Info
import com.nevmem.moneysaver.data.MonthDescription
import com.nevmem.moneysaver.data.util.RequestState

interface InfoRepository {
    fun tryUpdate()
    fun monthDescriptions(): MutableLiveData<List<MonthDescription>>
    fun info(): MutableLiveData<Info>
    fun state(): MutableLiveData<RequestState>
}