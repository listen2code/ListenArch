package com.listen.arch

import com.listen.arch.apm.ApmLogChannel
import com.listen.arch.apm.ApmLogLevel
import com.listen.arch.apm.ApmLogger
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ApmLoggerTest {

    @Before
    fun setUp() {
        ApmLogger.clear()
    }

    @Test
    fun testLogAdditionAndLevels() {
        ApmLogger.d("TEST_TAG", "Debug log message")
        ApmLogger.i("TEST_TAG", "Info log message")
        ApmLogger.w("TEST_TAG", "Warn log message")
        ApmLogger.e("TEST_TAG", "Error log message", RuntimeException("Boom"))
        ApmLogger.db("DB_TAG", "Database query log")
        ApmLogger.sync("SYNC_TAG", "Sync started")
        ApmLogger.crash("CRASH_TAG", "App crashed", NullPointerException("Null reference"))

        val logs = ApmLogger.logsFlow.value
        assertEquals(7, logs.size)

        assertEquals(ApmLogLevel.DEBUG, logs[0].level)
        assertEquals(ApmLogChannel.APP, logs[0].channel)

        assertEquals(ApmLogLevel.INFO, logs[1].level)
        assertEquals(ApmLogLevel.WARN, logs[2].level)
        assertEquals(ApmLogLevel.ERROR, logs[3].level)
        assertTrue(logs[3].stackTrace?.contains("Boom") == true)

        assertEquals(ApmLogChannel.DB, logs[4].channel)
        assertEquals(ApmLogChannel.SYNC, logs[5].channel)
        assertEquals(ApmLogChannel.CRASH, logs[6].channel)
    }

    @Test
    fun testChannelOverloads() {
        ApmLogger.d(ApmLogChannel.DB, "DB", "Debug db")
        ApmLogger.i(ApmLogChannel.SYNC, "Sync", "Info sync")
        ApmLogger.w(ApmLogChannel.APP, "App", "Warn app")
        ApmLogger.e(ApmLogChannel.APP, "App", "Error app", traceId = "t1", throwable = IllegalArgumentException("Bad arg"))

        val logs = ApmLogger.logsFlow.value
        assertEquals(4, logs.size)
        assertEquals(ApmLogChannel.DB, logs[0].channel)
        assertEquals(ApmLogChannel.SYNC, logs[1].channel)
        assertEquals(ApmLogChannel.APP, logs[2].channel)
        assertEquals(ApmLogLevel.ERROR, logs[3].level)
    }

    @Test
    fun testRingBufferLimit() {
        for (i in 1..600) {
            ApmLogger.i("TAG", "Message $i")
        }
        val logs = ApmLogger.logsFlow.value
        assertEquals(500, logs.size)
        // Last message should be message 600
        assertEquals("Message 600", logs.last().message)
        assertEquals("Message 101", logs.first().message)
    }

    @Test
    fun testClearAndExportText() {
        ApmLogger.i("TAG_1", "Exportable log 1", traceId = "trace-101")
        ApmLogger.e("TAG_2", "Exportable log 2", traceId = "trace-102")

        val text = ApmLogger.exportPlainText()
        assertTrue(text.contains("Exportable log 1"))
        assertTrue(text.contains("Exportable log 2"))
        assertTrue(text.contains("trace-101"))
        assertTrue(text.contains("trace-102"))

        ApmLogger.clear()
        val clearedLogs = ApmLogger.logsFlow.value
        assertTrue(clearedLogs.isEmpty())
        assertFalse(ApmLogger.exportPlainText().contains("Exportable log 1"))
    }
}
