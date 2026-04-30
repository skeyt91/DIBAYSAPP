package com.example.dibays.ui.screens.ventas

import com.example.dibays.data.model.Fardo
import com.example.dibays.data.model.Venta

data class VentasUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val products: List<Fardo> = emptyList(),
    val recentSales: List<Venta> = emptyList(),
    val clienteNombre: String = "",
    val pagoRecibido: String = "",
    val estadoPago: String = "pendiente",
    val cart: List<VentaCartItem> = emptyList(),
)

data class VentaCartItem(
    val productoId: String,
    val nombre: String,
    val stockDisponible: Int,
    val cantidad: Int,
    val precioUnitario: Double,
) {
    val subtotal: Double get() = cantidad * precioUnitario
}
