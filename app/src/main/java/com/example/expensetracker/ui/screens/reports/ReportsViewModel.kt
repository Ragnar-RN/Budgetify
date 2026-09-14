package com.example.expensetracker.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.entity.Category
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryBreakdownItem(
    val category: Category?,
    val total: Double,
    val fraction: Float
)

data class TrendPoint(
    val label: String,
    val income: Double,
    val expense: Double
)

data class ReportsUiState(
    val period: ReportPeriod = ReportPeriod(),
    val categoryBreakdown: List<CategoryBreakdownItem> = emptyList(),
    val totalSpend: Double = 0.0,
    val trendPoints: List<TrendPoint> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class ReportsViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val periodState = MutableStateFlow(ReportPeriod())

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            periodState
                .flatMapLatest { period ->
                    val (start, end) = period.range()
                    val bucketFormat = period.bucketSqlFormat()
                    combine(
                        transactionRepository.getCategoryExpenseTotals(start, end),
                        transactionRepository.getTrendTotals(start, end, bucketFormat),
                        categoryRepository.getAll()
                    ) { categoryTotals, trendTotals, categories ->
                        val categoryMap = categories.associateBy { it.id }
                        val totalSpend = categoryTotals.sumOf { it.total }
                        val breakdown = categoryTotals.map { entry ->
                            CategoryBreakdownItem(
                                category = categoryMap[entry.categoryId],
                                total = entry.total,
                                fraction = if (totalSpend > 0) (entry.total / totalSpend).toFloat() else 0f
                            )
                        }
                        val trend = trendTotals.map { entry ->
                            TrendPoint(
                                label = formatBucketLabel(entry.bucket, bucketFormat),
                                income = entry.income,
                                expense = entry.expense
                            )
                        }
                        ReportsUiState(
                            period = period,
                            categoryBreakdown = breakdown,
                            totalSpend = totalSpend,
                            trendPoints = trend,
                            isLoading = false
                        )
                    }
                }
                .collect { _uiState.value = it }
        }
    }

    fun onPeriodTypeSelected(type: ReportPeriodType) {
        periodState.update { it.copy(type = type) }
    }

    fun onCustomStartSelected(millis: Long) {
        periodState.update { it.copy(type = ReportPeriodType.CUSTOM, customStart = millis) }
    }

    fun onCustomEndSelected(millis: Long) {
        periodState.update { it.copy(type = ReportPeriodType.CUSTOM, customEnd = millis) }
    }
}
