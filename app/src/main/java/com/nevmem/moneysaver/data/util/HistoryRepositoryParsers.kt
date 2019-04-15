package com.nevmem.moneysaver.data.util

import com.nevmem.moneysaver.data.Record
import com.nevmem.moneysaver.data.RecordDate
import org.json.JSONObject

abstract class HistoryRepositoryParsers {
    companion object {
        const val unknownFormat = "Server response has unknown format"
        const val unspecifiedData = "Server response has bad format(data array is missing)"
        const val corruptedRecord = "One or more record were corrupted"

        fun parseServerLoadedResponse(json: JSONObject): ParseResult {
            val parsed: ArrayList<Record> = ArrayList()

            if (!json.has("type")) {
                return ParseError(unknownFormat)
            }

            val type = json.optString("type")
            if (type == null || (type != "ok" && type != "error"))
                return ParseError(unknownFormat)

            if (type == "error") {
                val serverErrorReason = json.optString("error")
                return ParseError(serverErrorReason)
            }

            val array = json.optJSONArray("data") ?: return ParseError(unspecifiedData)

            for (index in 0 until (array.length())) {
                val row = array.getJSONObject(index) ?: return ParseError(corruptedRecord)
                val record = Record()

                record.name = row.optString("name", "unset")
                record.value = Math.abs(row.optDouble("value", 0.0))
                record.wallet = row.optString("wallet", "unset")
                record.tag = row.optString("tag", "unset")
                val id = row.optString("_id") ?: return ParseError(corruptedRecord)
                record.id = id
                val timestamp = row.optLong("timestamp")
                if (timestamp == 0L) return ParseError(corruptedRecord)
                record.timestamp = timestamp
                record.date = RecordDate.fromJSON(row.optJSONObject("date") ?: return ParseError(corruptedRecord))
                record.daily = json.optBoolean("daily", true)

                parsed.add(record)
            }

            return ParsedValue(parsed)
        }
    }
}