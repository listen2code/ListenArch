package com.listen.arch

import com.listen.arch.data.pref.BaseDataStoreManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class BaseDataStoreKeysTest {

    @Test
    fun testCompanionKeys() {
        assertEquals("app_language", BaseDataStoreManager.KEY_LANGUAGE.name)
        assertEquals("theme_mode", BaseDataStoreManager.KEY_THEME_MODE.name)
        assertEquals("accent_color", BaseDataStoreManager.KEY_ACCENT_COLOR.name)
        assertEquals("is_logged_in", BaseDataStoreManager.KEY_IS_LOGGED_IN.name)
        assertEquals("user_email", BaseDataStoreManager.KEY_USER_EMAIL.name)
        assertEquals("last_sync_timestamp", BaseDataStoreManager.KEY_LAST_SYNC.name)

        assertNotNull(BaseDataStoreManager.KEY_LANGUAGE)
        assertNotNull(BaseDataStoreManager.KEY_THEME_MODE)
        assertNotNull(BaseDataStoreManager.KEY_ACCENT_COLOR)
        assertNotNull(BaseDataStoreManager.KEY_IS_LOGGED_IN)
    }
}
