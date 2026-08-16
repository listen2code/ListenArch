# ListenArch

<p align="center">
  <strong>通用 Android 核心架构与基础设施库 (Universal Android Core Architecture SDK)</strong>
</p>

---

## 📖 简介

`ListenArch` 是 Listen 系列原生 Android 应用的核心架构 SDK。提供业务无关、可复用的底层基础设施，包含 Room SQLite 数据库封装、BaseDataStoreManager 通用持久化、MVI / UDF 架构基类 (`handleIntent`, `updateState`, `emitEffect`)、Kotlin 原生 `Result<T>` 错误处理与 LocaleManager 多语言资源。

---

## 🌟 核心模块结构 (`com.listen.arch`)

- **`mvi/`**：
  - `BaseViewModel<State, Intent, Effect>`：响应式 StateFlow 与 SharedFlow 事件驱动 MVI 基类（包含 `handleIntent`, `updateState`, `emitEffect` 契约方法）。
  - `ResultExtensions.kt`：Kotlin 原生 `Result<T>` 与 `Flow.asResult()` / `safeCall {}` 错误收敛扩展。
- **`data/db/`**：
  - `TransactionEntity`：账单数据实体表。
  - `TransactionDao`：Flow 响应式 SQL 增删改查 DAO。
  - `AppDatabase`：Room SQLite 单例数据库。
- **`data/pref/`**：
  - `BaseDataStoreManager`：基于 DataStore Preferences 的多语言、主题、登录态与云端同步全通用基础配置项管理者。
- **`i18n/`**：
  - `LocaleManager` & `AppLanguage`：运行中中日英多语言切替调度。

---

## 📦 使用说明 (Composite Build / Gradle)

在宿主 APP 项目的 `settings.gradle.kts` 中添加工程引用：

```kotlin
// settings.gradle.kts
includeBuild("../ListenArch")
```

在模块 `build.gradle.kts` 中添加依赖：

```kotlin
dependencies {
    implementation("com.listen:listen-arch:1.0.0")
}
```

---

## 📄 开源许可

基于 [MIT License](LICENSE) 开源。
