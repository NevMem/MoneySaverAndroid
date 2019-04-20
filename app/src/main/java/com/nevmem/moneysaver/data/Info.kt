package com.nevmem.moneysaver.data

import android.util.Log.i
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nevmem.moneysaver.room.converters.InfoConverter
import org.json.JSONObject

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

        if (data.has("daySum")) {
            val sum = data.getJSONObject("daySum")
            println(sum.toString())
            for (key in sum.keys()) {
                sumDay.add(sum.getDouble(key))
            }
        }
    }
}