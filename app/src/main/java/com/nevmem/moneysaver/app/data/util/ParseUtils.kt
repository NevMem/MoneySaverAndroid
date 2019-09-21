package com.nevmem.moneysaver.app.data.util

import org.json.JSONObject

abstract class ParseUtils {
    companion object {
        fun optDouble(json: JSONObject, name: String): Double? {
            val value = json.optDouble(name)
            if (value.isNaN()) return null
            return value
        }

        fun optUInt(json: JSONObject, name: String): Int? {
            val value = json.optInt(name, -1)
            if (value == -1) return null
            return value
        }
    }
}