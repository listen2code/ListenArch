package com.listen.arch.i18n

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CommonStringsDictionaryTest {

    @Test
    fun `test map sizes are equal`() {
        val zhSize = CommonStringsDictionary.commonZhMap.size
        val enSize = CommonStringsDictionary.commonEnMap.size
        val jaSize = CommonStringsDictionary.commonJaMap.size
        
        assertEquals(zhSize, enSize)
        assertEquals(enSize, jaSize)
    }

    @Test
    fun `test maps contain same keys`() {
        val zhKeys = CommonStringsDictionary.commonZhMap.keys
        val enKeys = CommonStringsDictionary.commonEnMap.keys
        val jaKeys = CommonStringsDictionary.commonJaMap.keys
        
        assertEquals(zhKeys, enKeys)
        assertEquals(enKeys, jaKeys)
    }
    
    @Test
    fun `test specific key lookups`() {
        assertEquals("确定", CommonStringsDictionary.commonZhMap["common_ok"])
        assertEquals("OK", CommonStringsDictionary.commonEnMap["common_ok"])
        assertEquals("OK", CommonStringsDictionary.commonJaMap["common_ok"])
        
        assertEquals("取消", CommonStringsDictionary.commonZhMap["common_cancel"])
        assertEquals("Cancel", CommonStringsDictionary.commonEnMap["common_cancel"])
        assertEquals("キャンセル", CommonStringsDictionary.commonJaMap["common_cancel"])
    }
    
    @Test
    fun `test no null values in maps`() {
        assertTrue(CommonStringsDictionary.commonZhMap.values.all { it.isNotEmpty() })
        assertTrue(CommonStringsDictionary.commonEnMap.values.all { it.isNotEmpty() })
        assertTrue(CommonStringsDictionary.commonJaMap.values.all { it.isNotEmpty() })
    }
}
