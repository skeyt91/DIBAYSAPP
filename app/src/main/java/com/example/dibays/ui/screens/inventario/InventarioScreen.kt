package com.example.dibays.ui.screens.inventario

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dibays.BuildConfig
import com.example.dibays.data.repository.FardosRepository
import java.util.Locale

@Composable
fun InventarioRoute(
    onBackToDashboard: () -> Unit,
    accessToken: String,
) {
    val repository = androidx.compose.runtime.remember {
        FardosRepository(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            anonKey = BuildConfig.SUPABASE_ANON_KEY,
        )
    }
    val viewModel: InventarioViewModel = viewModel(
        key = accessToken.ifBlank { "inventario" },
        factory = InventarioViewModel.factory(repository, accessToken),
    )
    val state by viewModel.uiState.collectAsState()
    InventarioScreen(
        state = state,
        onBack = onBackToDashboard,
        onQueryChange = viewModel::onQueryChange,
        onNewItem = viewModel::openCreateForm,
        onEditItem = viewModel::editItem,
        onCloseForm = viewModel::closeForm,
        onNombreChange = viewModel::onNombreChange,
        onCodigoChange = viewModel::onCodigoChange,
        onCategoriaChange = viewModel::onCategoriaChange,
        onStockChange = viewModel::onStockChange,
        onPrecioChange = viewModel::onPrecioChange,
        onCostoChange = viewModel::onCostoChange,
        onSave = viewModel::save,
        onRefresh = viewModel::refresh,
        onIncreaseStock = viewModel::increaseStock,
        onDecreaseStock = viewModel::decreaseStock,
    )
}

@Composable
fun InventarioScreen(
    state: InventarioUiState,
    onBack: () -> Unit,
    onQueryChange: (String) -> Unit,
    onNewItem: () -> Unit,
    onEditItem: (com.example.dibays.data.model.Fardo) -> Unit,
    onCloseForm: () -> Unit,
    onNombreChange: (String) -> Unit,
    onCodigoChange: (String) -> Unit,
    onCategoriaChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onPrecioChange: (String) -> Unit,
    onCostoChange: (String) -> Unit,
    onSave: () -> Unit,
    onRefresh: () -> Unit,
    onIncreaseStock: (com.example.dibays.data.model.Fardo) -> Unit,
    onDecreaseStock: (com.example.dibays.data.model.Fardo) -> Unit,
) {
    val filteredItems = state.items.filter { item ->
        val query = state.query.trim()
        query.isBlank() ||
            listOf(item.nombre, item.codigo, item.categoria, item.stock.toString())
                .any { candidate -> candidate.contains(query, ignoreCase = true) }
    }
    val lowStockCount = filteredItems.count { it.stock in 1..5 }
    val totalValue = filteredItems.sumOf { it.stock * it.precio }

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(onClick = onBack) {
                    Text("←")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Volver")
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Inventario",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Gestion de fardos, stock y precios.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                OutlinedButton(onClick = onRefresh) {
                    Text("Actualizar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = onQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Buscar fardos") },
                        singleLine = true,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricChip(
                            title = "Items",
                            value = filteredItems.size.toString(),
                            modifier = Modifier.weight(1f),
                        )
                        MetricChip(
                            title = "Bajo stock",
                            value = lowStockCount.toString(),
                            modifier = Modifier.weight(1f),
                        )
                        MetricChip(
                            title = "Valor Bs",
                            value = totalValue.formatMoney(),
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    state.error?.let { error ->
                        ErrorCard(error = error)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (filteredItems.isEmpty()) {
                        EmptyInventoryState(
                            title = if (state.query.isBlank()) "Sin fardos" else "Sin resultados",
                            subtitle = if (state.query.isBlank()) {
                                "Crea tu primer fardo para comenzar a operar."
                            } else {
                                "No encontramos coincidencias con la busqueda actual."
                            },
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 96.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(filteredItems, key = { it.id }) { item ->
                                FardoCard(
                                    item = item,
                                    onEdit = { onEditItem(item) },
                                    onIncrease = { onIncreaseStock(item) },
                                    onDecrease = { onDecreaseStock(item) },
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onNewItem,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
        ) {
            Text("+")
        }

        if (state.showForm) {
            FardoFormDialog(
                state = state,
                onDismiss = onCloseForm,
                onNombreChange = onNombreChange,
                onCodigoChange = onCodigoChange,
                onCategoriaChange = onCategoriaChange,
                onStockChange = onStockChange,
                onPrecioChange = onPrecioChange,
                onCostoChange = onCostoChange,
                onSave = onSave,
            )
        }
    }
}

@Composable
private fun FardoCard(
    item: com.example.dibays.data.model.Fardo,
    onEdit: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
) {
    val isLowStock = item.stock in 1..5
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = listOfNotNull(
                            item.codigo.takeIf { it.isNotBlank() },
                            item.categoria.takeIf { it.isNotBlank() },
                        ).joinToString("  •  ").ifBlank { "Sin codigo ni categoria" },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Stock ${item.stock}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isLowStock) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = "Bs ${item.precio.formatMoney()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onDecrease) {
                    Text("−")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("-1")
                }
                OutlinedButton(onClick = onIncrease) {
                    Text("+")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+1")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = onEdit) {
                    Text("Editar")
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun ErrorCard(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun EmptyInventoryState(title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FardoFormDialog(
    state: InventarioUiState,
    onDismiss: () -> Unit,
    onNombreChange: (String) -> Unit,
    onCodigoChange: (String) -> Unit,
    onCategoriaChange: (String) -> Unit,
    onStockChange: (String) -> Unit,
    onPrecioChange: (String) -> Unit,
    onCostoChange: (String) -> Unit,
    onSave: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(scrollState),
            ) {
                Text(
                    text = if (state.editingId == null) "Nuevo fardo" else "Editar fardo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Carga nombre, codigo, categoria, stock, precio y costo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = onNombreChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.codigo,
                    onValueChange = onCodigoChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Codigo") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.categoria,
                    onValueChange = onCategoriaChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Categoria") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = state.stock,
                        onValueChange = onStockChange,
                        modifier = Modifier.weight(1f),
                        label = { Text("Stock") },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = state.precio,
                        onValueChange = onPrecioChange,
                        modifier = Modifier.weight(1f),
                        label = { Text("Precio") },
                        singleLine = true,
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = state.costo,
                    onValueChange = onCostoChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Costo") },
                    singleLine = true,
                )

                state.error?.let {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        enabled = !state.isSaving,
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onSave,
                        enabled = !state.isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        if (state.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

private fun Double.formatMoney(): String {
    return if (this % 1.0 == 0.0) {
        toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", this)
    }
}
