package com.nevmem.moneysaver.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginPageViewModel : ViewModel() {
    var error: MutableLiveData<String> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
}