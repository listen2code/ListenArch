# ListenArch

<p align="center">
  <strong>通用 Android 核心架构与基础设施库 (Universal Android Core Architecture SDK)</strong>
</p>

---

## 📖 简介

`ListenArch` 是 Listen 系列原生 Android 应用的核心架构 SDK。提供业务无关、高度可复用的底层基础设施，包含 Room SQLite 数据库封装、`BaseDataStoreManager` Google 账户与偏好持久化、MVI 单向数据流基类 (`BaseViewModel`)、`CloudSyncManager` 账号隔离云端同步引擎、`ApmLogger` 500 条环形内存日志、`TraceManager` 全链路毫秒打点与 `StringsRes` 多语言字典。

---

## 🌟 核心模块结构 (`com.listen.arch`)

- **`mvi/`**：
  - `BaseViewModel<ViewState, UserIntent, ViewEffect>`：响应式 StateFlow 与 Channel 事件驱动 MVI 核心基类（包含 `handleIntent`, `updateState`, `emitEffect`）。
  - `ResultExtensions.kt`：Kotlin 原生 `Result<T>` 与 `Flow.asResult()` / `safeCall {}` 错误收敛扩展。
- **`data/db/`**：
  - `TransactionEntity`：账单数据实体表。
  - `TransactionDao`：Flow 响应式 SQL 增删改查、区间查询与批量插入 DAO。
  - `AppDatabase`：Room SQLite 单例数据库。
- **`data/pref/`**：
  - `BaseDataStoreManager`：基于 DataStore Preferences 的多语言、主题、强调色、货币符号、月度预算、Google 账户（邮箱/昵称/头像）全通用配置管理者。
- **`sync/`**：
  - `CloudSyncManager`：Google 账户隔离的云端备份与恢复引擎，支持 MD5 校验和与 `SyncState` 响应式数据流。
- **`apm/`**：
  - `ApmLogger`：500 条内存环形链表日志（APP/DB/SYNC/CRASH 四大频道）。
  - `TraceManager`：分布式 `traceId` 链路追踪与执行耗时打点。
  - `CrashHandler`：未捕获全局异常拦截与保护。
- **`i18n/`**：
  - `StringsRes` & `AppLanguage`：运行中免重启的中/英/日多语言字典调度系统。

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
    implementation("com.listen:listen-arch")
}
```

---

## 📄 开源许可

基于 [MIT License](LICENSE) 开源。
