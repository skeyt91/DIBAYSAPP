package com.example.dibays.ui.screens.ventas

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dibays.BuildConfig
import com.example.dibays.data.repository.FardosRepository
import com.example.dibays.data.repository.VentasRepository
import java.util.Locale

@Composable
fun VentasRoute(
    onBackToDashboard: () -> Unit,
    accessToken: String,
) {
    val fardosRepository = remember {
        FardosRepository(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            anonKey = BuildConfig.SUPABASE_ANON_KEY,
        )
    }
    val ventasRepository = remember {
        VentasRepository(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            anonKey = BuildConfig.SUPABASE_ANON_KEY,
        )
    }
    val viewModel: VentasViewModel = viewModel(
        key = accessToken.ifBlank { "ventas" },
        factory = VentasViewModel.factory(fardosRepository, ventasRepository, accessToken),
    )
    val state by viewModel.uiState.collectAsState()

    VentasScreen(
        state = state,
        onBack = onBackToDashboard,
        onQueryChange = viewModel::onQueryChange,
        onClienteChange = viewModel::onClienteChange,
        onPagoRecibidoChange = viewModel::onPagoRecibidoChange,
        onEstadoPagoChange = viewModel::onEstadoPagoChange,
        onAddProduct = viewModel::addProduct,
        onIncreaseItem = viewModel::increaseItem,
        onDecreaseItem = viewModel::decreaseItem,
        onRemoveItem = viewModel::removeItem,
        onSubmit = viewModel::submitSale,
        onRefresh = viewModel::refresh,
    )
}

@Composable
fun VentasScreen(
    state: VentasUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onClienteChange: (String) -> Unit,
    onPagoRecibidoChange: (String) -> Unit,
    onEstadoPagoChange: (String) -> Unit,
    onAddProduct: (com.example.dibays.data.model.Fardo) -> Unit,
    onIncreaseItem: (String) -> Unit,
    onDecreaseItem: (String) -> Unit,
    onRemoveItem: (String) -> Unit,
    onSubmit: () -> Unit,
    onRefresh: () -> Unit,
) {
    val filteredProducts = state.products.filter { product ->
        val query = state.query.trim()
        query.isBlank() || listOf(product.nombre, product.codigo, product.categoria)
            .any { it.contains(query, ignoreCase = true) }
    }
    val cartTotal = state.cart.sumOf { it.subtotal }
    val pagoRecibido = state.pagoRecibido.toDoubleOrNull() ?: 0.0
    val saldo = (cartTotal - pagoRecibido).coerceAtLeast(0.0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(onClick = onBack) {
                    Text("← Volver")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ventas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Carrito, pago y salida de stock.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                OutlinedButton(onClick = onRefresh) {
                    Text("Actualizar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = state.clienteNombre,
                                onValueChange = onClienteChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Cliente") },
                                singleLine = true,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = state.pagoRecibido,
                                onValueChange = onPagoRecibidoChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Pago recibido") },
                                singleLine = true,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { onEstadoPagoChange("pendiente") },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Pendiente")
                                }
                                Button(
                                    onClick = { onEstadoPagoChange("pagado") },
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text("Pagado")
                                }
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Buscar producto",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = state.query,
                                onValueChange = onQueryChange,
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Nombre, codigo o categoria") },
                                singleLine = true,
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (filteredProducts.isEmpty()) {
                                Text(
                                    text = "No hay productos para esta busqueda.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                LazyColumn(
                                    contentPadding = PaddingValues(bottom = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                ) {
                                    items(filteredProducts, key = { it.id }) { product ->
                                        ProductSaleCard(
                                            product = product,
                                            onAdd = { onAddProduct(product) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Carrito",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            if (state.cart.isEmpty()) {
                                Text(
                                    text = "Aun no agregaste productos.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                state.cart.forEach { item ->
                                    CartItemRow(
                                        item = item,
                                        onIncrease = { onIncreaseItem(item.productoId) },
                                        onDecrease = { onDecreaseItem(item.productoId) },
                                        onRemove = { onRemoveItem(item.productoId) },
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                                Divider()
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Total Bs ${cartTotal.formatMoney()}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Text(
                                    text = "Saldo Bs ${saldo.formatMoney()}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onSubmit,
                                    enabled = !state.isSaving,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    if (state.isSaving) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.width(18.dp).height(18.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.onPrimary,
                                        )
                                    } else {
                                        Text("Registrar venta")
                                    }
                                }
                            }
                        }
                    }

                    state.error?.let { error ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        ) {
                            Text(
                                text = error,
                                modifier = Modifier.padding(14.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Ventas recientes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            if (state.recentSales.isEmpty()) {
                                Text(
                                    text = "Sin ventas registradas todavia.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            } else {
                                state.recentSales.forEach { sale ->
                                    RecentSaleRow(sale = sale)
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductSaleCard(
    product: com.example.dibays.data.model.Fardo,
    onAdd: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = listOfNotNull(
                        product.codigo.takeIf { it.isNotBlank() },
                        product.categoria.takeIf { it.isNotBlank() },
                        "Stock ${product.stock}",
                    ).joinToString(" • "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Bs ${product.precio.formatMoney()}",
                    fontWeight = FontWeight.SemiBold,
                )
                OutlinedButton(onClick = onAdd) {
                    Text("Agregar")
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: VentaCartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Bs ${item.precioUnitario.formatMoney()} x ${item.cantidad}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = "Bs ${item.subtotal.formatMoney()}",
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDecrease) { Text("−") }
                OutlinedButton(onClick = onIncrease) { Text("+") }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(onClick = onRemove) { Text("Quitar") }
            }
        }
    }
}

@Composable
private fun RecentSaleRow(sale: com.example.dibays.data.model.Venta) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sale.clienteNombre,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = sale.estadoPago,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Bs ${sale.total.formatMoney()}",
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = sale.createdAt.take(16).replace("T", " "),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun Double.formatMoney(): String = if (this % 1.0 == 0.0) {
    toLong().toString()
} else {
    String.format(Locale.US, "%.2f", this)
}
