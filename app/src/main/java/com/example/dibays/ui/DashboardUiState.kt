package com.example.dibays.ui

import com.example.dibays.data.dashboard.ProductSummary

data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val products: List<ProductSummary> = emptyList(),
    val searchQuery: String = "",
)
