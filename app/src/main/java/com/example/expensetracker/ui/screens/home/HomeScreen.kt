package com.example.expensetracker.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.components.TransactionRow
import com.example.expensetracker.ui.util.formatCurrency

private val IncomeColor = Color(0xFF2E7D32)
private val ExpenseColor = Color(0xFFC62828)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onTransactionClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Home") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BalanceCard(balance = uiState.balance)
            }
            item {
                MonthSummaryCard(income = uiState.monthIncome, expense = uiState.monthExpense)
            }
            item {
                Text(text = "Recent Transactions", style = MaterialTheme.typography.titleMedium)
            }
            if (uiState.recentTransactions.isEmpty()) {
                item {
                    Text(
                        text = "No transactions yet",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.recentTransactions, key = { it.transaction.id }) { item ->
                    TransactionRow(
                        transaction = item.transaction,
                        category = item.category,
                        onClick = { onTransactionClick(item.transaction.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceCard(balance: Double) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Balance", style = MaterialTheme.typography.titleMedium)
            Text(
                text = formatCurrency(balance),
                style = MaterialTheme.typography.headlineMedium,
                color = if (balance >= 0) IncomeColor else ExpenseColor
            )
        }
    }
}

@Composable
private fun MonthSummaryCard(income: Double, expense: Double) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "This Month Income", style = MaterialTheme.typography.bodyMedium)
                Text(text = formatCurrency(income), color = IncomeColor, style = MaterialTheme.typography.titleLarge)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "This Month Expense", style = MaterialTheme.typography.bodyMedium)
                Text(text = formatCurrency(expense), color = ExpenseColor, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
