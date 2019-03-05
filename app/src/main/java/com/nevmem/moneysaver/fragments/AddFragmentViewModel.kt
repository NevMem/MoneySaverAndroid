package com.nevmem.moneysaver.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class AddFragmentViewModel : ViewModel() {
    val success = MutableLiveData<String>()
    val error = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
}