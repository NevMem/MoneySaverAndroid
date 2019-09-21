package com.nevmem.moneysaver.app.room.converters

import androidx.room.TypeConverter
import com.nevmem.moneysaver.app.data.RecordDate
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
