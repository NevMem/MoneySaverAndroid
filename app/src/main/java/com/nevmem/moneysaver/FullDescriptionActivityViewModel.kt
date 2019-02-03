package com.nevmem.moneysaver

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class FullDescriptionActivityViewModel : ViewModel() {
    var prevMonth = MutableLiveData<String>()
    var currentMonth = MutableLiveData<String>()

    var prevYear = MutableLiveData<String>()
    var currentYear = MutableLiveData<String>()

    var prevDay = MutableLiveData<String>()
    var currentDay = MutableLiveData<String>()

    var prevHour = MutableLiveData<String>()
    var currentHour = MutableLiveData<String>()

    var prevMinute= MutableLiveData<String>()
    var currentMinute = MutableLiveData<String>()

    var needChange = MutableLiveData<Boolean>()

    var error = MutableLiveData<String>()
    var success = MutableLiveData<String>()

    init {
        needChange.value = false
    }
}