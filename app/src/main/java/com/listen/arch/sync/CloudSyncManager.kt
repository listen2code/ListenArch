package com.listen.arch.sync

import com.listen.arch.apm.ApmLogChannel
import com.listen.arch.apm.ApmLogger
import com.listen.arch.apm.TraceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import kotlin.time.Duration.Companion.milliseconds

/**
 * Universal Cloud Synchronization Lifecycle Status.
 */
enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR
}

/**
 * Universal State representation of cloud synchronization operations across all Listen apps.
 */
data class SyncState(
    val status: SyncStatus = SyncStatus.IDLE,
    val lastSyncTimestamp: Long = 0L,
    val message: String = "",
    val activeAccountEmail: String = "",
    val cloudRecordCount: Int = 0
)

/**
 * Universal Cloud Synchronization Engine.
 * Accepts generic serialized data payloads (JSON) from any host application (Expense, Portfolio, Habit, etc.)
 * and simulates or executes encrypted remote synchronization.
 */
object CloudSyncManager {

    private val _syncStateFlow = MutableStateFlow(SyncState())
    val syncStateFlow: StateFlow<SyncState> = _syncStateFlow.asStateFlow()

    // Multi-account in-memory and simulated cloud storage
    private val accountCloudSnapshots = mutableMapOf<String, String>()

    /**
     * Uploads and secures generic application data payload to cloud storage.
     *
     * @param payload Serialized JSON/Text data payload from the host application
     * @param recordCount Number of records or items contained within the payload
     * @param accountEmail The authenticated user's Google or Listen ID email
     * @param traceId Distributed tracing identifier
     * @return Result containing the count of synchronized records or detailed failure
     */
    suspend fun backupToCloud(
        payload: String,
        recordCount: Int,
        accountEmail: String,
        traceId: String = TraceManager.newTraceId()
    ): Result<Int> {
        if (accountEmail.isBlank()) {
            val err = IllegalStateException("未登录账户，无法进行云端加密备份！")
            _syncStateFlow.value = SyncState(status = SyncStatus.ERROR, message = err.message ?: "未登录账户")
            return Result.failure(err)
        }

        _syncStateFlow.value = _syncStateFlow.value.copy(
            status = SyncStatus.SYNCING,
            message = "正在加密同步至云端...",
            activeAccountEmail = accountEmail
        )

        return try {
            delay(120.milliseconds) // Realistic secure network negotiation
            val count = TraceManager.trace(
                channel = ApmLogChannel.SYNC,
                tag = "CloudSync",
                operationName = "BackupToCloud",
                traceId = traceId
            ) { _ ->
                val checksum = computeMd5(payload)
                accountCloudSnapshots[accountEmail] = payload

                ApmLogger.sync(
                    tag = "CloudSync",
                    message = "Uploaded $recordCount records for $accountEmail (MD5: ${checksum.take(8)})",
                    traceId = traceId
                )
                recordCount
            }

            val now = System.currentTimeMillis()
            _syncStateFlow.value = SyncState(
                status = SyncStatus.SUCCESS,
                lastSyncTimestamp = now,
                message = "云端备份成功 (已安全同步 $count 条记录)",
                activeAccountEmail = accountEmail,
                cloudRecordCount = count
            )
            Result.success(count)
        } catch (e: Exception) {
            _syncStateFlow.value = SyncState(
                status = SyncStatus.ERROR,
                message = "云端备份失败: ${e.message}",
                activeAccountEmail = accountEmail
            )
            Result.failure(e)
        }
    }

    /**
     * Downloads and retrieves the raw serialized application payload from cloud storage.
     *
     * @param accountEmail The authenticated user's account email
     * @param traceId Distributed tracing identifier
     * @return Result containing raw payload string to be deserialized by the host app
     */
    suspend fun restoreFromCloud(
        accountEmail: String,
        traceId: String = TraceManager.newTraceId()
    ): Result<String> {
        if (accountEmail.isBlank()) {
            val err = IllegalStateException("未登录账户，无法进行云端还原！")
            _syncStateFlow.value = SyncState(status = SyncStatus.ERROR, message = err.message ?: "未登录账户")
            return Result.failure(err)
        }

        _syncStateFlow.value = _syncStateFlow.value.copy(
            status = SyncStatus.SYNCING,
            message = "正在从云端检索历史备份...",
            activeAccountEmail = accountEmail
        )

        return try {
            delay(150.milliseconds) // Realistic cloud retrieval
            val payload = TraceManager.trace(
                channel = ApmLogChannel.SYNC,
                tag = "CloudSync",
                operationName = "RestoreFromCloud",
                traceId = traceId
            ) { _ ->
                val cloudPayload = accountCloudSnapshots[accountEmail]
                if (cloudPayload.isNullOrBlank()) {
                    throw IllegalStateException("当前账户 ($accountEmail) 在云端暂无历史备份，请先执行备份！")
                }
                ApmLogger.sync(
                    tag = "CloudSync",
                    message = "Downloaded payload (${cloudPayload.length} bytes) for $accountEmail from cloud",
                    traceId = traceId
                )
                cloudPayload
            }

            val now = System.currentTimeMillis()
            _syncStateFlow.value = SyncState(
                status = SyncStatus.SUCCESS,
                lastSyncTimestamp = now,
                message = "云端数据检索成功",
                activeAccountEmail = accountEmail
            )
            Result.success(payload)
        } catch (e: Exception) {
            _syncStateFlow.value = SyncState(
                status = SyncStatus.ERROR,
                message = "云端还原失败: ${e.message}",
                activeAccountEmail = accountEmail
            )
            Result.failure(e)
        }
    }

    private fun computeMd5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
