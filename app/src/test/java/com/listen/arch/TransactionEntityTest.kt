package com.listen.arch

import com.listen.arch.data.db.TransactionEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionEntityTest {

    @Test
    fun testTransactionEntityDefaultsAndCopy() {
        val entity = TransactionEntity(
            type = "EXPENSE",
            categoryId = "c_food",
            categoryName = "餐饮",
            categoryIcon = "c_food",
            categoryColorHex = "#EF4444",
            amount = 35.5,
            note = "午餐",
            accountType = "WECHAT"
        )

        assertNotNull(entity.id)
        assertTrue(entity.id.isNotBlank())
        assertEquals("EXPENSE", entity.type)
        assertEquals(35.5, entity.amount, 0.001)
        assertEquals("WECHAT", entity.accountType)

        val updated = entity.copy(amount = 40.0, note = "午餐加蛋")
        assertEquals(entity.id, updated.id)
        assertEquals(40.0, updated.amount, 0.001)
        assertEquals("午餐加蛋", updated.note)
    }
}
