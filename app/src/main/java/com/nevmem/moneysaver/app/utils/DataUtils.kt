package com.nevmem.moneysaver.app.utils

import com.nevmem.moneysaver.common.data.ByHourStatistics
import com.nevmem.moneysaver.common.data.Record

abstract class DataUtils {
    companion object {
        fun collectByHourStatistics(history: List<Record>): ByHourStatistics
            = ByHourStatistics(history)
    }
}