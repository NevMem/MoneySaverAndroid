package com.nevmem.moneysaver.app.activity.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.app.data.MonthDescription
import com.nevmem.moneysaver.app.data.repositories.InfoRepository
import javax.inject.Inject

class MonthDescriptionViewModel(application: Application) : AndroidViewModel(application) {
    @Inject
    lateinit var infoRepo: InfoRepository

    private var app: App = application as App

    var monthDescription = MediatorLiveData<MonthDescription>()
    private var currentLiveData: LiveData<MonthDescription>

    private var index = Int.MAX_VALUE

    init {
        app.appComponent.inject(this)
        currentLiveData = Transformations.map(infoRepo.monthDescriptions()) {
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

    fun setIndex(newIndex: Int) {
        index = newIndex
        indexChanged()
    }

    fun getMonthIndex() = index

    fun prev() {
        index -= 1
        indexChanged()
    }

    fun next() {
        index += 1
        indexChanged()
    }

    private fun indexChanged() {
        monthDescription.removeSource(currentLiveData)
        currentLiveData = Transformations.map(infoRepo.monthDescriptions()) {
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
}