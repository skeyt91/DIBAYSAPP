package com.example.dibays.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dibays.BuildConfig
import com.example.dibays.data.auth.AuthRepository
import com.example.dibays.data.dashboard.DashboardRepository
import com.example.dibays.data.session.SessionStore
import com.example.dibays.ui.DashboardViewModel
import com.example.dibays.ui.LoginViewModel
import com.example.dibays.ui.RecoverViewModel
import com.example.dibays.ui.RegisterViewModel
import com.example.dibays.ui.screens.dashboard.DashboardScreen
import com.example.dibays.ui.screens.recover.RecoverAccountScreen
import com.example.dibays.ui.screens.register.RegisterScreen
import com.example.dibays.ui.screens.login.LoginScreen

private const val ROUTE_LOADING = "loading"
private const val ROUTE_LOGIN = "login"
private const val ROUTE_RECOVER_ACCOUNT = "recover_account"
private const val ROUTE_REGISTER_ACCOUNT = "register_account"
private const val ROUTE_DASHBOARD = "dashboard"

@Composable
fun AppNavHost(
    viewModel: LoginViewModel,
    sessionStore: SessionStore,
    authRepository: AuthRepository,
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.ready, uiState.session) {
        if (!uiState.ready) return@LaunchedEffect
        val target = if (uiState.session == null) ROUTE_LOGIN else ROUTE_DASHBOARD
        navController.navigate(target) {
            popUpTo(ROUTE_LOADING) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = ROUTE_LOADING,
    ) {
        composable(ROUTE_LOADING) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        composable(ROUTE_LOGIN) {
            LoginScreen(
                state = uiState,
                onEmailChange = viewModel::onEmailChange,
                onPinChange = viewModel::onPinChange,
                onSubmit = viewModel::login,
                onRecoverAccount = { navController.navigate(ROUTE_RECOVER_ACCOUNT) },
                onRegister = { navController.navigate(ROUTE_REGISTER_ACCOUNT) },
            )
        }

        composable(ROUTE_RECOVER_ACCOUNT) {
            val recoverViewModel: RecoverViewModel = viewModel(
                factory = RecoverViewModel.factory(authRepository),
            )
            val recoverState by recoverViewModel.uiState.collectAsState()

            RecoverAccountScreen(
                state = recoverState,
                onEmailChange = recoverViewModel::onEmailChange,
                onSubmit = recoverViewModel::sendRecoveryEmail,
                onBackToLogin = {
                    navController.popBackStack(ROUTE_LOGIN, inclusive = false)
                },
            )
        }

        composable(ROUTE_REGISTER_ACCOUNT) {
            val registerViewModel: RegisterViewModel = viewModel(
                factory = RegisterViewModel.factory(authRepository, sessionStore),
            )
            val registerState by registerViewModel.uiState.collectAsState()

            RegisterScreen(
                state = registerState,
                onNameChange = registerViewModel::onNameChange,
                onEmailChange = registerViewModel::onEmailChange,
                onPinChange = registerViewModel::onPinChange,
                onConfirmPinChange = registerViewModel::onConfirmPinChange,
                onSubmit = registerViewModel::register,
                onBackToLogin = {
                    navController.popBackStack(ROUTE_LOGIN, inclusive = false)
                },
            )
        }

        composable(ROUTE_DASHBOARD) {
            val session = uiState.session
            val dashboardRepository = remember {
                DashboardRepository(
                    supabaseUrl = BuildConfig.SUPABASE_URL,
                    anonKey = BuildConfig.SUPABASE_ANON_KEY,
                )
            }
            val dashboardViewModel: DashboardViewModel = viewModel(
                key = session?.accessToken ?: "dashboard",
                factory = DashboardViewModel.factory(
                    repository = dashboardRepository,
                    accessToken = session?.accessToken.orEmpty(),
                ),
            )
            val dashboardState by dashboardViewModel.uiState.collectAsState()

            DashboardScreen(
                email = session?.email.orEmpty(),
                state = dashboardState,
                onRefresh = dashboardViewModel::refresh,
                onLogout = viewModel::logout,
            )
        }
    }
}
