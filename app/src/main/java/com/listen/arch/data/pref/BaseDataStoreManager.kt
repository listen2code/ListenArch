package com.listen.arch.data.pref

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.archDataStore by preferencesDataStore(name = "listen_app_common_settings")

/**
 * Universal DataStore Manager for common app settings across all Listen apps.
 */
open class BaseDataStoreManager(private val context: Context) {

    companion object {
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_ACCENT_COLOR = stringPreferencesKey("accent_color")
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_LAST_SYNC = longPreferencesKey("last_sync_timestamp")
    }

    val languageFlow: Flow<String> = context.archDataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: "zh"
    }

    val themeModeFlow: Flow<String> = context.archDataStore.data.map { prefs ->
        prefs[KEY_THEME_MODE] ?: "SYSTEM"
    }

    val accentColorFlow: Flow<String> = context.archDataStore.data.map { prefs ->
        prefs[KEY_ACCENT_COLOR] ?: "EMERALD"
    }

    val isLoggedInFlow: Flow<Boolean> = context.archDataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val userEmailFlow: Flow<String> = context.archDataStore.data.map { prefs ->
        prefs[KEY_USER_EMAIL] ?: ""
    }

    val lastSyncTimestampFlow: Flow<Long> = context.archDataStore.data.map { prefs ->
        prefs[KEY_LAST_SYNC] ?: 0L
    }

    suspend fun setLanguage(langCode: String) {
        context.archDataStore.edit { prefs -> prefs[KEY_LANGUAGE] = langCode }
    }

    suspend fun setThemeMode(mode: String) {
        context.archDataStore.edit { prefs -> prefs[KEY_THEME_MODE] = mode }
    }

    suspend fun setAccentColor(accent: String) {
        context.archDataStore.edit { prefs -> prefs[KEY_ACCENT_COLOR] = accent }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean, userEmail: String = "") {
        context.archDataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = isLoggedIn
            prefs[KEY_USER_EMAIL] = userEmail
        }
    }

    suspend fun setLastSyncTimestamp(timestamp: Long) {
        context.archDataStore.edit { prefs -> prefs[KEY_LAST_SYNC] = timestamp }
    }

    // Generic preferences accessors for app-specific extensions
    fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.archDataStore.data.map { prefs ->
            prefs[key] ?: defaultValue
        }
    }

    suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        context.archDataStore.edit { prefs ->
            prefs[key] = value
        }
    }
}
