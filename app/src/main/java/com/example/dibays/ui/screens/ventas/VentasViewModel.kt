package com.example.dibays.ui.screens.ventas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dibays.data.model.Fardo
import com.example.dibays.data.repository.FardosRepository
import com.example.dibays.data.repository.VentasRepository
import com.example.dibays.data.repository.VentasRepository.SaleDraftItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VentasViewModel(
    private val fardosRepository: FardosRepository,
    private val ventasRepository: VentasRepository,
    private val accessToken: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VentasUiState())
    val uiState: StateFlow<VentasUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val productsResult = runCatching { fardosRepository.list(accessToken) }
            val salesResult = runCatching { ventasRepository.recent(accessToken) }

            productsResult.onSuccess { products ->
                salesResult.onSuccess { sales ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            products = products,
                            recentSales = sales,
                        )
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "No se pudo cargar las ventas recientes.",
                            products = products,
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "No se pudo cargar el inventario para ventas.",
                    )
                }
            }
        }
    }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun onClienteChange(value: String) {
        _uiState.update { it.copy(clienteNombre = value, error = null) }
    }

    fun onPagoRecibidoChange(value: String) {
        _uiState.update { it.copy(pagoRecibido = value.filterPrice(), error = null) }
    }

    fun onEstadoPagoChange(value: String) {
        _uiState.update { it.copy(estadoPago = value, error = null) }
    }

    fun addProduct(item: Fardo) {
        _uiState.update { state ->
            val current = state.cart.firstOrNull { it.productoId == item.id }
            val updatedCart = if (current == null) {
                state.cart + VentaCartItem(
                    productoId = item.id,
                    nombre = item.nombre,
                    stockDisponible = item.stock,
                    cantidad = 1,
                    precioUnitario = item.precio,
                )
            } else {
                state.cart.map {
                    if (it.productoId == item.id) {
                        it.copy(cantidad = (it.cantidad + 1).coerceAtMost(item.stock))
                    } else {
                        it
                    }
                }
            }
            state.copy(cart = updatedCart, error = null)
        }
    }

    fun increaseItem(productId: String) {
        _uiState.update { state ->
            val updatedCart = state.cart.map { item ->
                if (item.productoId == productId) {
                    item.copy(cantidad = (item.cantidad + 1).coerceAtMost(item.stockDisponible))
                } else {
                    item
                }
            }
            state.copy(cart = updatedCart)
        }
    }

    fun decreaseItem(productId: String) {
        _uiState.update { state ->
            val updatedCart = state.cart.mapNotNull { item ->
                if (item.productoId == productId) {
                    val newQuantity = item.cantidad - 1
                    if (newQuantity <= 0) null else item.copy(cantidad = newQuantity)
                } else {
                    item
                }
            }
            state.copy(cart = updatedCart)
        }
    }

    fun removeItem(productId: String) {
        _uiState.update { state ->
            state.copy(cart = state.cart.filterNot { it.productoId == productId })
        }
    }

    fun clearCart() {
        _uiState.update {
            it.copy(
                cart = emptyList(),
                clienteNombre = "",
                pagoRecibido = "",
                estadoPago = "pendiente",
                error = null,
            )
        }
    }

    fun submitSale() {
        val state = _uiState.value
        if (state.cart.isEmpty()) {
            _uiState.update { it.copy(error = "Agrega al menos un producto.") }
            return
        }

        val pagoRecibido = state.pagoRecibido.toDoubleOrNull() ?: 0.0
        val total = state.cart.sumOf { it.subtotal }
        if (pagoRecibido < 0) {
            _uiState.update { it.copy(error = "El pago recibido no es valido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            runCatching {
                ventasRepository.create(
                    accessToken = accessToken,
                    clienteNombre = state.clienteNombre,
                    estadoPago = state.estadoPago,
                    pagoRecibido = pagoRecibido,
                    items = state.cart.map {
                        SaleDraftItem(
                            productoId = it.productoId,
                            nombre = it.nombre,
                            cantidad = it.cantidad,
                            precioUnitario = it.precioUnitario,
                        )
                    },
                )
            }.onSuccess {
                val remainingById = state.cart.associate { it.productoId to it.cantidad }
                state.products.forEach { product ->
                    val soldQty = remainingById[product.id] ?: return@forEach
                    fardosRepository.updateStock(
                        accessToken = accessToken,
                        id = product.id,
                        stock = (product.stock - soldQty).coerceAtLeast(0),
                    )
                }
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        cart = emptyList(),
                        clienteNombre = "",
                        pagoRecibido = "",
                        estadoPago = "pendiente",
                        error = null,
                    )
                }
                refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = error.message ?: "No se pudo registrar la venta.",
                    )
                }
            }
        }
    }

    private fun String.filterPrice(): String {
        val builder = StringBuilder()
        var decimalUsed = false
        for (char in this) {
            when {
                char.isDigit() -> builder.append(char)
                (char == '.' || char == ',') && !decimalUsed -> {
                    builder.append('.')
                    decimalUsed = true
                }
            }
        }
        return builder.toString()
    }

    companion object {
        fun factory(
            fardosRepository: FardosRepository,
            ventasRepository: VentasRepository,
            accessToken: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return VentasViewModel(fardosRepository, ventasRepository, accessToken) as T
            }
        }
    }
}
