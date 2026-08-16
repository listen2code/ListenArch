# ListenArch

<p align="center">
  <strong>通用 Android 核心架构与基础设施库 (Universal Android Core Architecture SDK)</strong>
</p>

---

## 📖 简介

`ListenArch` 是 Listen 系列原生 Android 应用的核心架构库。提供业务无关、可复用的底层基础设施，方便在记账、记事、习惯打卡等多个 Android 项目间无缝共享标准架构。

---

## 🌟 核心职责

- **Room 数据库**：基类 DB、Base DAO 与通用 TypeConverters 序列化工具。
- **DataStore 偏好设置**：Key-Value 状态与偏好设置持久化管理器。
- **云端同步 Client**：Google Drive REST API (`appDataFolder`) 无服务器数据备份与恢复引擎。
- **多语言管理 (i18n)**：LocaleManager 运行时语言动态切替与 Resource 助手。
- **MVI 架构扩展**：ViewModel 响应式状态流 (StateFlow/SharedFlow)、SingleLiveEvent 与 Kotlin Coroutines 扩展。

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
