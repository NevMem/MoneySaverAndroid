package com.nevmem.moneysaver

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.nevmem.moneysaver.data.User

class HomePageActivityViewModel : ViewModel() {
    var user: MutableLiveData<User> = MutableLiveData()
    var totalSpend: MutableLiveData<Double> = MutableLiveData()
    var amountOfDays: MutableLiveData<Int> = MutableLiveData()
    var averageSpend: MutableLiveData<Double> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
}