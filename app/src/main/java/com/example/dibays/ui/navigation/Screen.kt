package com.example.dibays.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Loading : Screen("loading", "Cargando")
    data object Login : Screen("login", "Login")
    data object RecoverAccount : Screen("recover_account", "Recuperar cuenta")
    data object RegisterAccount : Screen("register_account", "Registrar cuenta")
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object Inventory : Screen("inventario", "Inventario")
    data object Sales : Screen("ventas", "Ventas")
    data object Clients : Screen("clientes", "Clientes")
    data object Providers : Screen("proveedores", "Proveedores")
    data object Users : Screen("usuarios", "Usuarios")
    data object Expenses : Screen("gastos", "Gastos")
    data object CatalogAdmin : Screen("catalogo_admin", "Catalogo admin")
    data object CatalogPublic : Screen("catalogo_publico", "Catalogo publico")
    data object Reports : Screen("reportes", "Reportes")
    data object Statistics : Screen("estadisticas", "Estadisticas")
    data object History : Screen("historial", "Historial")
}
