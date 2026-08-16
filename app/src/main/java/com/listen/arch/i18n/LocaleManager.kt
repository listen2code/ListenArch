package com.listen.arch.i18n

import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String) {
    ZH("zh", "简体中文"),
    EN("en", "English"),
    JA("ja", "日本語");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code == code } ?: ZH
    }
}

object LocaleManager {
    fun getLocale(lang: AppLanguage): Locale = when (lang) {
        AppLanguage.ZH -> Locale.SIMPLIFIED_CHINESE
        AppLanguage.EN -> Locale.ENGLISH
        AppLanguage.JA -> Locale.JAPANESE
    }
}
