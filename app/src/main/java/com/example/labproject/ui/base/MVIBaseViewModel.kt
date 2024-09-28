package com.example.labproject.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

abstract class MVIBaseViewModel<UiState, UiEvent, Effect>: ViewModel() {

    private val _uiState = MutableStateFlow<UiState?>(null)
    val uiState: StateFlow<UiState?> get() = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> get() = _uiEvent

    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> get() = _effect

    protected fun setState(state: UiState) {
        _uiState.value = state
    }

    protected suspend fun sendEvent(event: UiEvent) {
        _uiEvent.emit(event)
    }

    protected suspend fun sendEffect(effect: Effect) {
        _effect.emit(effect)
    }

    abstract fun handleEvent(event: UiEvent)
}