package com.nevmem.moneysaver.data.util

import com.nevmem.moneysaver.Vars
import org.json.JSONObject

class TagsRepositoryParsers {
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
    }
}