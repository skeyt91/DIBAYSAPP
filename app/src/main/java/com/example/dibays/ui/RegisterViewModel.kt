package com.example.dibays.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dibays.data.auth.AuthRepository
import com.example.dibays.data.session.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val sessionStore: SessionStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, error = null, successMessage = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, error = null, successMessage = null) }
    }

    fun onPinChange(value: String) {
        _uiState.update { it.copy(pin = value.filter(Char::isDigit).take(4), error = null, successMessage = null) }
    }

    fun onConfirmPinChange(value: String) {
        _uiState.update { it.copy(confirmPin = value.filter(Char::isDigit).take(4), error = null, successMessage = null) }
    }

    fun register() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Escribe el nombre de la cuenta.") }
            return
        }
        if (!isValidEmail(state.email)) {
            _uiState.update { it.copy(error = "Escribe un correo valido.") }
            return
        }
        if (state.pin.length != 4) {
            _uiState.update { it.copy(error = "El PIN debe tener 4 digitos.") }
            return
        }
        if (state.pin != state.confirmPin) {
            _uiState.update { it.copy(error = "Los PIN no coinciden.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            runCatching {
                authRepository.signUp(state.name, state.email, state.pin)
            }.onSuccess { session ->
                if (session != null) {
                    sessionStore.save(session)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            successMessage = "Cuenta creada. Entrando al sistema...",
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            successMessage = "Cuenta creada. Revisa tu correo para confirmar el acceso.",
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "No se pudo crear la cuenta.",
                    )
                }
            }
        }
    }

    private fun isValidEmail(value: String): Boolean {
        return value.contains("@") && value.contains(".")
    }

    companion object {
        fun factory(
            authRepository: AuthRepository,
            sessionStore: SessionStore,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegisterViewModel(authRepository, sessionStore) as T
            }
        }
    }
}
