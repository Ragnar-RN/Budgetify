package com.example.expensetracker.ui.screens.transactionslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.ui.model.TransactionWithCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class TransactionsListUiState(
    val items: List<TransactionWithCategory> = emptyList(),
    val isLoading: Boolean = true
)

class TransactionsListViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionsListUiState())
    val uiState: StateFlow<TransactionsListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                transactionRepository.getAll(),
                categoryRepository.getAll()
            ) { transactions, categories ->
                val categoryMap = categories.associateBy { it.id }
                TransactionsListUiState(
                    items = transactions.map { TransactionWithCategory(it, categoryMap[it.categoryId]) },
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }
}
