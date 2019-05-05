package com.nevmem.moneysaver.data.util

import com.nevmem.moneysaver.Vars.Companion.unknownFormat
import org.json.JSONObject

abstract class WalletsRepositoryParsers {
    companion object {
        fun parseAddWalletRequest(json: JSONObject): ParseResult {
            val type = json.optString("type") ?: ParseError(unknownFormat)
            if (type != "ok" && type != "error") return ParseError(unknownFormat)
            if (type == "error") {
                val error = json.optString("error") ?: return ParseError(unknownFormat)
                return ParseError(error)
            }
            val data = json.optString("data") ?: return ParseError(unknownFormat)
            return ParsedValue(data)
        }
    }
}