package com.example.dibays.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dibays.data.auth.AuthRepository
import com.example.dibays.data.session.AuthSession
import com.example.dibays.data.session.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val sessionStore: SessionStore,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionStore.sessionFlow.collectLatest { session ->
                _uiState.update {
                    it.copy(
                        session = session,
                        ready = true,
                        isLoading = false,
                        error = null,
                    )
                }
            }
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, error = null) }
    }

    fun onPinChange(value: String) {
        val filtered = value.filter(Char::isDigit).take(4)
        _uiState.update { it.copy(pin = filtered, error = null) }
    }

    fun login() {
        val state = _uiState.value
        if (!isValidEmail(state.email) || state.pin.length != 4) {
            _uiState.update { it.copy(error = "Ingresa un correo válido y un PIN de 4 dígitos.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                authRepository.signIn(state.email, state.pin)
            }.onSuccess { session ->
                sessionStore.save(session)
                _uiState.update {
                    it.copy(
                        session = session,
                        isLoading = false,
                        error = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "No se pudo iniciar sesión.",
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionStore.clear()
            _uiState.update {
                it.copy(
                    session = null,
                    isLoading = false,
                    error = null,
                    pin = "",
                )
            }
        }
    }

    private fun isValidEmail(value: String): Boolean {
        return value.contains("@") && value.contains(".")
    }

    companion object {
        fun factory(
            sessionStore: SessionStore,
            authRepository: AuthRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(sessionStore, authRepository) as T
            }
        }
    }
}
