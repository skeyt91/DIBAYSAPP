package com.example.dibays.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dibays.ui.LoginViewModel
import com.example.dibays.ui.screens.dashboard.DashboardScreen
import com.example.dibays.ui.screens.login.LoginScreen

private const val ROUTE_LOADING = "loading"
private const val ROUTE_LOGIN = "login"
private const val ROUTE_DASHBOARD = "dashboard"

@Composable
fun AppNavHost(viewModel: LoginViewModel) {
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
            )
        }

        composable(ROUTE_DASHBOARD) {
            DashboardScreen(
                email = uiState.session?.email.orEmpty(),
                onLogout = viewModel::logout,
            )
        }
    }
}
