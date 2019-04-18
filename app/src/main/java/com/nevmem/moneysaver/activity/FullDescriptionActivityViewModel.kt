package com.nevmem.moneysaver.activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nevmem.moneysaver.data.Record

class FullDescriptionActivityViewModel : ViewModel() {
    var record = MutableLiveData<Record>(Record())
    var needChange = MutableLiveData<Boolean>(false)
    var error = MutableLiveData<String>("")
    var success = MutableLiveData<String>("")
}