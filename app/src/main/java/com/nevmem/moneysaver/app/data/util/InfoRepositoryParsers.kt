package com.nevmem.moneysaver.app.data.util

import com.nevmem.moneysaver.common.Vars
import com.nevmem.moneysaver.app.data.Info
import com.nevmem.moneysaver.app.data.MonthDescription
import com.nevmem.moneysaver.app.data.util.ParseUtils.Companion.optDouble
import com.nevmem.moneysaver.app.data.util.ParseUtils.Companion.optUInt
import org.json.JSONObject
import java.lang.NumberFormatException

abstract class InfoRepositoryParsers {
    companion object {
        data class InfoMonthDescriptionsPair(var info: Info, var monthDescription: ArrayList<MonthDescription>)

        private fun createDateTimestamp(date: String): Int? {
            val tmp = date.split("-")
            if (tmp.size != 3) return null
            var timestamp = 0
            for (value in tmp) {
                timestamp *= 10000
                try {
                    timestamp += value.toInt()
                } catch (e: NumberFormatException) {
                    return null
                }
            }
            return timestamp
        }

        fun parseInfo(json: JSONObject): Info? {
            val info = Info()
            info.average = optDouble(json, "average") ?: return null
            info.sum30Days = optDouble(json, "sum30Days") ?: return null
            info.sum7Days = optDouble(json, "sum7Days") ?: return null
            info.average30Days = optDouble(json, "average30Days") ?: return null
            info.average7Days = optDouble(json,"average7Days") ?: return null
            info.totalSpend = optDouble(json,"totalSpend") ?: return null
            info.trackedDays = optUInt(json, "amountOfDays") ?: return null
            info.dailySum = optDouble(json, "dailySum") ?: return null
            info.dailyAverage = optDouble(json, "dailyAverage") ?: return null

            val sumDayJson = json.optJSONObject("daySum")
            info.sumDay.clear()
            val tmp = ArrayList<Pair<Int, Double>>()
            for (key in sumDayJson.keys()) {
                val current = optDouble(sumDayJson, key) ?: return null
                val dateTimestamp = createDateTimestamp(key) ?: return null
                tmp.add(Pair(dateTimestamp, current))
            }

            tmp.sortBy { value -> value.first }
            tmp.forEach { value -> info.sumDay.add(value.second) }

            return info
        }

        private fun parseOneMonthDescription(monthId: String, json: JSONObject): MonthDescription? {
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

        private fun parseMonthDescriptions(json: JSONObject): ArrayList<MonthDescription>? {
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