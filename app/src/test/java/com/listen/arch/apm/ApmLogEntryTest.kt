package com.listen.arch.apm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ApmLogEntryTest {

    @Test
    fun `test ApmLogChannel enum values`() {
        val channels = ApmLogChannel.values()
        assertEquals(4, channels.size)
        assertEquals(ApmLogChannel.APP, channels[0])
        assertEquals(ApmLogChannel.DB, channels[1])
        assertEquals(ApmLogChannel.SYNC, channels[2])
        assertEquals(ApmLogChannel.CRASH, channels[3])
    }

    @Test
    fun `test ApmLogLevel enum values`() {
        val levels = ApmLogLevel.values()
        assertEquals(4, levels.size)
        assertEquals(ApmLogLevel.DEBUG, levels[0])
        assertEquals(ApmLogLevel.INFO, levels[1])
        assertEquals(ApmLogLevel.WARN, levels[2])
        assertEquals(ApmLogLevel.ERROR, levels[3])
    }

    @Test
    fun `test ApmLogEntry defaults`() {
        val entry = ApmLogEntry(message = "Test message")
        
        assertNotNull(entry.id)
        assertNotNull(entry.timestamp)
        assertEquals(ApmLogLevel.INFO, entry.level)
        assertEquals(ApmLogChannel.APP, entry.channel)
        assertEquals("APM", entry.tag)
        assertEquals("Test message", entry.message)
        assertNull(entry.traceId)
        assertNull(entry.stackTrace)
    }

    @Test
    fun `test ApmLogEntry with custom values`() {
        val entry = ApmLogEntry(
            id = "custom-id",
            timestamp = 12345L,
            level = ApmLogLevel.ERROR,
            channel = ApmLogChannel.DB,
            tag = "CUSTOM_TAG",
            message = "Custom error",
            traceId = "trace-123",
            stackTrace = "java.lang.Exception"
        )
        
        assertEquals("custom-id", entry.id)
        assertEquals(12345L, entry.timestamp)
        assertEquals(ApmLogLevel.ERROR, entry.level)
        assertEquals(ApmLogChannel.DB, entry.channel)
        assertEquals("CUSTOM_TAG", entry.tag)
        assertEquals("Custom error", entry.message)
        assertEquals("trace-123", entry.traceId)
        assertEquals("java.lang.Exception", entry.stackTrace)
    }

    @Test
    fun `test ApmLogEntry copy`() {
        val entry = ApmLogEntry(message = "Original").copy(
            message = "Copied",
            level = ApmLogLevel.WARN
        )
        
        assertEquals("Copied", entry.message)
        assertEquals(ApmLogLevel.WARN, entry.level)
        assertEquals(ApmLogChannel.APP, entry.channel)
    }
}
