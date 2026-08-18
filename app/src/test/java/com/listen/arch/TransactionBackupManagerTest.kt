package com.listen.arch

import com.listen.arch.data.backup.TransactionBackupManager
import com.listen.arch.data.db.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionBackupManagerTest {

    @Test
    fun testJsonExportAndImport() {
        val sampleList = listOf(
            TransactionEntity(
                id = "tx-1",
                type = "EXPENSE",
                categoryId = "c_food",
                categoryName = "餐饮",
                categoryIcon = "c_food",
                categoryColorHex = "#EF4444",
                amount = 45.0,
                note = "午餐拉面",
                accountType = "WECHAT",
                timestamp = 1723900000000L
            ),
            TransactionEntity(
                id = "tx-2",
                type = "INCOME",
                categoryId = "c_salary",
                categoryName = "工资",
                categoryIcon = "c_salary",
                categoryColorHex = "#10B981",
                amount = 10000.0,
                note = "月薪",
                accountType = "BANK",
                timestamp = 1723900001000L
            )
        )

        val json = TransactionBackupManager.exportToJson(sampleList)
        assertTrue(json.contains("tx-1"))
        assertTrue(json.contains("餐饮"))
        assertTrue(json.contains("tx-2"))

        val imported = TransactionBackupManager.importFromJson(json)
        assertEquals(2, imported.size)
        assertEquals("tx-1", imported[0].id)
        assertEquals("EXPENSE", imported[0].type)
        assertEquals(45.0, imported[0].amount, 0.001)
        assertEquals("午餐拉面", imported[0].note)

        assertEquals("tx-2", imported[1].id)
        assertEquals("INCOME", imported[1].type)
        assertEquals(10000.0, imported[1].amount, 0.001)
    }

    @Test
    fun testCsvExport() {
        val sampleList = listOf(
            TransactionEntity(
                id = "tx-1",
                type = "EXPENSE",
                categoryId = "c_food",
                categoryName = "餐饮",
                categoryIcon = "c_food",
                categoryColorHex = "#EF4444",
                amount = 45.0,
                note = "午餐拉面",
                accountType = "WECHAT",
                timestamp = 1723900000000L
            )
        )

        val csv = TransactionBackupManager.exportToCsv(sampleList)
        assertTrue(csv.contains("ID,类型,分类,金额,账户,备注,时间"))
        assertTrue(csv.contains("tx-1,支出,餐饮,45.0,WECHAT,午餐拉面"))
    }
}
