package com.listen.arch.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MVI 架构的基础 ViewModel。
 * 这里仅声明了两个泛型：[State]（UI状态）和 [Intent]（用户意图）。
 * Effect 被硬编码限制为 [CommonUiEffect]，以此简化泛型设计，降低架构复杂度，同时仍能满足大多数通用场景。
 */
abstract class BaseViewModel<State, Intent>(initialState: State) : ViewModel() {

    // MutableStateFlow 适用于状态流，因为新的订阅者总能立即获取（回放）最新的 UI 状态。
    private val _viewState = MutableStateFlow(initialState)
    val viewState: StateFlow<State> = _viewState.asStateFlow()

    // MutableSharedFlow 适用于单次消费的副作用（如 Toast，导航），因为它不保留（不回放）历史值，从而避免屏幕旋转等引起的重复触发。
    private val _viewEffect = MutableSharedFlow<CommonUiEffect>()
    val viewEffect: SharedFlow<CommonUiEffect> = _viewEffect.asSharedFlow()

    protected val currentState: State
        get() = _viewState.value

    abstract fun handleIntent(intent: Intent)

    protected fun updateState(reducer: State.() -> State) {
        _viewState.value = currentState.reducer()
    }

    // 使用 viewModelScope.launch 异步发送副作用，确保此过程非阻塞，
    // 防止在高频状态更新或耗时任务中阻塞调用者（通常是主线程）。
    protected fun emitEffect(builder: () -> CommonUiEffect) {
        viewModelScope.launch {
            _viewEffect.emit(builder())
        }
    }

    protected fun emitEffect(effect: CommonUiEffect) {
        viewModelScope.launch {
            _viewEffect.emit(effect)
        }
    }

    /**
     * 将通用生命周期事件映射为该 ViewModel 专用的业务 Intent。
     * 默认返回 null，表示当前 ViewModel 不关心生命周期事件。
     */
    open fun toLifecycleIntent(event: LifecycleEvent): Intent? = null

    /**
     * 内部生命周期事件分发通道，由顶层路由或调度器统一调用。
     * 若映射出的 Intent 不为 null，则直接送入 [handleIntent] 状态机。
     */
    fun dispatchLifecycleEvent(event: LifecycleEvent) {
        val intent = toLifecycleIntent(event)
        if (intent != null) {
            handleIntent(intent)
        }
    }
}
