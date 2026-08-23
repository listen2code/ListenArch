package com.listen.arch

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
        val payload = """[{"id":"item-1","name":"Test Payload"}]"""
        val email = "test@example.com"

        val backupRes = CloudSyncManager.backupToCloud(
            payload = payload,
            recordCount = 1,
            accountEmail = email,
            traceId = "trace-backup-test"
        )
        assertTrue(backupRes.isSuccess)
        assertEquals(1, backupRes.getOrNull())

        val syncState = CloudSyncManager.syncStateFlow.value
        assertEquals(SyncStatus.SUCCESS, syncState.status)
        assertTrue(syncState.lastSyncTimestamp > 0)
        assertEquals(email, syncState.activeAccountEmail)

        val restoreRes = CloudSyncManager.restoreFromCloud(
            accountEmail = email,
            traceId = "trace-restore-test"
        )
        assertTrue(restoreRes.isSuccess)
        val restoredPayload = restoreRes.getOrNull()
        assertNotNull(restoredPayload)
        assertEquals(payload, restoredPayload)
    }
}
