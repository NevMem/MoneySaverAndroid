package com.nevmem.moneysaver.data

import android.util.Log.i
import org.json.JSONObject

class Info {
    var ready: Boolean = false
    var average30Days: Double? = null
    var sum30Days: Double? = null
    var average7Days: Double? = null
    var sum7Days: Double? = null
    var trackedDays: Int? = null
    var totalSpend: Double? = null
    var average: Double? = null
    var sumDay: ArrayList<Double>

    init {
        i("INFO", "init()")
        sumDay = ArrayList()
    }

    fun fromJSON(data: JSONObject) {
        average30Days = null
        average7Days = null
        trackedDays = null
        totalSpend = null
        sumDay.clear()

        if (data.has("average30Days"))
            average30Days = data.getDouble("average30Days")
        if (data.has("sum30Days"))
            sum30Days = data.getDouble("sum30Days")

        if (data.has("average7Days"))
            average7Days = data.getDouble("average7Days")
        if (data.has("sum7Days"))
            sum7Days = data.getDouble("sum7Days")

        if (data.has("amountOfDays"))
            trackedDays = data.getInt("amountOfDays")
        if (data.has("totalSpend"))
            totalSpend = data.getDouble("totalSpend")
        if (data.has("average"))
            average = data.getDouble("average")

        val sum = data.getJSONObject("daySum")
        println(sum.toString())
        for (key in sum.keys()) {
            sumDay.add(sum.getDouble(key))
        }

        ready = true
    }
}