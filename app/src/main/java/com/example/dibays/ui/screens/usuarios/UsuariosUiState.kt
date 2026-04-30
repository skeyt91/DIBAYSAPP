package com.example.dibays.ui.screens.usuarios

import com.example.dibays.data.model.Usuario

data class UsuariosUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val usuarios: List<Usuario> = emptyList(),
)
