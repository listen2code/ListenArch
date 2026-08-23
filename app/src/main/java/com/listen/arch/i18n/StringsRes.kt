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
            "en" -> commonEnMap
            "ja" -> commonJaMap
            else -> commonZhMap
        }
        systemMap[key]?.let { return it }

        // 3. Fallback to Chinese app-registered or system dictionary
        appRegisteredStrings["zh"]?.get(key)?.let { return it }
        commonZhMap[key]?.let { return it }

        // 4. Return raw key if completely unmapped
        return key
    }

    // Common system-level strings (Reusable across all Listen ecosystem apps)
    private val commonZhMap = mapOf(
        "common_ok" to "确定",
        "common_cancel" to "取消",
        "common_save" to "保存",
        "common_delete" to "删除",
        "common_confirm" to "确认",
        "common_edit" to "编辑",
        "common_done" to "完成",
        "common_add" to "新增",
        "common_search" to "搜索...",
        "common_retry" to "重试",
        "common_back" to "返回",
        "common_close" to "关闭",
        "common_clear" to "清空",
        "common_loading" to "加载中...",
        "common_no_data" to "暂无数据",
        "common_error" to "操作失败",
        "common_success" to "操作成功",
        "common_settings" to "设置",
        "common_language" to "语言设置",
        "common_appearance" to "外观主题",
        "common_theme" to "主题模式",
        "theme_light" to "浅色",
        "theme_dark" to "深色",
        "theme_system" to "系统",
        "common_accent_color" to "强调色",
        "common_cloud_sync" to "云端同步与备份",
        "cloud_status_idle" to "就绪",
        "cloud_status_syncing" to "同步中...",
        "cloud_status_success" to "同步成功",
        "cloud_status_error" to "同步失败",
        "cloud_last_sync" to "上次同步：",
        "cloud_backup_btn" to "备份至云端",
        "cloud_restore_btn" to "从云端恢复",
        "google_account" to "Google 账号",
        "google_login_required" to "未登录 Google 账户（登录后方可进行云端同步与备份）",
        "google_login_btn" to "登录 Google 账号",
        "google_logout_btn" to "退出登录",
        "google_logged_in" to "已登录 Google 账号：",
        "google_link_title" to "绑定 Google 账号",
        "google_link_desc" to "登录后可开启全量数据云端加密备份与跨设备实时同步",
        "google_manual_email" to "手动输入 Google 邮箱",
        "google_email_placeholder" to "输入您的 Google 邮箱",
        "about_app" to "关于应用",
        "check_update" to "检查更新",
        "check_update_desc" to "前往 Google Play 检查并升级到最新版本",
        "app_version_label" to "当前版本",
        "app_architecture_label" to "架构",
        "app_core_sdk_label" to "核心 SDK",
        "app_features_label" to "特性",
        "apm_inspector" to "🔍 APM 日志面板",
        "export_json" to "导出 JSON",
        "export_csv" to "导出 CSV",
        "import_json" to "导入 JSON",
        "btn_done" to "完成",
        "btn_save" to "保存",
        "btn_cancel" to "取消",
        "btn_delete" to "删除"
    )

    private val commonEnMap = mapOf(
        "common_ok" to "OK",
        "common_cancel" to "Cancel",
        "common_save" to "Save",
        "common_delete" to "Delete",
        "common_confirm" to "Confirm",
        "common_edit" to "Edit",
        "common_done" to "Done",
        "common_add" to "Add",
        "common_search" to "Search...",
        "common_retry" to "Retry",
        "common_back" to "Back",
        "common_close" to "Close",
        "common_clear" to "Clear",
        "common_loading" to "Loading...",
        "common_no_data" to "No data available",
        "common_error" to "Operation Failed",
        "common_success" to "Operation Successful",
        "common_settings" to "Settings",
        "common_language" to "Language Settings",
        "common_appearance" to "Theme Appearance",
        "common_theme" to "Theme Mode",
        "theme_light" to "Light",
        "theme_dark" to "Dark",
        "theme_system" to "System",
        "common_accent_color" to "Accent Color",
        "common_cloud_sync" to "Cloud Sync & Backup",
        "cloud_status_idle" to "Ready",
        "cloud_status_syncing" to "Syncing...",
        "cloud_status_success" to "Success",
        "cloud_status_error" to "Failed",
        "cloud_last_sync" to "Last Synced: ",
        "cloud_backup_btn" to "Backup to Cloud",
        "cloud_restore_btn" to "Restore from Cloud",
        "google_account" to "Google Account",
        "google_login_required" to "Not signed in (Sign in with Google to enable cloud sync)",
        "google_login_btn" to "Sign in with Google",
        "google_logout_btn" to "Sign Out",
        "google_logged_in" to "Signed in as: ",
        "google_link_title" to "Link Google Account",
        "google_link_desc" to "Sign in to enable cloud backup & multi-device sync",
        "google_manual_email" to "Enter Google Email Manually",
        "google_email_placeholder" to "Enter your Google email address",
        "about_app" to "About App",
        "check_update" to "Check for Updates",
        "check_update_desc" to "Open Google Play to check for the latest version",
        "app_version_label" to "Current Version",
        "app_architecture_label" to "Architecture",
        "app_core_sdk_label" to "Core SDKs",
        "app_features_label" to "Features",
        "apm_inspector" to "🔍 APM Log Inspector",
        "export_json" to "Export JSON",
        "export_csv" to "Export CSV",
        "import_json" to "Import JSON",
        "btn_done" to "Done",
        "btn_save" to "Save",
        "btn_cancel" to "Cancel",
        "btn_delete" to "Delete"
    )

    private val commonJaMap = mapOf(
        "common_ok" to "OK",
        "common_cancel" to "キャンセル",
        "common_save" to "保存",
        "common_delete" to "削除",
        "common_confirm" to "確認",
        "common_edit" to "編集",
        "common_done" to "完了",
        "common_add" to "追加",
        "common_search" to "検索...",
        "common_retry" to "再試行",
        "common_back" to "戻る",
        "common_close" to "閉じる",
        "common_clear" to "消去",
        "common_loading" to "読み込み中...",
        "common_no_data" to "データがありません",
        "common_error" to "処理に失敗しました",
        "common_success" to "処理に成功しました",
        "common_settings" to "設定",
        "common_language" to "言語設定",
        "common_appearance" to "外観テーマ",
        "common_theme" to "テーマモード",
        "theme_light" to "ライト",
        "theme_dark" to "ダーク",
        "theme_system" to "システム",
        "common_accent_color" to "アクセントカラー",
        "common_cloud_sync" to "クラウド同期・バックアップ",
        "cloud_status_idle" to "準備完了",
        "cloud_status_syncing" to "同期中...",
        "cloud_status_success" to "同期成功",
        "cloud_status_error" to "同期失敗",
        "cloud_last_sync" to "前回の同期: ",
        "cloud_backup_btn" to "クラウドへバックアップ",
        "cloud_restore_btn" to "クラウドから復元",
        "google_account" to "Google アカウント",
        "google_login_required" to "未ログイン（ログイン後にクラウド同期・バックアップが有効になります）",
        "google_login_btn" to "Google でログイン",
        "google_logout_btn" to "ログアウト",
        "google_logged_in" to "ログイン済み: ",
        "google_link_title" to "Google アカウントを連携",
        "google_link_desc" to "連携すると暗号化クラウドバックアップと端末間同期が有効になります",
        "google_manual_email" to "Google メールを手動入力",
        "google_email_placeholder" to "Google メールアドレスを入力",
        "about_app" to "アプリについて",
        "check_update" to "アップデートを確認",
        "check_update_desc" to "Google Playで最新バージョンを確認・更新",
        "app_version_label" to "現在のバージョン",
        "app_architecture_label" to "アーキテクチャ",
        "app_core_sdk_label" to "コアSDK",
        "app_features_label" to "機能",
        "apm_inspector" to "🔍 APM ログパネル",
        "export_json" to "JSON エクスポート",
        "export_csv" to "CSV エクスポート",
        "import_json" to "JSON インポート",
        "btn_done" to "完了",
        "btn_save" to "保存",
        "btn_cancel" to "キャンセル",
        "btn_delete" to "削除"
    )
}
