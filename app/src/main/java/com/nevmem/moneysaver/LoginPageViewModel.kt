package com.nevmem.moneysaver

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class LoginPageViewModel : ViewModel() {
    var error: MutableLiveData<String> = MutableLiveData<String>()
}