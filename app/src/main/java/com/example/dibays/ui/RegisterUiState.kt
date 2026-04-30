package com.example.dibays.ui

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val pin: String = "",
    val confirmPin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
)
