package com.listen.arch

import com.listen.arch.apm.ApmLogChannel
import com.listen.arch.apm.ApmLogLevel
import com.listen.arch.apm.ApmLogger
import com.listen.arch.apm.CrashHandler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

class CrashHandlerTest {

    @Before
    fun setUp() {
        ApmLogger.clear()
    }

    @Test
    fun testHandleCrashFormatsAndLogs() {
        val tempDir = File(System.getProperty("java.io.tmpdir"), "test_crash_logs_" + System.currentTimeMillis())
        tempDir.mkdirs()

        val testException = NullPointerException("Crash Handler Unit Test Crash")
        val stackTrace = CrashHandler.handleCrash(Thread.currentThread(), testException, tempDir)

        assertTrue(stackTrace.contains("NullPointerException"))
        assertTrue(stackTrace.contains("Crash Handler Unit Test Crash"))

        val logs = ApmLogger.logsFlow.value
        assertEquals(1, logs.size)
        assertEquals(ApmLogChannel.CRASH, logs[0].channel)
        assertEquals(ApmLogLevel.ERROR, logs[0].level)
        assertTrue(logs[0].message.contains("Crash in thread"))

        val crashFile = File(tempDir, "crash_logs.txt")
        assertTrue(crashFile.exists())
        val content = crashFile.readText()
        assertTrue(content.contains("CRASH"))
        assertTrue(content.contains("Crash Handler Unit Test Crash"))

        // Cleanup
        crashFile.delete()
        tempDir.delete()
    }
}
