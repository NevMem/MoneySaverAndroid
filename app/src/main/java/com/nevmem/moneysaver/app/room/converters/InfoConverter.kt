package com.nevmem.moneysaver.app.room.converters

import androidx.room.TypeConverter
import java.lang.NumberFormatException

class InfoConverter {
    @TypeConverter
    fun doubleListToString(list: List<Double>): String {
        return list.joinToString()
    }

    @TypeConverter
    fun stringToDoubleArrayList(str: String): ArrayList<Double> {
        val result = ArrayList<Double>()
        str.split(",").forEach {
            try {
                val value = it.toDouble()
                result.add(value)
            } catch (_: NumberFormatException) {}
        }
        return result
    }
}