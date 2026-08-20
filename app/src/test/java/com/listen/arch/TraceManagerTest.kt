package com.listen.arch

import com.listen.arch.apm.ApmLogChannel
import com.listen.arch.apm.ApmLogger
import com.listen.arch.apm.TraceManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TraceManagerTest {

    @Before
    fun setUp() {
        ApmLogger.clear()
    }

    @Test
    fun testNewTraceIdFormat() {
        val traceId1 = TraceManager.newTraceId()
        val traceId2 = TraceManager.newTraceId()

        assertNotNull(traceId1)
        assertNotNull(traceId2)
        assertTrue(traceId1.startsWith("trace-"))
        assertTrue(traceId2.startsWith("trace-"))
        assertTrue(traceId1 != traceId2)
    }

    @Test
    fun testTraceExecutionAndLogging() {
        val customTraceId = TraceManager.newTraceId()
        var executed = false

        val result = TraceManager.trace(
            channel = ApmLogChannel.DB,
            tag = "RoomDB",
            operationName = "TestOperation",
            traceId = customTraceId
        ) {
            executed = true
            42
        }

        assertTrue(executed)
        assertEquals(42, result)

        val logs = ApmLogger.logsFlow.value
        assertEquals(2, logs.size)

        val startLog = logs.find { it.message.contains("Start: TestOperation") }
        assertNotNull(startLog)
        assertEquals(customTraceId, startLog?.traceId)

        val successLog = logs.find { it.message.contains("Success: TestOperation") }
        assertNotNull(successLog)
        assertTrue(successLog!!.message.contains("ms"))
    }
}
