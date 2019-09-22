package com.nevmem.moneysaver.common.converters

import androidx.room.TypeConverter
import com.nevmem.moneysaver.common.data.RecordDate
import org.json.JSONObject

class HistoryConverter {
    @TypeConverter
    fun dateToString(date: RecordDate): String {
        return date.toJSON().toString()
    }

    @TypeConverter
    fun stringToDate(json: String): RecordDate {
        return RecordDate.fromJSON(JSONObject(json))
    }
}
