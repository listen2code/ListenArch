package com.listen.arch.apm

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CrashHandler {
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true

        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val stackTrace = sw.toString()

                ApmLogger.crash("UncaughtException", "Crash in thread ${thread.name}: ${throwable.message}", throwable)

                // Persist crash to file
                val file = File(context.filesDir, "crash_logs.txt")
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val record = "--- CRASH [${sdf.format(Date())}] Thread: ${thread.name} ---\n$stackTrace\n\n"
                file.appendText(record)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                defaultHandler?.uncaughtException(thread, throwable)
            }
        }
    }
}
