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
import com.example.dibays.ui.screens.inventario.InventarioRoute
import com.example.dibays.ui.screens.usuarios.UsuariosRoute
import com.example.dibays.ui.screens.ventas.VentasRoute
import com.example.dibays.ui.screens.recover.RecoverAccountScreen
import com.example.dibays.ui.screens.shared.FeatureScreen
import com.example.dibays.ui.screens.register.RegisterScreen
import com.example.dibays.ui.screens.login.LoginScreen

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
        val target = if (uiState.session == null) Screen.Login.route else Screen.Dashboard.route
        navController.navigate(target) {
            popUpTo(Screen.Loading.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Loading.route,
    ) {
        composable(Screen.Loading.route) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        composable(Screen.Login.route) {
            LoginScreen(
                state = uiState,
                onEmailChange = viewModel::onEmailChange,
                onPinChange = viewModel::onPinChange,
                onSubmit = viewModel::login,
                onRecoverAccount = { navController.navigate(Screen.RecoverAccount.route) },
                onRegister = { navController.navigate(Screen.RegisterAccount.route) },
            )
        }

        composable(Screen.RecoverAccount.route) {
            val recoverViewModel: RecoverViewModel = viewModel(
                factory = RecoverViewModel.factory(authRepository),
            )
            val recoverState by recoverViewModel.uiState.collectAsState()

            RecoverAccountScreen(
                state = recoverState,
                onEmailChange = recoverViewModel::onEmailChange,
                onSubmit = recoverViewModel::sendRecoveryEmail,
                onBackToLogin = {
                    navController.popBackStack(Screen.Login.route, inclusive = false)
                },
            )
        }

        composable(Screen.RegisterAccount.route) {
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
                    navController.popBackStack(Screen.Login.route, inclusive = false)
                },
            )
        }

        composable(Screen.Dashboard.route) {
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
                onOpenInventory = { navController.navigate(Screen.Inventory.route) },
                onOpenSales = { navController.navigate(Screen.Sales.route) },
                onOpenClients = { navController.navigate(Screen.Clients.route) },
                onOpenProviders = { navController.navigate(Screen.Providers.route) },
                onOpenUsers = { navController.navigate(Screen.Users.route) },
                onOpenReports = { navController.navigate(Screen.Reports.route) },
            )
        }

        composable(Screen.Inventory.route) {
            val session = uiState.session
            InventarioRoute(
                onBackToDashboard = {
                    navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                },
                accessToken = session?.accessToken.orEmpty(),
            )
        }

        composable(Screen.Sales.route) {
            val session = uiState.session
            VentasRoute(
                onBackToDashboard = {
                    navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                },
                accessToken = session?.accessToken.orEmpty(),
            )
        }

        composable(Screen.Clients.route) {
            FeatureScreen(
                title = "Clientes",
                description = "Controla compradores, saldos pendientes y contacto rapido.",
                actionLabel = "Volver al dashboard",
                onAction = { navController.popBackStack(Screen.Dashboard.route, inclusive = false) },
            )
        }

        composable(Screen.Providers.route) {
            FeatureScreen(
                title = "Proveedores",
                description = "Administra piloteros, origen de mercaderia y cuentas.",
                actionLabel = "Volver al dashboard",
                onAction = { navController.popBackStack(Screen.Dashboard.route, inclusive = false) },
            )
        }

        composable(Screen.Users.route) {
            val session = uiState.session
            UsuariosRoute(
                onBackToDashboard = {
                    navController.popBackStack(Screen.Dashboard.route, inclusive = false)
                },
                accessToken = session?.accessToken.orEmpty(),
            )
        }

        composable(Screen.Reports.route) {
            FeatureScreen(
                title = "Reportes",
                description = "Resumen financiero, utilidad, pendientes y rentabilidad.",
                actionLabel = "Volver al dashboard",
                onAction = { navController.popBackStack(Screen.Dashboard.route, inclusive = false) },
            )
        }
    }
}
