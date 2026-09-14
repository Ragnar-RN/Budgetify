package com.example.expensetracker.ui.screens.transactionslist

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.local.entity.Category
import com.example.expensetracker.data.local.entity.TransactionType
import com.example.expensetracker.ui.components.TransactionRow
import com.example.expensetracker.ui.util.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsListScreen(
    viewModel: TransactionsListViewModel,
    onTransactionClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Transactions") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            FilterBar(
                filter = uiState.filter,
                categories = uiState.categories,
                onKeywordChanged = viewModel::onKeywordChanged,
                onTypeSelected = viewModel::onTypeFilterSelected,
                onCategorySelected = viewModel::onCategoryFilterSelected,
                onStartDateSelected = viewModel::onStartDateSelected,
                onEndDateSelected = viewModel::onEndDateSelected,
                onClear = viewModel::clearFilters
            )
            if (uiState.items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = if (uiState.filter.isActive) "No transactions match your filters" else "No transactions yet",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.items, key = { it.transaction.id }) { item ->
                        TransactionRow(
                            transaction = item.transaction,
                            category = item.category,
                            onClick = { onTransactionClick(item.transaction.id) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterBar(
    filter: TransactionFilter,
    categories: List<Category>,
    onKeywordChanged: (String) -> Unit,
    onTypeSelected: (TransactionType) -> Unit,
    onCategorySelected: (Long?) -> Unit,
    onStartDateSelected: (Long) -> Unit,
    onEndDateSelected: (Long) -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = filter.keyword,
            onValueChange = onKeywordChanged,
            placeholder = { Text("Search notes") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filter.type == TransactionType.EXPENSE,
                onClick = { onTypeSelected(TransactionType.EXPENSE) },
                label = { Text("Expense") }
            )
            FilterChip(
                selected = filter.type == TransactionType.INCOME,
                onClick = { onTypeSelected(TransactionType.INCOME) },
                label = { Text("Income") }
            )
            CategoryFilterChip(
                categories = categories,
                selectedCategoryId = filter.categoryId,
                onSelected = onCategorySelected
            )
            DateFilterChip(label = "From", dateMillis = filter.startDate, onDateSelected = onStartDateSelected)
            DateFilterChip(label = "To", dateMillis = filter.endDate, onDateSelected = onEndDateSelected)
            if (filter.isActive) {
                AssistChip(onClick = onClear, label = { Text("Clear") })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryFilterChip(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = categories.firstOrNull { it.id == selectedCategoryId }?.name ?: "Category"

    Box {
        FilterChip(
            selected = selectedCategoryId != null,
            onClick = { expanded = true },
            label = { Text(selectedName) }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("All Categories") },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onSelected(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateFilterChip(
    label: String,
    dateMillis: Long?,
    onDateSelected: (Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    FilterChip(
        selected = dateMillis != null,
        onClick = { showDialog = true },
        label = { Text(if (dateMillis != null) formatDate(dateMillis) else label) }
    )

    if (showDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let(onDateSelected)
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
