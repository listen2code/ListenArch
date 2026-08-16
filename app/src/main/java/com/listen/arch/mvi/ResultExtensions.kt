package com.listen.arch.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Wraps a Flow execution in Kotlin's native Result<T>.
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map { Result.success(it) }
        .catch { emit(Result.failure(it)) }
}

/**
 * Safely executes a suspend block and returns Kotlin's native Result<T>.
 */
inline fun <T> safeCall(block: () -> T): Result<T> {
    return runCatching(block)
}
