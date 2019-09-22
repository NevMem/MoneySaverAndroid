package com.nevmem.moneysaver

import com.nevmem.moneysaver.common.data.RecordDate
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

/**
 * Host tests
 */

class DateUnitTest {
    @Test
    fun checkDateFromJsonParsing() {
        val expected = RecordDate(2017, 12, 21, 23, 53)
        val json = JSONObject()
        json.put("year", expected.year)
        json.put("month", expected.month)
        json.put("day", expected.day)
        json.put("hour", expected.hour)
        json.put("minute", expected.minute)
        val found = RecordDate.fromJSON(json)
        Assert.assertEquals(expected, found)
    }

    @Test
    fun checkDateFromJsonParsingEmptyJson() {
        val json = JSONObject()
        val found = RecordDate.fromJSON(json)
        Assert.assertEquals(RecordDate(), found)
    }

    @Test
    fun checkDateFromJsonParsingNotOnlyInts() {
        val json = JSONObject()
        json.put("year", "some year")
        json.put("month", "1,3")
        val found = RecordDate.fromJSON(json)
        Assert.assertEquals(RecordDate(), found)
    }
}
