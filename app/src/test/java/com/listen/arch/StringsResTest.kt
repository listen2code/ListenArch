package com.listen.arch

import com.listen.arch.i18n.StringsRes
import org.junit.Assert.assertEquals
import org.junit.Test

class StringsResTest {

    @Test
    fun testZhStrings() {
        assertEquals("流水", StringsRes.get("nav_transactions", "zh"))
        assertEquals("结余", StringsRes.get("balance_title", "zh"))
        assertEquals("总支出", StringsRes.get("total_expense", "zh"))
    }

    @Test
    fun testEnStrings() {
        assertEquals("Transactions", StringsRes.get("nav_transactions", "en"))
        assertEquals("Net Balance", StringsRes.get("balance_title", "en"))
        assertEquals("Total Expense", StringsRes.get("total_expense", "en"))
    }

    @Test
    fun testJaStrings() {
        assertEquals("明細", StringsRes.get("nav_transactions", "ja"))
        assertEquals("残高", StringsRes.get("balance_title", "ja"))
        assertEquals("支出合計", StringsRes.get("total_expense", "ja"))
    }

    @Test
    fun testFallback() {
        assertEquals("fallback_key", StringsRes.get("fallback_key", "en"))
    }
}
