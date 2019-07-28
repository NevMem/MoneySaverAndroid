package com.nevmem.moneysaver.data.util

import android.util.Log
import com.nevmem.moneysaver.Vars
import org.json.JSONObject

abstract class TagsRepositoryParsers {
    companion object {
        fun parseAddTagResponse(json: JSONObject): ParseResult {
            val type = json.optString("type") ?: return ParseError(Vars.unknownFormat)
            if (type == "ok") {
                val data = json.optString("data") ?: return ParseError(Vars.unknownFormat)
                return ParsedValue(data)
            }
            if (type == "error") {
                val error = json.optString("error") ?: return ParseError(Vars.unknownFormat)
                return ParseError(error)
            } else {
                return ParseError(Vars.unknownFormat)
            }
        }

        fun parseRemoveTagResponse(json: JSONObject): ParseResult {
            val type = json.optString("type") ?: return ParseError(Vars.unknownFormat)
            if (type == "ok") {
                return ParsedValue("ok")
            }
            if (type == "error") {
                val error = json.optString("error") ?: return ParseError(Vars.unknownFormat)
                return ParseError(error)
            }
            return ParseError(Vars.unknownFormat)
        }
    }
}