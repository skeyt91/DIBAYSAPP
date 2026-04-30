package com.example.dibays.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dibays.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecoverViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecoverUiState())
    val uiState: StateFlow<RecoverUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, error = null, successMessage = null) }
    }

    fun sendRecoveryEmail() {
        val email = _uiState.value.email
        if (!isValidEmail(email)) {
            _uiState.update { it.copy(error = "Escribe un correo valido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            runCatching {
                authRepository.sendRecoveryEmail(email)
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Te enviamos un correo para recuperar la cuenta.",
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "No se pudo enviar el correo.",
                    )
                }
            }
        }
    }

    private fun isValidEmail(value: String): Boolean {
        return value.contains("@") && value.contains(".")
    }

    companion object {
        fun factory(authRepository: AuthRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecoverViewModel(authRepository) as T
            }
        }
    }
}
