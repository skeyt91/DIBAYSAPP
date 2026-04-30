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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
private val DrawerBorder = Color(0xFF2E394E)

@Composable
fun DashboardScreen(
    email: String,
    state: DashboardUiState,
    onRefresh: () -> Unit,
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
                    onNavigate = { route ->
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigateToRoute(route)
                    },
                    activeRoute = Screen.Dashboard.route,
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
                    .verticalScroll(rememberScrollState()),
            ) {
                HeaderSection(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onRegisterSale = onOpenSales,
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    MockupCard(
                        title = if (state.products.isEmpty()) {
                            "¿Empezamos por crear tu primer producto?"
                        } else {
                            "Ya tienes productos en tu inventario"
                        },
                        subtitle = if (state.products.isEmpty()) {
                            "Es simple, solo necesitas un nombre y un precio."
                        } else {
                            "Sigue sumando inventario y registra nuevas ventas."
                        },
                        actionLabel = "+ Añadir producto",
                        onAction = onOpenInventory,
                        illustration = { ProductIllustration() },
                    )

                    MockupCard(
                        title = "Historial de ventas",
                        subtitle = "Revisa el movimiento del dia o simula una nueva venta.",
                        actionLabel = "+ Simular una venta",
                        onAction = onOpenSales,
                        illustration = { RegisterIllustration() },
                    )

                    state.error?.let { error ->
                        ErrorCard(error = error)
                    }

                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Usuarios activos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F2937),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Toca Usuarios en el menu lateral para ver colaboradores y accesos.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF6B7280),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(onClick = onOpenUsers) {
                                Text("Abrir usuarios")
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Estado rapido",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F2937),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Productos cargados: ${state.products.size}",
                                color = Color(0xFF6B7280),
                            )
                            Text(
                                text = "Sesion: ${email.ifBlank { "Activa" }}",
                                color = Color(0xFF6B7280),
                            )
                        }
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
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HeaderBg)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
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

        Spacer(modifier = Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "HOY",
                color = Teal,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(10.dp))
            EyeGlyph()
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "$0.00",
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "en 0 ventas completadas",
            color = Teal,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onRegisterSale,
            colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
        ) {
            Text(
                text = "Registrar venta",
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun DrawerContent(
    onNavigate: (String) -> Unit,
    activeRoute: String,
) {
    val items = listOf(
        DrawerEntry("Inicio", Screen.Dashboard.route, isActive = true),
        DrawerEntry("Vender", Screen.Sales.route),
        DrawerEntry("Pedidos", Screen.Orders.route, badge = "0"),
        DrawerEntry("Productos", Screen.Inventory.route),
        DrawerEntry("Asistente Inteligente", Screen.Assistant.route),
        DrawerEntry("Catalogo Online", Screen.CatalogPublic.route),
        DrawerEntry("Finanzas", Screen.Finances.route),
        DrawerEntry("Cupon", Screen.Coupon.route, badge = "NUEVO"),
        DrawerEntry("Clientes", Screen.Clients.route),
        DrawerEntry("Transacciones", Screen.Transactions.route),
        DrawerEntry("Estadisticas", Screen.Reports.route),
        DrawerEntry("Usuarios", Screen.Users.route),
        DrawerEntry("Configuraciones", Screen.Settings.route),
        DrawerEntry("Ayuda", Screen.Help.route),
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
        Spacer(modifier = Modifier.height(18.dp))

        items.forEach { item ->
            DrawerMenuItem(
                item = item.copy(isActive = item.route == activeRoute || item.isActive),
                onClick = {
                    if (item.route != activeRoute) {
                        onNavigate(item.route)
                    }
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
        Text(
            text = item.title,
            color = Color.White,
            modifier = Modifier.weight(1f),
            fontWeight = if (item.isActive) FontWeight.SemiBold else FontWeight.Normal,
        )
        item.badge?.let { badge ->
            Spacer(modifier = Modifier.width(8.dp))
            if (badge == "NUEVO") {
                NewBadge()
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
        Text(
            text = text,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun NewBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 1.dp)
            .background(Color.Transparent),
    ) {
        Text(
            text = "NUEVO",
            color = Teal,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
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
        Text(
            text = ">",
            color = HeaderBg,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
    }
}

@Composable
private fun MockupCard(
    title: String,
    subtitle: String,
    actionLabel: String,
    onAction: () -> Unit,
    illustration: @Composable () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            illustration()
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = Teal, contentColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(actionLabel, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun ProductIllustration() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniTile("P")
            MiniTile("G")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniTile("T")
            MiniTile("I")
        }
    }
}

@Composable
private fun RegisterIllustration() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MiniTile("▣", tint = Color(0xFF6B7280))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MiniTile("—", tint = Color(0xFF6B7280))
            MiniTile("—", tint = Color(0xFF6B7280))
        }
    }
}

@Composable
private fun MiniTile(symbol: String, tint: Color = Teal) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Color(0xFFF6F7FA)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = symbol,
            color = tint,
            fontWeight = FontWeight.Bold,
        )
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
        Text(
            text = "◆",
            color = Teal,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
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
private fun EyeGlyph() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "◉",
            color = Teal,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ErrorCard(error: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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
    val badge: String? = null,
    val isActive: Boolean = false,
)
