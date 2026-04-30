package com.example.dibays.data.model

data class Venta(
    val id: String,
    val clienteNombre: String,
    val estadoPago: String,
    val total: Double,
    val pagoRecibido: Double,
    val saldo: Double,
    val createdAt: String,
)

data class VentaItem(
    val id: String,
    val ventaId: String,
    val productoId: String?,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double,
)
