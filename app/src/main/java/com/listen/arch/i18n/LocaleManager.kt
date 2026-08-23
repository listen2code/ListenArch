package com.listen.arch.i18n

import java.util.Locale

/**
 * Supported Languages in Listen ecosystem.
 */
enum class AppLanguage(val code: String, val displayName: String) {
    ZH("zh", "简体中文"),
    EN("en", "English"),
    JA("ja", "日本語");

    companion object {
        val CHINESE = ZH
        val ENGLISH = EN
        val JAPANESE = JA

        fun fromCode(code: String): AppLanguage =
            entries.find { it.code.equals(code, ignoreCase = true) } ?: ZH
    }
}

/**
 * Universal Locale resolver for Java/Android Locale conversions.
 */
object LocaleManager {

    fun getLocale(lang: AppLanguage): Locale = when (lang) {
        AppLanguage.ZH -> Locale.SIMPLIFIED_CHINESE
        AppLanguage.EN -> Locale.ENGLISH
        AppLanguage.JA -> Locale.JAPANESE
    }

    fun getLocale(langCode: String): Locale = getLocale(AppLanguage.fromCode(langCode))
}
