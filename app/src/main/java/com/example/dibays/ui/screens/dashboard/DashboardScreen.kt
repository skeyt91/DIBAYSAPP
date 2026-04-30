package com.example.dibays.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dibays.data.dashboard.ProductSummary
import com.example.dibays.ui.DashboardUiState
import com.example.dibays.ui.navigation.Screen
import kotlinx.coroutines.launch

private val HeaderBg = Color(0xFF1E2535)
private val SurfaceBg = Color(0xFFF2F3F5)
private val Teal = Color(0xFF2ECC9A)
private val DrawerBg = Color(0xFF1E2535)

@Composable
fun DashboardScreen(
    email: String,
    state: DashboardUiState,
    onRefresh: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onLogout: () -> Unit,
    onOpenInventory: () -> Unit,
    onOpenSales: () -> Unit,
    onOpenClients: () -> Unit,
    onOpenProviders: () -> Unit,
    onOpenUsers: () -> Unit,
    onOpenReports: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val filteredProducts = state.products.filter { product ->
        val query = state.searchQuery.trim()
        query.isBlank() || listOf(product.name, product.code, product.category)
            .any { candidate -> candidate.contains(query, ignoreCase = true) }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxWidth(0.74f)
                    .widthIn(max = 340.dp),
                drawerContainerColor = DrawerBg,
            ) {
                DrawerContent(
                    activeRoute = Screen.Dashboard.route,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        onNavigateToRoute(route)
                    },
                )
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 88.dp),
            ) {
                HeaderSection(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onRegisterSale = onOpenSales,
                    onSearchQueryChange = onSearchQueryChange,
                    searchQuery = state.searchQuery,
                    productsCount = state.products.size,
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 760.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    if (state.searchQuery.isNotBlank()) {
                        SearchResultsCard(
                            query = state.searchQuery,
                            products = filteredProducts,
                            onOpenInventory = onOpenInventory,
                        )
                    }

                    SummaryCard("Productos", state.products.size.toString(), onOpenInventory)
                    SummaryCard("Ventas", "0", onOpenSales)
                    SummaryCard("Usuarios", "Ver", onOpenUsers)

                    state.error?.let { error ->
                        ErrorCard(error)
                    }
                }
            }

            FloatingAppButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(18.dp),
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Teal)
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onMenuClick: () -> Unit,
    onRegisterSale: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    searchQuery: String,
    productsCount: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HeaderBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onMenuClick)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    HamburgerIcon()
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Inicio",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            OutlinedButton(onClick = onRegisterSale) {
                Text("Registrar venta")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "HOY",
                    color = Teal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$0.00",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "en 0 ventas completadas",
                    color = Teal,
                    fontSize = 13.sp,
                )
            }

            Text(text = "◉", color = Teal, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            singleLine = true,
            label = { Text("Buscar en inventario") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Productos cargados: $productsCount",
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun SearchResultsCard(
    query: String,
    products: List<ProductSummary>,
    onOpenInventory: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (products.isEmpty()) "Sin resultados para \"$query\"" else "Resultados para \"$query\"",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (products.isEmpty()) {
                Text(
                    text = "No encontramos coincidencias en el inventario.",
                    color = Color(0xFF6B7280),
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(onClick = onOpenInventory) {
                    Text("Abrir inventario")
                }
            } else {
                products.take(5).forEach { product ->
                    ProductRow(product)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    value: String,
    onAction: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = value, color = Color(0xFF6B7280))
            }

            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(if (label == "Ventas") "Ver ventas" else "Abrir")
            }
        }
    }
}

@Composable
private fun DrawerContent(
    onNavigate: (String) -> Unit,
    activeRoute: String,
) {
    val entries = listOf(
        DrawerEntry("Inicio", Screen.Dashboard.route, Icons.Default.Home, isActive = true),
        DrawerEntry("Vender", Screen.Sales.route, Icons.Default.PointOfSale),
        DrawerEntry("Pedidos", Screen.Orders.route, Icons.Default.Upcoming, badge = "0"),
        DrawerEntry("Productos", Screen.Inventory.route, Icons.Default.Inventory2),
        DrawerEntry("Asistente Inteligente", Screen.Assistant.route, Icons.Default.Assistant),
        DrawerEntry("Catalogo Online", Screen.CatalogPublic.route, Icons.Default.Storefront),
        DrawerEntry("Finanzas", Screen.Finances.route, Icons.Default.AccountBalance),
        DrawerEntry("Cupon", Screen.Coupon.route, Icons.Default.LocalOffer, badge = "NUEVO"),
        DrawerEntry("Clientes", Screen.Clients.route, Icons.Default.People),
        DrawerEntry("Transacciones", Screen.Transactions.route, Icons.Default.SwapHoriz),
        DrawerEntry("Estadisticas", Screen.Reports.route, Icons.Default.QueryStats),
        DrawerEntry("Usuarios", Screen.Users.route, Icons.Default.Groups),
        DrawerEntry("Configuraciones", Screen.Settings.route, Icons.Default.Settings),
        DrawerEntry("Ayuda", Screen.Help.route, Icons.Default.HelpOutline),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 18.dp),
    ) {
        Text(
            text = "DIBAYS",
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Menu",
            color = Color.White.copy(alpha = 0.55f),
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(14.dp))

        entries.forEach { item ->
            DrawerMenuItem(
                item = item.copy(isActive = item.route == activeRoute || item.isActive),
                onClick = {
                    if (item.route != activeRoute) onNavigate(item.route)
                },
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.weight(1f))
        BottomAccountBanner()
    }
}

@Composable
private fun DrawerMenuItem(
    item: DrawerEntry,
    onClick: () -> Unit,
) {
    val background = if (item.isActive) Color(0xFF243045) else Color.Transparent
    val stripeColor = if (item.isActive) Teal else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(18.dp)
                .background(stripeColor, RoundedCornerShape(99.dp)),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            tint = if (item.isActive) Teal else Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.title,
            color = Color.White,
            modifier = Modifier.weight(1f),
            fontWeight = if (item.isActive) FontWeight.SemiBold else FontWeight.Normal,
        )
        item.badge?.let { badge ->
            Spacer(modifier = Modifier.width(8.dp))
            if (badge == "NUEVO") {
                Text(text = "NUEVO", color = Teal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            } else {
                CounterBadge(text = badge)
            }
        }
    }
}

@Composable
private fun CounterBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Teal)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BottomAccountBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Teal)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Confirme su cuenta",
                color = HeaderBg,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Para mantener sus datos seguros",
                color = HeaderBg.copy(alpha = 0.85f),
                fontSize = 12.sp,
            )
        }
        Text(text = ">", color = HeaderBg, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}

@Composable
private fun ProductRow(product: ProductSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = product.category.takeIf { it.isNotBlank() }?.take(1)?.uppercase() ?: "P",
                    color = Teal,
                    fontWeight = FontWeight.Bold,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontWeight = FontWeight.SemiBold)
                Text(
                    text = listOfNotNull(
                        product.code.takeIf { it.isNotBlank() },
                        product.category.takeIf { it.isNotBlank() },
                    ).joinToString(" · ").ifBlank { "Sin codigo ni categoria" },
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp,
                )
            }

            Text(text = "Stock ${product.stock}", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun FloatingAppButton(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(HeaderBg),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "◆", color = Teal, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HamburgerIcon() {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .width(14.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White.copy(alpha = 0.9f)),
            )
        }
    }
}

@Composable
private fun ErrorCard(error: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Text(
            text = error,
            modifier = Modifier.padding(14.dp),
            color = Color(0xFFB91C1C),
        )
    }
}

private data class DrawerEntry(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val badge: String? = null,
    val isActive: Boolean = false,
)
