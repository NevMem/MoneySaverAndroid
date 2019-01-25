package com.nevmem.moneysaver.exceptions

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class AddRecordPageViewModel : ViewModel() {
    var nameError: MutableLiveData<String> = MutableLiveData()
    var valueError: MutableLiveData<String> = MutableLiveData()

    init {
        nameError.value = ""
        valueError.value = ""
    }
}