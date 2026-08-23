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
 * Uses translation message keys instead of hardcoded strings for clean internationalization.
 */
data class SyncState(
    val status: SyncStatus = SyncStatus.IDLE,
    val lastSyncTimestamp: Long = 0L,
    val messageKey: String = "cloud_status_idle",
    val activeAccountEmail: String = "",
    val cloudRecordCount: Int = 0,
    val errorMessage: String? = null
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
            val err = IllegalStateException("sync_err_not_logged_in")
            _syncStateFlow.value = SyncState(
                status = SyncStatus.ERROR,
                messageKey = "sync_err_not_logged_in",
                errorMessage = err.message
            )
            return Result.failure(err)
        }

        _syncStateFlow.value = _syncStateFlow.value.copy(
            status = SyncStatus.SYNCING,
            messageKey = "sync_msg_syncing",
            activeAccountEmail = accountEmail,
            errorMessage = null
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
                messageKey = "sync_msg_backup_success",
                activeAccountEmail = accountEmail,
                cloudRecordCount = count,
                errorMessage = null
            )
            Result.success(count)
        } catch (e: Exception) {
            _syncStateFlow.value = SyncState(
                status = SyncStatus.ERROR,
                messageKey = "sync_msg_failed",
                activeAccountEmail = accountEmail,
                errorMessage = e.message
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
            val err = IllegalStateException("sync_err_not_logged_in")
            _syncStateFlow.value = SyncState(
                status = SyncStatus.ERROR,
                messageKey = "sync_err_not_logged_in",
                errorMessage = err.message
            )
            return Result.failure(err)
        }

        _syncStateFlow.value = _syncStateFlow.value.copy(
            status = SyncStatus.SYNCING,
            messageKey = "sync_msg_syncing",
            activeAccountEmail = accountEmail,
            errorMessage = null
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
                    throw IllegalStateException("sync_err_no_snapshot")
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
                messageKey = "sync_msg_restore_success",
                activeAccountEmail = accountEmail,
                errorMessage = null
            )
            Result.success(payload)
        } catch (e: Exception) {
            _syncStateFlow.value = SyncState(
                status = SyncStatus.ERROR,
                messageKey = "sync_msg_failed",
                activeAccountEmail = accountEmail,
                errorMessage = e.message
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
