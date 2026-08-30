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

abstract class BaseViewModel<State, Intent>(initialState: State) : ViewModel() {

    private val _viewState = MutableStateFlow(initialState)
    val viewState: StateFlow<State> = _viewState.asStateFlow()

    private val _viewEffect = MutableSharedFlow<CommonUiEffect>()
    val viewEffect: SharedFlow<CommonUiEffect> = _viewEffect.asSharedFlow()

    protected val currentState: State
        get() = _viewState.value

    abstract fun handleIntent(intent: Intent)

    protected fun updateState(reducer: State.() -> State) {
        _viewState.value = currentState.reducer()
    }

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
