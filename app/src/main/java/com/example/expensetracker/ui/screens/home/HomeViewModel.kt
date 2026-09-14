package com.example.expensetracker.ui.screens.home

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
import java.time.YearMonth
import java.time.ZoneId

data class HomeUiState(
    val balance: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val recentTransactions: List<TransactionWithCategory> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val zoneId = ZoneId.systemDefault()
        val yearMonth = YearMonth.now()
        val monthStart = yearMonth.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val monthEnd = yearMonth.atEndOfMonth().atTime(23, 59, 59).atZone(zoneId).toInstant().toEpochMilli()

        viewModelScope.launch {
            combine(
                transactionRepository.getBalance(),
                transactionRepository.getIncomeTotal(monthStart, monthEnd),
                transactionRepository.getExpenseTotal(monthStart, monthEnd),
                transactionRepository.getRecent(5),
                categoryRepository.getAll()
            ) { balance, income, expense, recent, categories ->
                val categoryMap = categories.associateBy { it.id }
                HomeUiState(
                    balance = balance,
                    monthIncome = income,
                    monthExpense = expense,
                    recentTransactions = recent.map { TransactionWithCategory(it, categoryMap[it.categoryId]) },
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }
}
