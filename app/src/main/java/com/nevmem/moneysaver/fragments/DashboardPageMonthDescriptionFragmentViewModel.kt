package com.nevmem.moneysaver.fragments

import android.app.Application
import androidx.lifecycle.*
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.data.MonthDescription
import com.nevmem.moneysaver.data.repositories.InfoRepository
import javax.inject.Inject

class DashboardPageMonthDescriptionFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private var app: App = application as App

    @Inject
    lateinit var infoRepo: InfoRepository

    var monthDescription = MediatorLiveData<MonthDescription>()
    private var currentLiveData: LiveData<MonthDescription>
    private var index = Int.MAX_VALUE

    init {
        app.appComponent.inject(this)
        currentLiveData = Transformations.map(infoRepo.monthDescriptions) {
                descriptions -> run {
            if (index >= descriptions.size)
                index = descriptions.size - 1
            if (index < 0)
                index = 0
            if (descriptions.isEmpty())
                null
            else
                descriptions[index]
        } }
        monthDescription.addSource(currentLiveData) { value -> monthDescription.postValue(value) }
    }

    fun prev() {
        index -= 1
        indexChanged()
    }

    fun next() {
        index += 1
        indexChanged()
    }

    private fun setupNewLiveData() {
        currentLiveData = Transformations.map(infoRepo.monthDescriptions) {
                descriptions -> run {
            if (index >= descriptions.size)
                index = descriptions.size - 1
            if (index < 0)
                index = 0
            if (descriptions.isEmpty())
                null
            else
                descriptions[index]
        } }
        monthDescription.addSource(currentLiveData) { value -> monthDescription.postValue(value) }
    }

    private fun indexChanged() {
        monthDescription.removeSource(currentLiveData)
        setupNewLiveData()
    }
}