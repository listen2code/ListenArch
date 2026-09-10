package com.listen.arch.apm

import java.util.UUID

object TraceManager {
    /**
     * 生成短 UUID（前 8 位）作为 TraceID。
     * 技术决策：在满足足够随机性的前提下，保持简短以提升日志可读性，
     * 避免全长 UUID 在控制台中占用过多宽度。
     */
    fun newTraceId(): String {
        return "trace-" + UUID.randomUUID().toString().take(8)
    }

    /**
     * 核心追踪方法。
     * WHY inline：日志追踪在系统热点路径中被极其频繁地调用。
     * 使用 inline 可以消除 Lambda 表达式带来的对象分配开销，降低 GC 压力。
     *
     * block 参数主动抛出 traceId，调用方可以将其向下传递，形成相关联的追踪链。
     */
    inline fun <T> trace(
        channel: ApmLogChannel = ApmLogChannel.APP,
        tag: String = "Trace",
        operationName: String,
        traceId: String = newTraceId(),
        block: (traceId: String) -> T
    ): T {
        // 采用 currentTimeMillis 而非 nanoTime，因为产生的耗时及时间戳主要是为了给人阅读的绝对时间，而非纳秒级性能剖析
        val start = System.currentTimeMillis()
        ApmLogger.i(channel = channel, tag = tag, message = "[$traceId] Start: $operationName", traceId = traceId)
        return try {
            val result = block(traceId)
            // 自动记录执行成功的时长
            val duration = System.currentTimeMillis() - start
            ApmLogger.i(channel = channel, tag = tag, message = "[$traceId] Success: $operationName (${duration}ms)", traceId = traceId)
            result
        } catch (e: Throwable) {
            // 异常捕获模式：不仅记录耗时，同时将错误信息及异常堆栈一并保存，并重新抛出异常
            val duration = System.currentTimeMillis() - start
            ApmLogger.e(channel = channel, tag = tag, message = "[$traceId] Failed: $operationName (${duration}ms) - ${e.message}", traceId = traceId, throwable = e)
            throw e
        }
    }
}
