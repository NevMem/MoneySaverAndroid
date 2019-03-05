package com.nevmem.moneysaver

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.nevmem.moneysaver.data.Record

class MainPageViewModel : ViewModel() {
    var average30Days = MutableLiveData<Double>()
    var average7Days = MutableLiveData<Double>()
    var average = MutableLiveData<Double>()
    var totalSum = MutableLiveData<Double>()
    var daySum = MutableLiveData<ArrayList<Float>>()
    var trackedDays = MutableLiveData<Double>()
    var records: MutableLiveData<ArrayList<Record>> = MutableLiveData()

    init {
        System.out.println("Some info from MainPageViewModel info")
    }
}