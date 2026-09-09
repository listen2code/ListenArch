package com.listen.arch.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * 这种设计模式采用了将异常包装在 Kotlin 原生的 Result<T> 中。
 * 它能够以函数式的方式优雅地处理错误，代替传统的 try-catch 嵌套结构。
 */

/**
 * 捕获 Flow 收集过程中抛出的异常，将其转化为 Result.failure，正常值则转化为 Result.success。
 * 从而使下游能够持续安全地处理数据流而不会因为异常而崩溃。
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map { Result.success(it) }
        .catch { emit(Result.failure(it)) }
}

/**
 * 安全地执行挂起代码块。捕获域内异常并封装成 Result，方便做安全的回调或错误分支判断。
 */
inline fun <T> safeCall(block: () -> T): Result<T> {
    return runCatching(block)
}
