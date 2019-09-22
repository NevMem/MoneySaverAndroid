package com.nevmem.moneysaver.app.room.converters

import androidx.room.TypeConverter
import org.json.JSONObject

class MonthDescriptionTypeConverters {
    @TypeConverter
    fun fromHashMapToString(map: HashMap<String, Double>): String {
        val json = JSONObject()
        for (key in map.keys)
            json.put(key, map[key])
        return json.toString()
    }

    @TypeConverter
    fun fromStringToHashMap(jsonString: String): HashMap<String, Double> {
        val json = JSONObject(jsonString)
        val map = HashMap<String, Double>()
        for (key in json.keys()) {
            map[key] = json.getDouble(key)
        }
        return map
    }
}
