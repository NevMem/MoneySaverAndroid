package com.nevmem.moneysaver.data.util

import com.nevmem.moneysaver.Vars
import com.nevmem.moneysaver.data.Info
import com.nevmem.moneysaver.data.MonthDescription
import com.nevmem.moneysaver.data.util.ParseUtils.Companion.optDouble
import com.nevmem.moneysaver.data.util.ParseUtils.Companion.optUInt
import org.json.JSONObject

abstract class InfoRepositoryParsers {
    companion object {
        data class InfoMonthDescriptionsPair(var info: Info, var monthDescription: ArrayList<MonthDescription>)

        fun parseInfo(json: JSONObject): Info? {
            val info = Info()
            info.average = optDouble(json, "average") ?: return null
            info.sum30Days = optDouble(json, "sum30Days") ?: return null
            info.sum7Days = optDouble(json, "sum7Days") ?: return null
            info.average30Days = optDouble(json, "average30Days") ?: return null
            info.average7Days = optDouble(json,"average7Days") ?: return null
            info.totalSpend = optDouble(json,"totalSpend") ?: return null
            info.trackedDays = optUInt(json, "amountOfDays") ?: return null

            val sumDayJson = json.optJSONObject("daySum")
            info.sumDay.clear()
            for (key in sumDayJson.keys()) {
                val current = optDouble(sumDayJson, key) ?: return null
                info.sumDay.add(current)
            }

            return info
        }

        fun parseOneMonthDescription(monthId: String, json: JSONObject): MonthDescription? {
            val monthDescription = MonthDescription()
            monthDescription.average = optDouble(json, "average") ?: return null
            monthDescription.averageDaily = optDouble(json, "averageDaily") ?: return null
            monthDescription.total = optDouble(json, "total") ?: return null
            monthDescription.totalDaily = optDouble(json, "totalDaily") ?: return null
            monthDescription.monthTimestamp = optUInt(json, "monthTimestamp") ?: return null
            monthDescription.monthId = monthId

            val byTag = json.optJSONObject("byTag") ?: return null
            for (tag in byTag.keys()) {
                val fullTagInfo = byTag.getJSONObject(tag) ?: return null
                monthDescription.byTagDaily[tag] = optDouble(fullTagInfo, "daily") ?: return null
                monthDescription.byTagTotal[tag] = optDouble(fullTagInfo, "total") ?: return null
            }

            return monthDescription
        }

        fun parseMonthDescriptions(json: JSONObject): ArrayList<MonthDescription>? {
            val result = ArrayList<MonthDescription>()
            for (key in json.keys()) {
                val monthJson = json.optJSONObject(key) ?: return null
                result.add(parseOneMonthDescription(key, monthJson) ?: return null)
            }
            return result
        }

        fun parseServerLoadedResponse(json: JSONObject): ParseResult {
            val type = json.optString("type") ?: return ParseError(Vars.unknownFormat)
            if (type != "error" && type != "ok") return ParseError(Vars.unknownFormat)
            if (type == "error") {
                val error = json.optString("error") ?: return ParseError(Vars.unknownFormat)
                return ParseError(error)
            }
            val infoJson = json.optJSONObject("info") ?: return ParseError(Vars.unknownFormat)
            val monthSum = infoJson.optJSONObject("monthSum") ?: return ParseError(Vars.unknownFormat)

            val info = parseInfo(infoJson) ?: return ParseError(Vars.unknownFormat)
            val monthDescriptions = parseMonthDescriptions(monthSum) ?: return ParseError(Vars.unknownFormat)

            return ParsedValue(InfoMonthDescriptionsPair(info, monthDescriptions))
        }
    }
}