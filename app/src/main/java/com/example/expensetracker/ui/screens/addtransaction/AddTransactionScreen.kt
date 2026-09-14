package com.example.expensetracker.ui.screens.addtransaction

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddTransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: AddTransactionViewModel
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Text(text = "Add Transaction", modifier = Modifier.padding(innerPadding))
    }
}
