package com.example.dibays.ui.screens.usuarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dibays.data.repository.UsuariosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuariosViewModel(
    private val repository: UsuariosRepository,
    private val accessToken: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsuariosUiState())
    val uiState: StateFlow<UsuariosUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.list(accessToken)
            }.onSuccess { usuarios ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        usuarios = usuarios,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "No se pudieron cargar los usuarios.",
                    )
                }
            }
        }
    }

    companion object {
        fun factory(
            repository: UsuariosRepository,
            accessToken: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UsuariosViewModel(repository, accessToken) as T
            }
        }
    }
}
