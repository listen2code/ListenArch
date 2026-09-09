package com.listen.arch.mvi

/**
 * Universal One-Shot UI Side Effects for MVI ViewModels across Listen applications.
 * Handles common operations such as Toast notifications, Snackbars, System Share intents, and APM logging inspector.
 *
 * 这里设计成 `interface` 而不是 `sealed interface`，是因为其允许不同的宿主应用扩展自定义的副作用，增强了架构的灵活性和可插拔性。
 */
interface CommonUiEffect {
    // 用于显示普通的 Toast 提示
    data class ShowToast(val message: String) : CommonUiEffect
    // 用于显示底部的 Snackbar，并支持可选的交互动作
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val onAction: (() -> Unit)? = null
    ) : CommonUiEffect
    // 触发系统的文本分享意图
    data class ShareText(val title: String, val content: String) : CommonUiEffect
    // 应用内路由导航
    data class NavigateTo(val route: String) : CommonUiEffect
    // 返回上一级页面
    data object NavigateBack : CommonUiEffect
    data class OpenUrl(val url: String) : CommonUiEffect
    data object HideKeyboard : CommonUiEffect
}
