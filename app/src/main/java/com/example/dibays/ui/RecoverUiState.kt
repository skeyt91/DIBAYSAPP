package com.example.dibays.ui

data class RecoverUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)
