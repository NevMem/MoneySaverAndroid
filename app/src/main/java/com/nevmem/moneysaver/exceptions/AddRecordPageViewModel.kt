package com.nevmem.moneysaver.exceptions

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class AddRecordPageViewModel : ViewModel() {
    var nameError: MutableLiveData<String> = MutableLiveData()
    var valueError: MutableLiveData<String> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()
    var success: MutableLiveData<String> = MutableLiveData()

    init {
        nameError.value = ""
        valueError.value = ""
        loading.value = false
        error.value = ""
        success.value = ""
    }
}