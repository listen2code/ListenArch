package com.listen.arch.apm

enum class ApmLogChannel {
    APP,
    DB,
    SYNC,
    CRASH
}

enum class ApmLogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR
}

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
