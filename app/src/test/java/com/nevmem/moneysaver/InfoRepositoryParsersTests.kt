package com.nevmem.moneysaver

import com.nevmem.moneysaver.app.data.util.InfoRepositoryParsers
import com.nevmem.moneysaver.app.data.util.ParsedValue
import com.nevmem.moneysaver.JsonLoaderHelper.Companion.loadJson
import com.nevmem.moneysaver.app.data.Info
import org.junit.Assert
import org.junit.Test

class InfoRepositoryParsersTests {
    @Test
    fun noErrorFullParserTest() {
        val json = loadJson("sampleApiInfoResponse.json")
        val parsed = InfoRepositoryParsers.parseServerLoadedResponse(json)
        Assert.assertTrue(parsed is ParsedValue<*>)
    }

    @Test
    fun parseInfoTest() {
        val json = loadJson("sampleInfo.json")
        val parsed = InfoRepositoryParsers.parseInfo(json)
        Assert.assertNotNull(parsed)
        val expectedInfo = Info()
        expectedInfo.totalSpend = 6765.0
        expectedInfo.average = 1353.0
        expectedInfo.average7Days = 1353.0
        expectedInfo.average30Days = 1353.0
        expectedInfo.sumDay = arrayListOf(500.0, 0.0, 0.0, 4687.0, 1578.0)
        expectedInfo.sum7Days = 6765.0
        expectedInfo.sum30Days = 6765.0
        expectedInfo.trackedDays = 5
        expectedInfo.dailySum = 6765.0
        expectedInfo.dailyAverage = 128.0
        Assert.assertEquals(expectedInfo, parsed)
    }
}
