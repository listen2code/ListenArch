package com.listen.arch.i18n

import java.util.concurrent.ConcurrentHashMap

/**
 * Universal Centralized Internationalization String Resource Engine.
 * Provides system-level common localized strings and allows host apps to dynamically register app-specific string dictionaries.
 */
object StringsRes {

    private val appRegisteredStrings = ConcurrentHashMap<String, ConcurrentHashMap<String, String>>()

    /**
     * Registers an app-specific string dictionary for a specified language.
     *
     * @param lang The two-letter ISO language code ("zh", "en", "ja")
     * @param strings Key-value map of localized strings
     */
    fun registerAppStrings(lang: String, strings: Map<String, String>) {
        val langKey = lang.lowercase()
        val currentMap = appRegisteredStrings.getOrPut(langKey) { ConcurrentHashMap() }
        currentMap.putAll(strings)
    }

    /**
     * Resolves a localized string given its translation key and active language code.
     * Prioritizes app-registered strings, then falls back to common system strings, and finally the raw key.
     *
     * @param key The translation identifier key
     * @param lang The two-letter ISO language code ("zh", "en", "ja")
     * @return The localized human-readable text
     */
    fun get(key: String, lang: String): String {
        val langKey = lang.lowercase()

        // 1. Check app-registered dictionary for target language
        appRegisteredStrings[langKey]?.get(key)?.let { return it }

        // 2. Check system common dictionary for target language
        val systemMap = when (langKey) {
            "en" -> CommonStringsDictionary.commonEnMap
            "ja" -> CommonStringsDictionary.commonJaMap
            else -> CommonStringsDictionary.commonZhMap
        }
        systemMap[key]?.let { return it }

        // 3. Fallback to Chinese app-registered or system dictionary
        appRegisteredStrings["zh"]?.get(key)?.let { return it }
        CommonStringsDictionary.commonZhMap[key]?.let { return it }

        // 4. Return raw key if completely unmapped
        return key
    }
}

/**
 * Idiomatic Kotlin String extension function for localized translation lookup.
 * Example: AppStrings.app_version_label.tr(lang)
 *
 * @param lang ISO Language code ("zh", "en", "ja")
 * @return Localized string text
 */
fun String.tr(lang: String = "zh"): String = StringsRes.get(this, lang)
