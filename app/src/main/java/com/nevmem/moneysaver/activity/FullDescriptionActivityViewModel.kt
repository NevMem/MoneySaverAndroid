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
    var needChange = MutableLiveData<Boolean>(false)

    var changeableRecord = Record()

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

    fun setName(name: String) {
        changeableRecord.name = name
    }

    fun setValue(value: Double) {
        changeableRecord.value = value
    }

    fun setWallet(wallet: String) {
        changeableRecord.wallet = wallet
    }

    fun setTag(tag: String) {
        changeableRecord.tag = tag
    }

    fun setDate(date: RecordDate) {
        changeableRecord.date = date
    }

    fun setDaily(daily: Boolean) {
        changeableRecord.daily = daily
    }

    fun save() {
        historyRepo.editRecord(changeableRecord)
    }

    private fun postRecord(record: Record) {
        changeableRecord = record
        this.record.postValue(record)
    }

    fun currentDate(): RecordDate = changeableRecord.date

    private fun indexChanged() {
        val allHistory = historyRepo.history.value ?: run {
            return
        }
        if (index != -1 && index >= 0 && index < allHistory.size) {
            postRecord(allHistory[index])
        }
    }

}