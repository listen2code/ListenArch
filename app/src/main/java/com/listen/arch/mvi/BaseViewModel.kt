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
}
