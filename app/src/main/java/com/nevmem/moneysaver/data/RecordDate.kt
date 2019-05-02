package com.nevmem.moneysaver.data

import com.nevmem.moneysaver.data.util.DateHelper
import org.json.JSONObject
import java.util.*

class RecordDate {
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

    internal constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        this.year = year
        this.month = month
        this.day = day
        this.hour = hour
        this.minute = minute
    }

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

    fun dateString(): String = DateHelper.fillTo2Length(day) + "." + DateHelper.fillTo2Length(month) + "." + year

    fun timeString(): String = DateHelper.fillTo2Length(hour) + ":" + DateHelper.fillTo2Length(minute)

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
