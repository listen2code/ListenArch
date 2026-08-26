# ListenArch

<p align="center">
  <strong>通用 Android 核心架构与基础设施库 (Universal Android Core Architecture & Infrastructure SDK)</strong>
</p>

---

## 📖 简介与定位

`ListenArch` 是 Listen 多 App 生态矩阵（记账、资产管理、习惯打卡、备忘录等）的**通用核心架构与技术底座**。
它遵循**完全业务解耦（Zero Business Coupling）**原则，仅提供通用底层技术设施与标准契约，不包含任何特定业务实体或表结构。

---

## 🌟 核心功能模块 (`com.listen.arch`)

- **`mvi/` (单向数据流架构基类)**：
  - `BaseViewModel<State, Intent, Effect>`：响应式 StateFlow 状态机与 SharedFlow 副作用单向数据流核心基类。
  - `CommonUiEffect`：通用一次性 UI 副作用密封接口（`ShowToast`、`ShowSnackbar`、`ShareText`、`NavigateTo`、`LaunchGoogleSignIn`、`OpenApmInspector`），开箱即用的跨模块标准 Effect 契约。
  - `ResultExtensions.kt`：Kotlin 原生 `Result<T>`、`safeCall {}` 异常收敛与流式转换扩展。
- **`data/pref/` (通用配置持久化)**：
  - `BaseDataStoreManager`：基于 Jetpack DataStore Preferences 的多 App 通用配置管理者（语言、深浅色主题、强调色、Google 账户认证状态、上次云同步时间戳、用户邮箱、显示名称与头像 URL），支持业务子类通过 `getPreference` / `setPreference` 自由扩展。
- **`sync/` (通用云同步引擎)**：
  - `CloudSyncManager`：面向任意序列化数据 Payload（JSON 字符串）的多账户隔离云端加密备份与恢复引擎，内置 MD5 校验和与 `SyncState` 响应式状态流。
- **`apm/` (应用性能监控与可观测性)**：
  - `ApmLogEntry`：APM 日志数据结构（`ApmLogEntry`、`ApmLogLevel`、`ApmLogChannel`），定义日志条目的统一格式与频道枚举。
  - `ApmLogger`：500 条高频环形缓冲区内存日志（APP、DB、SYNC、CRASH 四大核心频道）。
  - `TraceManager`：基于分布式 `traceId` 的毫秒级执行链路耗时打点。
  - `CrashHandler`：未捕获异常全局兜底与静默排查。
- **`i18n/` (通用国际化调度引擎)**：
  - `StringsRes`：内置核心系统通用词（确认、取消、保存、删除、主题设置等），并提供 `registerAppStrings(lang, map)` 开放接口供各业务宿主 App 动态注册自定义多语言字典。
  - `CommonStringsDictionary`：内置中文 (`commonZhMap`)、英文 (`commonEnMap`)、日文 (`commonJaMap`) 三语通用 UI 词典，为 `StringsRes` 提供开箱即用的基础词条。
  - `LocaleManager` & `AppLanguage`：标准 ISO 语言代码与 Android Locale 转换器。

---

## 📦 Gradle 引入说明 (Composite Build)

在宿主 App 项目的 `settings.gradle.kts` 中包含模块：

```kotlin
// settings.gradle.kts
includeBuild("../ListenArch")
```

在宿主 App 模块的 `build.gradle.kts` 中添加依赖：

```kotlin
dependencies {
    implementation("com.listen:listen-arch")
}
```

---

## 📄 开源许可

基于 [MIT License](LICENSE) 开源。
