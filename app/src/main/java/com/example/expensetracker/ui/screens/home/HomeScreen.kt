package com.example.expensetracker.ui.screens.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Text(text = "Home", modifier = Modifier.padding(innerPadding))
    }
}
