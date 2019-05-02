package com.nevmem.moneysaver.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nevmem.moneysaver.App
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.RecordDate
import com.nevmem.moneysaver.data.repositories.HistoryRepository
import javax.inject.Inject

class FullDescriptionActivityViewModel(app: Application) : AndroidViewModel(app) {
    var record = MutableLiveData<Record>(Record())

    fun editingState() = historyRepo.editingState

    @Inject
    lateinit var historyRepo: HistoryRepository

    var index: Int = -1
        set(value) {
            field = value
            indexChanged()
        }

    init {
        (app as App).appComponent.inject(this)
    }

    fun setDaily(daily: Boolean) {
        val currentRecord = record.value ?: return
        currentRecord.daily = daily
        record.postValue(currentRecord)
    }

    fun setYear(year: Int) {
        val currentRecord = record.value ?: return
        currentRecord.date.year = year
        record.postValue(currentRecord)
    }

    fun setMonth(month: Int) {
        val currentRecord = record.value ?: return
        currentRecord.date.month = month
        record.postValue(currentRecord)
    }

    fun setDay(day: Int) {
        val currentRecord = record.value ?: return
        currentRecord.date.day = day
        record.postValue(currentRecord)
    }

    fun setHour(hour: Int) {
        val currentRecord = record.value ?: return
        currentRecord.date.hour = hour
        record.postValue(currentRecord)
    }

    fun setMinute(minute: Int) {
        val currentRecord = record.value ?: return
        currentRecord.date.minute = minute
        record.postValue(currentRecord)
    }

    fun save() {
        val currentRecord = record.value ?: return
        historyRepo.editRecord(currentRecord)
    }

    private fun postRecord(updRecord: Record) {
        record.postValue(updRecord)
    }

    fun currentDate(): RecordDate {
        val currentRecord = record.value ?: return RecordDate()
        return currentRecord.date
    }

    private fun indexChanged() {
        val allHistory = historyRepo.history.value ?: run {
            return
        }
        if (index != -1 && index >= 0 && index < allHistory.size) {
            postRecord(allHistory[index])
        }
    }

}