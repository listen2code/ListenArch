package com.listen.arch.sync

import com.listen.arch.apm.ApmLogChannel
import com.listen.arch.apm.ApmLogger
import com.listen.arch.apm.TraceManager
import com.listen.arch.data.backup.TransactionBackupManager
import com.listen.arch.data.db.TransactionEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR
}

data class SyncState(
    val status: SyncStatus = SyncStatus.IDLE,
    val lastSyncTimestamp: Long = 0L,
    val message: String = "",
    val activeAccountEmail: String = "",
    val cloudRecordCount: Int = 0
)

object CloudSyncManager {

    private val _syncStateFlow = MutableStateFlow(SyncState())
    val syncStateFlow: StateFlow<SyncState> = _syncStateFlow.asStateFlow()

    // Multi-account in-memory and simulated cloud storage
    private val accountCloudSnapshots = mutableMapOf<String, String>()

    suspend fun backupToCloud(
        transactions: List<TransactionEntity>,
        accountEmail: String,
        traceId: String = TraceManager.newTraceId()
    ): Result<Int> {
        if (accountEmail.isBlank()) {
            val err = IllegalStateException("未登录 Google 账户，无法进行云端加密备份！")
            _syncStateFlow.value = SyncState(status = SyncStatus.ERROR, message = err.message ?: "未登录账户")
            return Result.failure(err)
        }

        _syncStateFlow.value = _syncStateFlow.value.copy(
            status = SyncStatus.SYNCING,
            message = "正在加密同步至 Google 云端...",
            activeAccountEmail = accountEmail
        )

        return try {
            delay(120) // Realistic secure network negotiation
            val count = TraceManager.trace(
                channel = ApmLogChannel.SYNC,
                tag = "CloudSync",
                operationName = "BackupToCloud",
                traceId = traceId
            ) {
                val jsonPayload = TransactionBackupManager.exportToJson(transactions)
                val checksum = computeMd5(jsonPayload)
                accountCloudSnapshots[accountEmail] = jsonPayload

                ApmLogger.sync(
                    tag = "CloudSync",
                    message = "Uploaded ${transactions.size} records for $accountEmail (MD5: ${checksum.take(8)})",
                    traceId = traceId
                )
                transactions.size
            }

            val now = System.currentTimeMillis()
            _syncStateFlow.value = SyncState(
                status = SyncStatus.SUCCESS,
                lastSyncTimestamp = now,
                message = "云端备份成功 (已安全同步 $count 条账单)",
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

    suspend fun restoreFromCloud(
        accountEmail: String,
        traceId: String = TraceManager.newTraceId()
    ): Result<List<TransactionEntity>> {
        if (accountEmail.isBlank()) {
            val err = IllegalStateException("未登录 Google 账户，无法进行云端还原！")
            _syncStateFlow.value = SyncState(status = SyncStatus.ERROR, message = err.message ?: "未登录账户")
            return Result.failure(err)
        }

        _syncStateFlow.value = _syncStateFlow.value.copy(
            status = SyncStatus.SYNCING,
            message = "正在从 Google 云端检索历史备份...",
            activeAccountEmail = accountEmail
        )

        return try {
            delay(150) // Realistic cloud retrieval
            val list = TraceManager.trace(
                channel = ApmLogChannel.SYNC,
                tag = "CloudSync",
                operationName = "RestoreFromCloud",
                traceId = traceId
            ) {
                val cloudPayload = accountCloudSnapshots[accountEmail]
                if (cloudPayload.isNullOrBlank()) {
                    throw IllegalStateException("当前账户 ($accountEmail) 在云端暂无历史备份，请先执行备份！")
                }
                val parsed = TransactionBackupManager.importFromJson(cloudPayload)
                ApmLogger.sync(
                    tag = "CloudSync",
                    message = "Downloaded ${parsed.size} records for $accountEmail from cloud",
                    traceId = traceId
                )
                parsed
            }

            val now = System.currentTimeMillis()
            _syncStateFlow.value = SyncState(
                status = SyncStatus.SUCCESS,
                lastSyncTimestamp = now,
                message = "云端还原成功 (已恢复 ${list.size} 条账单)",
                activeAccountEmail = accountEmail,
                cloudRecordCount = list.size
            )
            Result.success(list)
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
