package com.nevmem.moneysaver.common.data

import com.nevmem.moneysaver.common.utils.DateHelper
import org.json.JSONObject
import java.util.*

class RecordDate : Comparable<RecordDate> {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var hour: Int = 0
    var minute: Int = 0

    companion object {
        fun fromJSON(json: JSONObject): RecordDate {
            val date = RecordDate()
            date.year = json.optInt("year")
            date.month = json.optInt("month")
            date.day = json.optInt("day")
            date.hour = json.optInt("hour")
            date.minute = json.optInt("minute")
            return date
        }

        fun currentDate(): RecordDate {
            val curCalendar = Calendar.getInstance()
            return RecordDate(
                curCalendar.get(Calendar.YEAR),
                curCalendar.get(Calendar.MONTH) + 1,
                curCalendar.get(Calendar.DATE),
                curCalendar.get(Calendar.HOUR_OF_DAY),
                curCalendar.get(Calendar.MINUTE)
            )
        }
    }

    constructor()

    constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        this.year = year
        this.month = month
        this.day = day
        this.hour = hour
        this.minute = minute
    }

    constructor(other: RecordDate): this(other.year, other.month, other.day, other.hour, other.minute)

    fun toJSON(): JSONObject {
        val json = JSONObject()
        json.put("year", year)
        json.put("month", month)
        json.put("day", day)
        json.put("hour", hour)
        json.put("minute", minute)
        return json
    }

    fun injectJson(json: JSONObject) {
        json.put("year", year)
        json.put("month", month)
        json.put("day", day)
        json.put("hour", hour)
        json.put("minute", minute)
    }

    fun nextDay(inPlace: Boolean = false): RecordDate {
        var result = this
        if (!inPlace)
            result = RecordDate(this)

        if (result.day == DateHelper.getAmountOfDaysInMonth(result.year, result.month)) {
            result.day = 1
            if (result.month == 12) {
                result.month = 1
                result.year += 1
            } else {
                result.month += 1
            }
        } else {
            result.day += 1
        }

        return this
    }

    fun dateString(): String = DateHelper.fillTo2Length(day) + "." + DateHelper.fillTo2Length(month) + "." + year

    fun timeString(): String = DateHelper.fillTo2Length(hour) + ":" + DateHelper.fillTo2Length(minute)

    override fun compareTo(other: RecordDate): Int {
        if (year != other.year)
            return year.compareTo(other.year)
        if (month != other.month)
            return month.compareTo(other.month)
        if (day != other.day)
            return day.compareTo(other.day)
        if (hour != other.hour)
            return hour.compareTo(other.hour)
        return minute.compareTo(other.minute)
    }

    override fun toString(): String = dateString() + " " + timeString()

    override fun equals(other: Any?): Boolean {
        return if (other is RecordDate) {
            other.year == year && other.month == month && other.day == day &&
                    other.hour == hour && other.minute == minute
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }
}
