package com.listen.arch.mvi

/**
 * 画面级生命周期通用事件定义 (LifecycleEvent)。
 *
 * 规范说明：
 * - [ON_APPEAR]：画面进入前台（切入当前 Tab，或应用从后台/其他界面切回前台）；
 * - [ON_DISAPPEAR]：画面离开前台（切出当前 Tab，或应用退入系统后台/被新界面遮挡）。
 */
enum class LifecycleEvent {
    ON_APPEAR,
    ON_DISAPPEAR
}
