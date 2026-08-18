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

enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR
}

data class SyncState(
    val status: SyncStatus = SyncStatus.IDLE,
    val lastSyncTimestamp: Long = 0L,
    val message: String = ""
)

object CloudSyncManager {

    private val _syncStateFlow = MutableStateFlow(SyncState())
    val syncStateFlow: StateFlow<SyncState> = _syncStateFlow.asStateFlow()

    // Local cached cloud snapshot for offline mock/real fallback
    private var cloudSnapshotJson: String = ""

    suspend fun backupToCloud(
        transactions: List<TransactionEntity>,
        traceId: String = TraceManager.newTraceId()
    ): Result<Int> {
        _syncStateFlow.value = _syncStateFlow.value.copy(status = SyncStatus.SYNCING, message = "正在同步备份到云端...")

        return try {
            val count = TraceManager.trace(
                channel = ApmLogChannel.SYNC,
                tag = "CloudSync",
                operationName = "BackupToCloud",
                traceId = traceId
            ) {
                // Simulate network sync latency
                delay(800)
                cloudSnapshotJson = TransactionBackupManager.exportToJson(transactions)
                ApmLogger.sync("CloudSync", "Uploaded ${transactions.size} records to cloud snapshot", traceId)
                transactions.size
            }

            val now = System.currentTimeMillis()
            _syncStateFlow.value = SyncState(
                status = SyncStatus.SUCCESS,
                lastSyncTimestamp = now,
                message = "云端备份成功 (已同步 $count 条明细)"
            )
            Result.success(count)
        } catch (e: Exception) {
            _syncStateFlow.value = SyncState(
                status = SyncStatus.ERROR,
                message = "云端同步失败: ${e.message}"
            )
            Result.failure(e)
        }
    }

    suspend fun restoreFromCloud(
        traceId: String = TraceManager.newTraceId()
    ): Result<List<TransactionEntity>> {
        _syncStateFlow.value = _syncStateFlow.value.copy(status = SyncStatus.SYNCING, message = "正在从云端拉取备份...")

        return try {
            val list = TraceManager.trace(
                channel = ApmLogChannel.SYNC,
                tag = "CloudSync",
                operationName = "RestoreFromCloud",
                traceId = traceId
            ) {
                delay(800)
                if (cloudSnapshotJson.isBlank()) {
                    throw IllegalStateException("云端暂无可用备份，请先执行云端备份！")
                }
                val parsed = TransactionBackupManager.importFromJson(cloudSnapshotJson)
                ApmLogger.sync("CloudSync", "Downloaded ${parsed.size} records from cloud snapshot", traceId)
                parsed
            }

            val now = System.currentTimeMillis()
            _syncStateFlow.value = SyncState(
                status = SyncStatus.SUCCESS,
                lastSyncTimestamp = now,
                message = "云端还原成功 (已恢复 ${list.size} 条明细)"
            )
            Result.success(list)
        } catch (e: Exception) {
            _syncStateFlow.value = SyncState(
                status = SyncStatus.ERROR,
                message = "云端还原失败: ${e.message}"
            )
            Result.failure(e)
        }
    }
}
