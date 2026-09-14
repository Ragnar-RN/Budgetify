package com.example.expensetracker.ui.screens.transactionslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.entity.Category
import com.example.expensetracker.data.local.entity.TransactionType
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.ui.model.TransactionWithCategory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionFilter(
    val type: TransactionType? = null,
    val categoryId: Long? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val keyword: String = ""
) {
    val isActive: Boolean
        get() = type != null || categoryId != null || startDate != null || endDate != null || keyword.isNotBlank()

    val keywordPattern: String?
        get() = keyword.trim().takeIf { it.isNotEmpty() }?.let { "%$it%" }
}

data class TransactionsListUiState(
    val items: List<TransactionWithCategory> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filter: TransactionFilter = TransactionFilter(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsListViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val filterState = MutableStateFlow(TransactionFilter())

    private val _uiState = MutableStateFlow(TransactionsListUiState())
    val uiState: StateFlow<TransactionsListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                filterState.flatMapLatest { filter ->
                    transactionRepository.getFiltered(
                        type = filter.type?.name,
                        categoryId = filter.categoryId,
                        startInclusive = filter.startDate,
                        endInclusive = filter.endDate,
                        keywordPattern = filter.keywordPattern
                    )
                },
                categoryRepository.getAll(),
                filterState
            ) { transactions, categories, filter ->
                val categoryMap = categories.associateBy { it.id }
                TransactionsListUiState(
                    items = transactions.map { TransactionWithCategory(it, categoryMap[it.categoryId]) },
                    categories = categories,
                    filter = filter,
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onTypeFilterSelected(type: TransactionType) {
        filterState.update { current -> current.copy(type = if (current.type == type) null else type) }
    }

    fun onCategoryFilterSelected(categoryId: Long?) {
        filterState.update { it.copy(categoryId = categoryId) }
    }

    fun onStartDateSelected(millis: Long) {
        filterState.update { it.copy(startDate = millis) }
    }

    fun onEndDateSelected(millis: Long) {
        filterState.update { it.copy(endDate = millis) }
    }

    fun onKeywordChanged(keyword: String) {
        filterState.update { it.copy(keyword = keyword) }
    }

    fun clearFilters() {
        filterState.value = TransactionFilter()
    }
}
