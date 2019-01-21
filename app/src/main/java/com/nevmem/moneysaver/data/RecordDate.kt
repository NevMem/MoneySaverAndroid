package com.nevmem.moneysaver.data

class RecordDate {
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var hour: Int = 0
    var minute: Int = 0

    internal constructor() {
        minute = 0
        hour = minute
        day = hour
        month = day
        year = month
    }

    internal constructor(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        this.year = year
        this.month = month
        this.day = day
        this.hour = hour
        this.minute = minute
    }

    private fun format(value: Int): String {
        var ans = Integer.valueOf(value)!!.toString()
        while (ans.length != 2) {
            ans = "0$ans"
        }
        return ans
    }

    override fun toString(): String {
        return format(day) + "." + format(month) + "." + year + " " + hour + ":" + minute
    }
}
