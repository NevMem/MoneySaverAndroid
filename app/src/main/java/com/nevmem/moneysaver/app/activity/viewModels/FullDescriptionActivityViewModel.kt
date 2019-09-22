package com.nevmem.moneysaver.app.activity.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.common.data.Record
import com.nevmem.moneysaver.common.data.RecordDate
import com.nevmem.moneysaver.app.data.repositories.HistoryRepository
import javax.inject.Inject

class FullDescriptionActivityViewModel(app: Application) : AndroidViewModel(app) {
    var record = MutableLiveData<Record>(Record())

    fun editingState() = historyRepo.editingState()

    private var changedName: String? = null
    private var changedValue: Double? = null

    @Inject
    lateinit var historyRepo: HistoryRepository

    var id: String = ""
        set(value) {
            field = value
            idChanged()
        }

    init {
        (app as App).appComponent.inject(this)
    }

    fun setName(name: String) {
        changedName = name
    }

    fun setValue(value: Double) {
        changedValue = value
    }

    private fun insertChangedFields(record: Record) {
        changedName?.let { record.name = it }
        changedValue?.let { record.value = it }
    }

    fun setDaily(daily: Boolean) {
        val currentRecord = record.value ?: return
        currentRecord.daily = daily
        insertChangedFields(currentRecord)
        record.postValue(currentRecord)
    }

    fun setYear(year: Int) {
        val currentRecord = record.value ?: return
        currentRecord.date.year = year
        insertChangedFields(currentRecord)
        record.postValue(currentRecord)
    }

    fun setMonth(month: Int) {
        val currentRecord = record.value ?: return
        currentRecord.date.month = month
        insertChangedFields(currentRecord)
        record.postValue(currentRecord)
    }

    fun setDay(day: Int) {
        val currentRecord = record.value ?: return
        currentRecord.date.day = day
        insertChangedFields(currentRecord)
        record.postValue(currentRecord)
    }

    fun setHour(hour: Int) {
        val currentRecord = record.value ?: return
        currentRecord.date.hour = hour
        insertChangedFields(currentRecord)
        record.postValue(currentRecord)
    }

    fun setMinute(minute: Int) {
        val currentRecord = record.value ?: return
        currentRecord.date.minute = minute
        insertChangedFields(currentRecord)
        record.postValue(currentRecord)
    }

    fun save() {
        val currentRecord = record.value ?: return
        insertChangedFields(currentRecord)
        historyRepo.editRecord(currentRecord)
    }

    fun currentDate(): RecordDate {
        val currentRecord = record.value ?: return RecordDate()
        return currentRecord.date
    }

    fun stopEditing() {
        historyRepo.stopEditing()
    }

    private fun idChanged() {
        val allHistory = historyRepo.history().value ?: return
        allHistory.forEach {
            if (it.id == id)
                record.postValue(it)
        }
    }
}