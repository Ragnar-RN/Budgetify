package com.example.expensetracker.ui.screens.transactionslist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TransactionsListScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionsListViewModel
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Text(text = "Transactions", modifier = Modifier.padding(innerPadding))
    }
}
