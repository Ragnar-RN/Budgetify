package com.example.expensetracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** A simple bar-chart glyph, hand-drawn to avoid pulling in material-icons-extended for one icon. */
private val BarChartIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "BarChart",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 9f)
            lineTo(8f, 9f)
            lineTo(8f, 20f)
            lineTo(5f, 20f)
            close()
            moveTo(10.5f, 4f)
            lineTo(13.5f, 4f)
            lineTo(13.5f, 20f)
            lineTo(10.5f, 20f)
            close()
            moveTo(16f, 13f)
            lineTo(19f, 13f)
            lineTo(19f, 20f)
            lineTo(16f, 20f)
            close()
        }
    }.build()
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object AddTransaction : Screen("add_transaction", "Add", Icons.Filled.Add)
    object TransactionsList : Screen("transactions_list", "Transactions", Icons.AutoMirrored.Filled.List)
    object Reports : Screen("reports", "Reports", BarChartIcon)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)

    companion object {
        val bottomNavItems = listOf(Home, AddTransaction, TransactionsList, Reports, Settings)
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

object SmsRationaleRoute {
    const val route = "sms_rationale"
}

object SmsSenderManagementRoute {
    const val route = "sms_sender_management"
}
