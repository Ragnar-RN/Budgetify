package com.example.expensetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object AddTransaction : Screen("add_transaction", "Add", Icons.Filled.Add)
    object TransactionsList : Screen("transactions_list", "Transactions", Icons.AutoMirrored.Filled.List)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)

    companion object {
        val bottomNavItems = listOf(Home, AddTransaction, TransactionsList, Settings)
    }
}

object EditTransactionRoute {
    private const val ARG_TRANSACTION_ID = "transactionId"
    const val route = "edit_transaction/{$ARG_TRANSACTION_ID}"

    fun createRoute(transactionId: Long) = "edit_transaction/$transactionId"
}

object CategoryManagementRoute {
    const val route = "category_management"
}
