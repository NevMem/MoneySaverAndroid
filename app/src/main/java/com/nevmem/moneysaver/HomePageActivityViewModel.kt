package com.nevmem.moneysaver

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.nevmem.moneysaver.data.User

class HomePageActivityViewModel : ViewModel() {
     var user: MutableLiveData<User> = MutableLiveData()
}