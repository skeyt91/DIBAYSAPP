package com.example.dibays.ui.screens.inventario

import com.example.dibays.data.model.Fardo

data class InventarioUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val query: String = "",
    val items: List<Fardo> = emptyList(),
    val showForm: Boolean = false,
    val editingId: String? = null,
    val nombre: String = "",
    val codigo: String = "",
    val categoria: String = "",
    val stock: String = "",
    val precio: String = "",
    val costo: String = "",
)
