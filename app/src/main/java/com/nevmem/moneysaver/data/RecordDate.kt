package com.nevmem.moneysaver.data

import org.json.JSONObject

class RecordDate {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var hour: Int = 0
    var minute: Int = 0

    companion object {
        fun fromJSON(jsonObject: JSONObject): RecordDate {
            return RecordDate(
                jsonObject.getInt("year"), jsonObject.getInt("month"),
                jsonObject.getInt("day"), jsonObject.getInt("hour"), jsonObject.getInt("minute")
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

    private fun format(value: Int): String {
        var ans = Integer.valueOf(value).toString()
        while (ans.length != 2) {
            ans = "0$ans"
        }
        return ans
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

    override fun toString(): String {
        return format(day) + "." + format(month) + "." + year + " " + format(hour) + ":" + format(minute)
    }
}
