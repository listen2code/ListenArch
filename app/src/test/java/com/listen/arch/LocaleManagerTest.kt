package com.listen.arch

import com.listen.arch.i18n.AppLanguage
import com.listen.arch.i18n.LocaleManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class LocaleManagerTest {

    @Test
    fun testAppLanguageEnum() {
        val languages = AppLanguage.entries
        assertEquals(3, languages.size)
        assertTrue(languages.contains(AppLanguage.CHINESE))
        assertTrue(languages.contains(AppLanguage.ENGLISH))
        assertTrue(languages.contains(AppLanguage.JAPANESE))

        assertEquals("zh", AppLanguage.CHINESE.code)
        assertEquals("en", AppLanguage.ENGLISH.code)
        assertEquals("ja", AppLanguage.JAPANESE.code)

        assertEquals(AppLanguage.CHINESE, AppLanguage.fromCode("zh"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.JAPANESE, AppLanguage.fromCode("ja"))
        assertEquals(AppLanguage.CHINESE, AppLanguage.fromCode("unknown_fallback"))
    }

    @Test
    fun testLocaleManagerGetLocale() {
        val zhLocale = LocaleManager.getLocale("zh")
        assertEquals(Locale.SIMPLIFIED_CHINESE, zhLocale)

        val enLocale = LocaleManager.getLocale("en")
        assertEquals(Locale.ENGLISH, enLocale)

        val jaLocale = LocaleManager.getLocale("ja")
        assertEquals(Locale.JAPANESE, jaLocale)

        val fallbackLocale = LocaleManager.getLocale("fr")
        assertEquals(Locale.SIMPLIFIED_CHINESE, fallbackLocale)
    }
}
