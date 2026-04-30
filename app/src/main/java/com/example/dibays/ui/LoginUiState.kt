package com.example.dibays.ui

import com.example.dibays.data.session.AuthSession

data class LoginUiState(
    val email: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val session: AuthSession? = null,
    val ready: Boolean = false,
)
