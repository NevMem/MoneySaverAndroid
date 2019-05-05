package com.nevmem.moneysaver.fragments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.repositories.HistoryRepository
import com.nevmem.moneysaver.data.util.*
import javax.inject.Inject

class AddFragmentViewModel(application: Application) : AndroidViewModel(application) {
    var addingState = MutableLiveData<RequestState>(NoneState)
    private var app: App = application as App

    @Inject
    lateinit var historyRepo: HistoryRepository

    init {
        app.appComponent.inject(this)
    }

    fun add(record: Record) {
        addingState.postValue(LoadingState)
        historyRepo.addRecord(record) {
            if (it == null) {
                addingState.postValue(SuccessState("Successfully added record"))
            } else {
                addingState.postValue(ErrorState(it))
            }
        }
    }

    fun receivedInfo() {
        addingState.postValue(NoneState)
    }
}