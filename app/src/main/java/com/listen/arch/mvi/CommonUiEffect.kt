package com.listen.arch.mvi

/**
 * Universal One-Shot UI Side Effects for MVI ViewModels across Listen applications.
 * Handles common operations such as Toast notifications, Snackbars, System Share intents, and APM logging inspector.
 */
sealed interface CommonUiEffect {
    data class ShowToast(val message: String) : CommonUiEffect
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val onAction: (() -> Unit)? = null
    ) : CommonUiEffect
    data class ShareText(val title: String, val content: String) : CommonUiEffect
    data object OpenApmInspector : CommonUiEffect
    data object LaunchGoogleSignIn : CommonUiEffect
    data class NavigateTo(val route: String) : CommonUiEffect
}
