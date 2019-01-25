package com.nevmem.moneysaver

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log.i
import com.nevmem.moneysaver.data.User

class HomePageActivityViewModel : ViewModel() {
    var user: MutableLiveData<User> = MutableLiveData()
    var totalSpend: MutableLiveData<Double> = MutableLiveData()
    var amountOfDays: MutableLiveData<Int> = MutableLiveData()
    var averageSpend: MutableLiveData<Double> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var changingAddButtonState: MutableLiveData<Boolean> = MutableLiveData()

    init {
        changingAddButtonState.value = false
        i("HOME_PAGE_VIEW_MODEL", "initing")
    }
}