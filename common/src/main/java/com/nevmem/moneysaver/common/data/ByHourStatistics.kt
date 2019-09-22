package com.nevmem.moneysaver.common.data

class ByHourStatistics(history: List<Record>) {
    private val list = ArrayList<HashMap<String, Int>>()

    init {
        for (i in 0 .. 24) {
            list.add(HashMap())
        }
        history.forEach {
            val map = list[it.date.hour]
            map[it.tag] = (map[it.tag] ?: 0) + 1
        }
    }

    fun hourStats(hour: Int): HashMap<String, Int> = list[hour]
}