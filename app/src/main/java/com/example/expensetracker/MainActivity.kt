package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.expensetracker.ui.ViewModelFactory
import com.example.expensetracker.ui.navigation.AppNavHost
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as ExpenseTrackerApplication).container
        val viewModelFactory = ViewModelFactory(
            categoryRepository = container.categoryRepository,
            transactionRepository = container.transactionRepository
        )

        setContent {
            ExpenseTrackerTheme {
                AppNavHost(viewModelFactory = viewModelFactory)
            }
        }
    }
}