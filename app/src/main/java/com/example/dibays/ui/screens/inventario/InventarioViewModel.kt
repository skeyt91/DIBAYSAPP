package com.example.dibays.ui.screens.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dibays.data.model.Fardo
import com.example.dibays.data.repository.FardosRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InventarioViewModel(
    private val repository: FardosRepository,
    private val accessToken: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(InventarioUiState())
    val uiState: StateFlow<InventarioUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                repository.list(accessToken)
            }.onSuccess { items ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        items = items,
                        error = null,
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "No se pudo cargar el inventario.",
                    )
                }
            }
        }
    }

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value) }
    }

    fun openCreateForm() {
        _uiState.update {
            it.copy(
                showForm = true,
                editingId = null,
                error = null,
                nombre = "",
                codigo = "",
                categoria = "",
                stock = "0",
                precio = "",
                costo = "",
            )
        }
    }

    fun editItem(item: Fardo) {
        _uiState.update {
            it.copy(
                showForm = true,
                editingId = item.id,
                error = null,
                nombre = item.nombre,
                codigo = item.codigo,
                categoria = item.categoria,
                stock = item.stock.toString(),
                precio = item.precio.formatInput(),
                costo = item.costo.formatInput(),
            )
        }
    }

    fun closeForm() {
        _uiState.update { it.copy(showForm = false, error = null) }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, error = null) }
    }

    fun onCodigoChange(value: String) {
        _uiState.update { it.copy(codigo = value, error = null) }
    }

    fun onCategoriaChange(value: String) {
        _uiState.update { it.copy(categoria = value, error = null) }
    }

    fun onStockChange(value: String) {
        _uiState.update { it.copy(stock = value.filter { ch -> ch.isDigit() || ch == '-' }, error = null) }
    }

    fun onPrecioChange(value: String) {
        _uiState.update { it.copy(precio = value.filterPrice(), error = null) }
    }

    fun onCostoChange(value: String) {
        _uiState.update { it.copy(costo = value.filterPrice(), error = null) }
    }

    fun save() {
        val state = _uiState.value
        val nombre = state.nombre.trim()
        val codigo = state.codigo.trim()
        val categoria = state.categoria.trim()
        val stock = state.stock.toIntOrNull()
        val precio = state.precio.toDoubleOrNull()
        val costo = state.costo.toDoubleOrNull()

        when {
            nombre.isBlank() -> {
                _uiState.update { it.copy(error = "Ingresa el nombre del fardo.") }
                return
            }
            stock == null || stock < 0 -> {
                _uiState.update { it.copy(error = "Ingresa un stock valido.") }
                return
            }
            precio == null || precio < 0 -> {
                _uiState.update { it.copy(error = "Ingresa un precio valido.") }
                return
            }
            costo == null || costo < 0 -> {
                _uiState.update { it.copy(error = "Ingresa un costo valido.") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            runCatching {
                if (state.editingId == null) {
                    repository.create(
                        accessToken = accessToken,
                        nombre = nombre,
                        codigo = codigo,
                        categoria = categoria,
                        stock = stock ?: 0,
                        precio = precio ?: 0.0,
                        costo = costo ?: 0.0,
                    )
                } else {
                    repository.update(
                        accessToken = accessToken,
                        id = state.editingId,
                        nombre = nombre,
                        codigo = codigo,
                        categoria = categoria,
                        stock = stock ?: 0,
                        precio = precio ?: 0.0,
                        costo = costo ?: 0.0,
                    )
                }
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        showForm = false,
                        editingId = null,
                        error = null,
                    )
                }
                refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = error.message ?: "No se pudo guardar el fardo.",
                    )
                }
            }
        }
    }

    fun increaseStock(item: Fardo) {
        updateStock(item, item.stock + 1)
    }

    fun decreaseStock(item: Fardo) {
        if (item.stock <= 0) return
        updateStock(item, item.stock - 1)
    }

    private fun updateStock(item: Fardo, newStock: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            runCatching {
                repository.updateStock(accessToken, item.id, newStock)
            }.onSuccess {
                refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(error = error.message ?: "No se pudo actualizar el stock.")
                }
            }
        }
    }

    fun deleteItem(item: Fardo) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            runCatching {
                repository.delete(accessToken, item.id)
            }.onSuccess {
                refresh()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(error = error.message ?: "No se pudo eliminar el fardo.")
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

    private fun Double.formatInput(): String {
        return if (this % 1.0 == 0.0) {
            toLong().toString()
        } else {
            String.format("%.2f", this)
        }
    }

    companion object {
        fun factory(
            repository: FardosRepository,
            accessToken: String,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return InventarioViewModel(repository, accessToken) as T
            }
        }
    }
}
