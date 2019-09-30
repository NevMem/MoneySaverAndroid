package com.nevmem.moneysaver

import com.nevmem.moneysaver.app.data.util.HistoryRepositoryParsers
import com.nevmem.moneysaver.app.data.util.ParseError
import com.nevmem.moneysaver.app.data.util.ParsedValue
import com.nevmem.moneysaver.common.Vars
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class HistoryRepositoryTests {
    @Test
    fun historyRepositoryParserBasicTest() {
        val json = JSONObject()
        val parsed = HistoryRepositoryParsers.parseServerLoadedResponse(json)
        if (parsed is ParseError) {
            Assert.assertEquals(Vars.unknownFormat, parsed.reason)
        } else {
            Assert.fail("Should return an error")
        }
    }

    @Test
    fun historyRepositoryParserServerErrorTest() {
        val error = "Small server problems"
        val json = JSONObject("{type: 'error', error: '$error'}")
        val parsed = HistoryRepositoryParsers.parseServerLoadedResponse(json)
        if (parsed is ParseError) {
            Assert.assertEquals(error, parsed.reason)
        } else {
            Assert.fail()
        }
    }

    @Test
    fun historyRepositoryParserNoDataTest() {
        val json = JSONObject("{type: 'ok'}")
        val parsed = HistoryRepositoryParsers.parseServerLoadedResponse(json)
        if (parsed is ParseError) {
            Assert.assertEquals(Vars.unspecifiedData, parsed.reason)
        } else {
            Assert.fail()
        }
    }

    @Test
    fun historyRepositoryParserEmptyDataTest() {
        val json = JSONObject("{type: 'ok', data: []}")
        val parsed = HistoryRepositoryParsers.parseServerLoadedResponse(json)
        if (parsed is ParseError) {
            Assert.fail("It's not an error")
        } else if (parsed is ParsedValue<*>) {
            if (parsed.parsed is ArrayList<*>) {
                val buffer = parsed.parsed as ArrayList<*>
                Assert.assertEquals(0, buffer.size)
            } else {
                Assert.fail("Parsed value is not an ArrayList")
            }
        }
    }

    @Test
    fun historyRepositoryParserRecordIsNotAnObjectTest() {
        val json = JSONObject("{type: 'ok', data: [[],{}]}")
        val parsed = HistoryRepositoryParsers.parseServerLoadedResponse(json)
        if (parsed is ParseError) {
            Assert.assertEquals(Vars.corruptedRecord, parsed.reason)
        } else if (parsed is ParsedValue<*>) {
            Assert.fail()
        }
    }
}