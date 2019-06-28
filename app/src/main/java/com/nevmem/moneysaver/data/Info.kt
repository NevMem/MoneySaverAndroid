package com.nevmem.moneysaver.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nevmem.moneysaver.room.converters.InfoConverter

@Entity
class Info {
    @PrimaryKey var uid: Int = 0
    var average30Days: Double? = null
    var sum30Days: Double? = null
    var average7Days: Double? = null
    var sum7Days: Double? = null
    var trackedDays: Int? = null
    var totalSpend: Double? = null
    var average: Double? = null
    @TypeConverters(InfoConverter::class)
    var sumDay: ArrayList<Double> = ArrayList()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Info

        if (average30Days != other.average30Days) return false
        if (sum30Days != other.sum30Days) return false
        if (average7Days != other.average7Days) return false
        if (sum7Days != other.sum7Days) return false
        if (trackedDays != other.trackedDays) return false
        if (totalSpend != other.totalSpend) return false
        if (average != other.average) return false
        if (sumDay != other.sumDay) return false

        return true
    }

    override fun hashCode(): Int {
        var result = average30Days?.hashCode() ?: 0
        result = 31 * result + (sum30Days?.hashCode() ?: 0)
        result = 31 * result + (average7Days?.hashCode() ?: 0)
        result = 31 * result + (sum7Days?.hashCode() ?: 0)
        result = 31 * result + (trackedDays ?: 0)
        result = 31 * result + (totalSpend?.hashCode() ?: 0)
        result = 31 * result + (average?.hashCode() ?: 0)
        result = 31 * result + sumDay.hashCode()
        return result
    }

    override fun toString(): String {
        return "Info(average30Days=$average30Days,\nsum30Days=$sum30Days,\naverage7Days=$average7Days,\nsum7Days=$sum7Days,\ntrackedDays=$trackedDays,\ntotalSpend=$totalSpend,\naverage=$average,\nsumDay=$sumDay)"
    }
}