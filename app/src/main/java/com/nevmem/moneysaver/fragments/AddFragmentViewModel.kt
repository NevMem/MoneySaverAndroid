package com.nevmem.moneysaver.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddFragmentViewModel : ViewModel() {
    val success = MutableLiveData<String>()
    val error = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
}