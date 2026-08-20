package com.listen.arch

import com.listen.arch.data.db.TransactionEntity
import com.listen.arch.sync.CloudSyncManager
import com.listen.arch.sync.SyncStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CloudSyncManagerTest {

    @Test
    fun testCloudBackupAndRestore() = runBlocking {
        val sampleList = listOf(
            TransactionEntity(
                id = "tx-cloud-1",
                type = "EXPENSE",
                categoryId = "c_food",
                categoryName = "餐饮",
                categoryIcon = "c_food",
                categoryColorHex = "#EF4444",
                amount = 88.0,
                note = "云端测试账单",
                accountType = "ALIPAY",
                timestamp = 1723900000000L
            )
        )

        val backupRes = CloudSyncManager.backupToCloud(sampleList, "trace-backup-test")
        assertTrue(backupRes.isSuccess)
        assertEquals(1, backupRes.getOrNull())

        val syncState = CloudSyncManager.syncStateFlow.value
        assertEquals(SyncStatus.SUCCESS, syncState.status)
        assertTrue(syncState.lastSyncTimestamp > 0)

        val restoreRes = CloudSyncManager.restoreFromCloud("trace-restore-test")
        assertTrue(restoreRes.isSuccess)
        val restoredList = restoreRes.getOrNull()
        assertNotNull(restoredList)
        assertTrue(restoredList!!.isNotEmpty())
    }
}
