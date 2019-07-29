package com.nevmem.moneysaver.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.nevmem.moneysaver.data.Info
import com.nevmem.moneysaver.data.MonthDescription
import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.util.RequestState
import javax.inject.Inject

class OfflineInfoRepositoryImpl @Inject constructor(
    private var historyRepository: HistoryRepository
) : InfoRepository {
    init {
        historyRepository.tryUpdate()
    }

    override fun tryUpdate() {
        historyRepository.tryUpdate()
    }

    override fun info(): LiveData<Info> {
        return Transformations.map(historyRepository.history) { records -> makeInfo(records) }
    }

    override fun monthDescriptions(): LiveData<List<MonthDescription>> {
        return Transformations.map(historyRepository.history) { records -> makeMonthDescriptions(records) }
    }

    override fun state(): LiveData<RequestState> {
        
    }

    private fun makeInfo(records: ArrayList<Record>): Info {
        return Info()
    }

    private fun makeMonthDescriptions(records: ArrayList<Record>): List<MonthDescription> {
        return ArrayList()
    }
}