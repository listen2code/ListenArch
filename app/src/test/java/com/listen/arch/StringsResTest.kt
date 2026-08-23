package com.listen.arch

import com.listen.arch.i18n.StringsRes
import org.junit.Assert.assertEquals
import org.junit.Test

class StringsResTest {

    @Test
    fun testCommonStrings() {
        assertEquals("确定", StringsRes.get("common_ok", "zh"))
        assertEquals("OK", StringsRes.get("common_ok", "en"))
        assertEquals("OK", StringsRes.get("common_ok", "ja"))

        assertEquals("设置", StringsRes.get("common_settings", "zh"))
        assertEquals("Settings", StringsRes.get("common_settings", "en"))
        assertEquals("設定", StringsRes.get("common_settings", "ja"))
    }

    @Test
    fun testRegisterAppStrings() {
        StringsRes.registerAppStrings("zh", mapOf("custom_app_key" to "自定义业务文案"))
        StringsRes.registerAppStrings("en", mapOf("custom_app_key" to "Custom App Text"))

        assertEquals("自定义业务文案", StringsRes.get("custom_app_key", "zh"))
        assertEquals("Custom App Text", StringsRes.get("custom_app_key", "en"))
    }

    @Test
    fun testFallback() {
        assertEquals("fallback_unknown_key", StringsRes.get("fallback_unknown_key", "en"))
    }
}
