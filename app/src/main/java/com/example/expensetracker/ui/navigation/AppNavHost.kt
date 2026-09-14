package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.expensetracker.ui.ViewModelFactory
import com.example.expensetracker.ui.screens.addtransaction.AddTransactionScreen
import com.example.expensetracker.ui.screens.addtransaction.AddTransactionViewModel
import com.example.expensetracker.ui.screens.categorymanagement.CategoryManagementScreen
import com.example.expensetracker.ui.screens.categorymanagement.CategoryManagementViewModel
import com.example.expensetracker.ui.screens.home.HomeScreen
import com.example.expensetracker.ui.screens.home.HomeViewModel
import com.example.expensetracker.ui.screens.reports.ReportsScreen
import com.example.expensetracker.ui.screens.reports.ReportsViewModel
import com.example.expensetracker.ui.screens.settings.SettingsScreen
import com.example.expensetracker.ui.screens.settings.SettingsViewModel
import com.example.expensetracker.ui.screens.smsrationale.SmsRationaleScreen
import com.example.expensetracker.ui.screens.smssenders.SmsSenderManagementScreen
import com.example.expensetracker.ui.screens.smssenders.SmsSenderManagementViewModel
import com.example.expensetracker.ui.screens.transactionslist.TransactionsListScreen
import com.example.expensetracker.ui.screens.transactionslist.TransactionsListViewModel

@Composable
fun AppNavHost(viewModelFactory: ViewModelFactory) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { AppBottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
                HomeScreen(
                    viewModel = viewModel,
                    onTransactionClick = { id -> navController.navigate(EditTransactionRoute.createRoute(id)) }
                )
            }
            composable(Screen.AddTransaction.route) {
                val viewModel: AddTransactionViewModel = viewModel(factory = viewModelFactory)
                AddTransactionScreen(viewModel = viewModel)
            }
            composable(Screen.TransactionsList.route) {
                val viewModel: TransactionsListViewModel = viewModel(factory = viewModelFactory)
                TransactionsListScreen(
                    viewModel = viewModel,
                    onTransactionClick = { id -> navController.navigate(EditTransactionRoute.createRoute(id)) }
                )
            }
            composable(Screen.Reports.route) {
                val viewModel: ReportsViewModel = viewModel(factory = viewModelFactory)
                ReportsScreen(viewModel = viewModel)
            }
            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateToCategoryManagement = { navController.navigate(CategoryManagementRoute.route) },
                    onNavigateToSmsRationale = { navController.navigate(SmsRationaleRoute.route) },
                    onNavigateToSmsSenderManagement = { navController.navigate(SmsSenderManagementRoute.route) }
                )
            }
            composable(
                route = EditTransactionRoute.route,
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: return@composable
                val viewModel: AddTransactionViewModel = viewModel(factory = viewModelFactory)
                LaunchedEffect(transactionId) { viewModel.loadTransaction(transactionId) }
                AddTransactionScreen(
                    viewModel = viewModel,
                    onSaved = { navController.popBackStack() },
                    onDeleted = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(CategoryManagementRoute.route) {
                val viewModel: CategoryManagementViewModel = viewModel(factory = viewModelFactory)
                CategoryManagementScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(SmsRationaleRoute.route) {
                SmsRationaleScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToSenderManagement = { navController.navigate(SmsSenderManagementRoute.route) }
                )
            }
            composable(SmsSenderManagementRoute.route) {
                val viewModel: SmsSenderManagementViewModel = viewModel(factory = viewModelFactory)
                SmsSenderManagementScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun AppBottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        Screen.bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) }
            )
        }
    }
}
