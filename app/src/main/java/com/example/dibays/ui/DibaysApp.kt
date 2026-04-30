package com.example.dibays.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dibays.BuildConfig
import com.example.dibays.data.auth.AuthRepository
import com.example.dibays.data.session.SessionStore
import com.example.dibays.ui.navigation.AppNavHost
import com.example.dibays.ui.theme.DibaysTheme

@Composable
fun DibaysApp() {
    val context = LocalContext.current.applicationContext
    val sessionStore = remember { SessionStore(context) }
    val authRepository = remember {
        AuthRepository(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            anonKey = BuildConfig.SUPABASE_ANON_KEY,
        )
    }
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.factory(sessionStore, authRepository)
    )

    DibaysTheme {
        AppNavHost(loginViewModel)
    }
}
