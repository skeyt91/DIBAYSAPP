package com.example.dibays.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dibays.DibaysApplication
import com.example.dibays.ui.navigation.AppNavHost
import com.example.dibays.ui.theme.DibaysTheme

@Composable
fun DibaysApp() {
    val context = LocalContext.current.applicationContext
    val container = remember {
        (context as DibaysApplication).container
    }
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.factory(container.sessionStore, container.authRepository)
    )

    DibaysTheme {
        AppNavHost(
            viewModel = loginViewModel,
            sessionStore = container.sessionStore,
            authRepository = container.authRepository,
        )
    }
}
