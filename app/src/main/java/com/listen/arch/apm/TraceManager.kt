package com.listen.arch.apm

import java.util.UUID

object TraceManager {
    fun newTraceId(): String {
        return "trace-" + UUID.randomUUID().toString().take(8)
    }

    inline fun <T> trace(
        channel: ApmLogChannel = ApmLogChannel.APP,
        tag: String = "Trace",
        operationName: String,
        traceId: String = newTraceId(),
        block: (traceId: String) -> T
    ): T {
        val start = System.currentTimeMillis()
        ApmLogger.i(channel = channel, tag = tag, message = "[$traceId] Start: $operationName", traceId = traceId)
        return try {
            val result = block(traceId)
            val duration = System.currentTimeMillis() - start
            ApmLogger.i(channel = channel, tag = tag, message = "[$traceId] Success: $operationName (${duration}ms)", traceId = traceId)
            result
        } catch (e: Throwable) {
            val duration = System.currentTimeMillis() - start
            ApmLogger.e(channel = channel, tag = tag, message = "[$traceId] Failed: $operationName (${duration}ms) - ${e.message}", traceId = traceId, throwable = e)
            throw e
        }
    }
}
