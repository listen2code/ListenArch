package com.listen.arch.apm

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

object ApmLogger {
    private const val MAX_LOG_SIZE = 500
    // 使用 CopyOnWriteArrayList 是因为日志系统存在多线程并发写入（如网络、数据库线程），
    // 且 UI 端可能会并发读取。该集合能保证线程安全且读取性能优异。
    private val buffer = CopyOnWriteArrayList<ApmLogEntry>()
    
    // 使用 StateFlow 是为了能够以响应式的方式向外实时暴露最新的日志列表（如内部的开发者调试面板）。
    private val _logsFlow = MutableStateFlow<List<ApmLogEntry>>(emptyList())
    val logsFlow: StateFlow<List<ApmLogEntry>> = _logsFlow.asStateFlow()

    fun log(
        level: ApmLogLevel,
        channel: ApmLogChannel,
        tag: String,
        message: String,
        traceId: String? = null,
        throwable: Throwable? = null
    ) {
        val stackTrace = throwable?.let {
            val sw = StringWriter()
            it.printStackTrace(PrintWriter(sw))
            sw.toString()
        }

        val entry = ApmLogEntry(
            level = level,
            channel = channel,
            tag = tag,
            message = message,
            traceId = traceId,
            stackTrace = stackTrace
        )

        // 内存环形缓冲区模式（Ring Buffer）：
        // 当日志量超过最大容量时，主动剔除最早（索引为 0）的数据，维持内存大小稳定，防止内存泄漏。
        if (buffer.size >= MAX_LOG_SIZE) {
            buffer.removeAt(0)
        }
        buffer.add(entry)
        _logsFlow.value = buffer.toList()
    }

    fun d(channel: ApmLogChannel = ApmLogChannel.APP, tag: String = "App", message: String, traceId: String? = null) {
        log(ApmLogLevel.DEBUG, channel, tag, message, traceId)
    }

    fun d(tag: String, message: String, traceId: String? = null) {
        log(ApmLogLevel.DEBUG, ApmLogChannel.APP, tag, message, traceId)
    }

    fun i(channel: ApmLogChannel = ApmLogChannel.APP, tag: String = "App", message: String, traceId: String? = null) {
        log(ApmLogLevel.INFO, channel, tag, message, traceId)
    }

    fun i(tag: String, message: String, traceId: String? = null) {
        log(ApmLogLevel.INFO, ApmLogChannel.APP, tag, message, traceId)
    }

    fun w(channel: ApmLogChannel = ApmLogChannel.APP, tag: String = "App", message: String, traceId: String? = null) {
        log(ApmLogLevel.WARN, channel, tag, message, traceId)
    }

    fun w(tag: String, message: String, traceId: String? = null) {
        log(ApmLogLevel.WARN, ApmLogChannel.APP, tag, message, traceId)
    }

    fun e(channel: ApmLogChannel = ApmLogChannel.APP, tag: String = "App", message: String, traceId: String? = null, throwable: Throwable? = null) {
        log(ApmLogLevel.ERROR, channel, tag, message, traceId, throwable)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null, traceId: String? = null) {
        log(ApmLogLevel.ERROR, ApmLogChannel.APP, tag, message, traceId, throwable)
    }

    fun db(tag: String = "RoomDB", message: String, traceId: String? = null) {
        log(ApmLogLevel.INFO, ApmLogChannel.DB, tag, message, traceId)
    }

    fun sync(tag: String = "Sync", message: String, traceId: String? = null) {
        log(ApmLogLevel.INFO, ApmLogChannel.SYNC, tag, message, traceId)
    }

    fun crash(tag: String = "Crash", message: String, throwable: Throwable? = null) {
        log(ApmLogLevel.ERROR, ApmLogChannel.CRASH, tag, message, null, throwable)
    }

    fun clear() {
        buffer.clear()
        _logsFlow.value = emptyList()
    }

    /**
     * 导出为纯文本格式。
     * 设计上统一了时间戳、通道、级别、标签及 TraceId 等核心要素的排版格式，便于后续存储到文件系统或作为分析附件上传。
     */
    fun exportPlainText(): String {
        val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
        return buffer.joinToString("\n") { entry ->
            val time = sdf.format(Date(entry.timestamp))
            val trace = if (entry.traceId != null) " [${entry.traceId}]" else ""
            val stack = if (entry.stackTrace != null) "\n${entry.stackTrace}" else ""
            "[$time][${entry.channel}][${entry.level}][${entry.tag}]$trace ${entry.message}$stack"
        }
    }
}
