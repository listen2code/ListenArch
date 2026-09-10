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

    /**
     * 幂等性保护 (idempotency guard)，防止被多次重复注册导致崩溃处理器嵌套。
     */
    fun init(context: Context) {
        if (isInitialized) return
        isInitialized = true

        // 责任链模式 (Chain of responsibility)：
        // 保存系统默认的异常处理器，在处理完自定义的日志持久化后，将异常转交回给它，
        // 从而保留系统原有的崩溃行为（例如向上传递或弹出崩溃提示）
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleCrash(thread, throwable, context.filesDir)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    fun handleCrash(thread: Thread, throwable: Throwable, targetDir: File?): String {
        // 外层 try-catch 至关重要：崩溃处理器自身的逻辑绝对不能再次抛出异常，
        // 否则会导致无限循环崩溃甚至直接闪退，导致无法留下任何诊断信息
        return try {
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            val stackTrace = sw.toString()

            // 双写策略 (Dual-write strategy)：
            // 1. 写入内存日志 (ApmLogger)，便于在当前存活会话中检索
            ApmLogger.crash("UncaughtException", "Crash in thread ${thread.name}: ${throwable.message}", throwable)

            if (targetDir != null) {
                // 2. 写入持久化文件，保证 App 重启后依然能够读取到崩溃记录
                val file = File(targetDir, "crash_logs.txt")
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val record = "--- CRASH [${sdf.format(Date())}] Thread: ${thread.name} ---\n$stackTrace\n\n"
                // 使用 appendText 模式进行追加，使得历史崩溃记录可以累积而不会被覆盖
                file.appendText(record)
            }
            stackTrace
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}
