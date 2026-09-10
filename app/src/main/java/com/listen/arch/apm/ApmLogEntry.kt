package com.listen.arch.apm

/**
 * 业务日志通道划分。
 * 通过将日志按通道隔离，可以在排查问题时快速过滤：
 * APP: 纯业务逻辑层面的日志
 * DB: 数据库操作、耗时及状态追踪
 * SYNC: 云端同步、网络请求相关日志
 * CRASH: 未捕获异常和致命错误的专属通道
 */
enum class ApmLogChannel {
    APP,
    DB,
    SYNC,
    CRASH
}

/**
 * 标准的四级日志严重程度模型
 */
enum class ApmLogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

/**
 * APM 日志实体类，用于记录单条运行状态。
 *
 * 设计考量：
 * @param id 默认使用 UUID 自动生成，主要为了在 Compose LazyColumn 中作为唯一的 key 提升差异比对性能
 * @param timestamp 默认取当前系统时间，代表日志产生的时间
 * @param traceId 可选参数，用于分布式链路追踪，串联上下游调用日志
 * @param stackTrace 可选参数，专门为 ERROR 或 CRASH 级别日志记录堆栈调用信息
 */
data class ApmLogEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val level: ApmLogLevel = ApmLogLevel.INFO,
    val channel: ApmLogChannel = ApmLogChannel.APP,
    val tag: String = "APM",
    val message: String,
    val traceId: String? = null,
    val stackTrace: String? = null
)
