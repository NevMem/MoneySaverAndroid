package com.nevmem.moneysaver.app.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.nevmem.moneysaver.app.data.Info
import com.nevmem.moneysaver.app.data.MonthDescription
import com.nevmem.moneysaver.common.data.Record
import com.nevmem.moneysaver.common.data.RecordDate
import com.nevmem.moneysaver.app.data.util.*
import com.nevmem.moneysaver.common.utils.DateHelper
import java.lang.IllegalStateException
import javax.inject.Inject

class OfflineInfoRepositoryImpl @Inject constructor() : InfoRepository {
    @Inject
    lateinit var historyRepository: HistoryRepository

    companion object {
        const val DEFAULT_SUM_DAY_SIZE = 30
    }

    enum class RepoState {
        ErrorState, LoadingState, NoneState
    }

    private var currentState = RepoState.NoneState

    override fun tryUpdate() {
        historyRepository.tryUpdate()
    }

    override fun info(): LiveData<Info> {
        return Transformations.map(historyRepository.history()) { records -> makeInfo(records) }
    }

    override fun monthDescriptions(): LiveData<List<MonthDescription>> {
        return Transformations.map(historyRepository.history()) { records -> makeMonthDescriptions(records) }
    }

    override fun state(): LiveData<RequestState> {
        val mediator = MediatorLiveData<RequestState>()

        mediator.addSource(historyRepository.error()) { value -> run {
            if (value.isNotEmpty()) {
                mediator.postValue(ErrorState(value))
                currentState = RepoState.ErrorState
            }
        }}
        mediator.addSource(historyRepository.loading()) { value -> run {
            if (value) {
                mediator.postValue(LoadingState)
                currentState = RepoState.LoadingState
            } else {
                if (currentState != RepoState.ErrorState) {
                    mediator.postValue(NoneState)
                }
            }
        }}

        return mediator
    }

    private fun makeInfo(records: List<Record>): Info {
        val currentDate = RecordDate.currentDate()
        val weekBefore = DateHelper.dayBefore(currentDate, 7)
        val thirtyDaysBefore = DateHelper.dayBefore(currentDate, 30)

        var totalSpend = 0.0
        var weekSpend = 0.0
        var thirtyDaysSpend = 0.0
        var minDate: RecordDate? = null
        var totalDaily = 0.0

        val sumDay = ArrayList<Double>()
        for (i in 0 until(DEFAULT_SUM_DAY_SIZE)) {
            sumDay.add(0.0)
        }

        val sumDayDate = DateHelper.dayBefore(currentDate, DEFAULT_SUM_DAY_SIZE)

        for (i in 0 until(records.size)) {
            val it = records[i]

            totalSpend += it.value

            if (it.daily) {
                totalDaily += it.value
            }

            if (it.date in weekBefore..currentDate) {
                weekSpend += it.value
            }
            if (it.date in thirtyDaysBefore..currentDate) {
                thirtyDaysSpend += it.value
            }
            if (it.date in sumDayDate..currentDate) {
                val index = DateHelper.amountOfDaysBetween(sumDayDate, it.date) - 1
                sumDay[index] += it.value
            }

            if (minDate == null) {
                minDate = it.date
            } else {
                if (minDate > it.date) {
                    minDate = it.date
                }
            }
        }

        val amountOfDays = DateHelper.amountOfDaysBetween(minDate, currentDate)

        val info = Info()
        info.totalSpend = totalSpend
        info.sum7Days = weekSpend
        info.sum30Days = thirtyDaysSpend
        info.dailySum = totalDaily

        if (minDate != null) {
            if (minDate <= weekBefore) {
                info.average7Days = weekSpend / 7
            } else {
                info.average7Days = weekSpend / DateHelper.amountOfDaysBetween(minDate, currentDate)
            }

            if (minDate <= thirtyDaysBefore) {
                info.average30Days = thirtyDaysSpend / 30
            } else {
                info.average30Days = thirtyDaysSpend / DateHelper.amountOfDaysBetween(minDate, currentDate)
            }
        }

        if (amountOfDays != 0) {
            info.average = totalSpend / amountOfDays
            info.dailyAverage = totalDaily / amountOfDays
        }
        info.sumDay = sumDay
        info.trackedDays = amountOfDays

        return info
    }

    private fun makeMonthDescriptions(records: List<Record>): List<MonthDescription> {
        val result = ArrayList<MonthDescription>()

        val map = HashMap<String, MonthDescription>()
        records.forEach {
            val monthId = "${it.date.year}.${it.date.month}"

            if (!map.containsKey(monthId)) {
                map[monthId] = MonthDescription()
                map[monthId]!!.monthId = monthId
            }
            if (!map[monthId]!!.byTagTotal.containsKey(it.tag)) {
                map[monthId]!!.byTagTotal[it.tag] = 0.0
            }
            if (!map[monthId]!!.byTagDaily.containsKey(it.tag)) {
                map[monthId]!!.byTagDaily[it.tag] = 0.0
            }

            map[monthId]!!.total = map[monthId]!!.total + it.value
            map[monthId]!!.byTagTotal[it.tag] = map[monthId]!!.byTagTotal[it.tag]!! + it.value

            if (it.daily) {
                map[monthId]!!.totalDaily = map[monthId]!!.totalDaily + it.value
                map[monthId]!!.byTagDaily[it.tag] = map[monthId]!!.byTagDaily[it.tag]!! + it.value
            }
        }

        val monthIds = ArrayList<String>()
        for (key in map.keys) {
            monthIds.add(key)
        }
        monthIds.sortWith(object: Comparator<String> {
            override fun compare(first: String, second: String): Int {
                if (first == second)
                    return 0
                val firstSplited = first.split(".")
                val secondSplited = second.split(".")
                if (firstSplited.size != 2)
                    throw IllegalStateException("Wrong month id ${firstSplited.size}")
                if (secondSplited.size != 2)
                    throw IllegalStateException("Wrong month id ${secondSplited.size}")
                val firstYear = firstSplited[0].toInt()
                val secondYear = secondSplited[0].toInt()
                if (firstYear != secondYear)
                    return firstYear.compareTo(secondYear)
                return firstSplited[1].toInt().compareTo(secondSplited[1].toInt())
            }
        })

        for (monthId in monthIds) {
            val current = map[monthId]
            if (current != null)
                result.add(current)
        }

        return result
    }
}
