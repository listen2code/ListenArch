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

abstract class BaseViewModel<State, Intent, Effect>(initialState: State) : ViewModel() {

    private val _viewState = MutableStateFlow(initialState)
    val viewState: StateFlow<State> = _viewState.asStateFlow()

    private val _viewEffect = MutableSharedFlow<Effect>()
    val viewEffect: SharedFlow<Effect> = _viewEffect.asSharedFlow()

    protected val currentState: State
        get() = _viewState.value

    abstract fun handleIntent(intent: Intent)

    protected fun updateState(reducer: State.() -> State) {
        _viewState.value = currentState.reducer()
    }

    protected fun emitEffect(builder: () -> Effect) {
        viewModelScope.launch {
            _viewEffect.emit(builder())
        }
    }

    protected fun emitEffect(effect: Effect) {
        viewModelScope.launch {
            _viewEffect.emit(effect)
        }
    }
}
