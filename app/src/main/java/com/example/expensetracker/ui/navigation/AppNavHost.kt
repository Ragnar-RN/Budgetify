package com.example.expensetracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.ui.ViewModelFactory
import com.example.expensetracker.ui.screens.addtransaction.AddTransactionScreen
import com.example.expensetracker.ui.screens.addtransaction.AddTransactionViewModel
import com.example.expensetracker.ui.screens.home.HomeScreen
import com.example.expensetracker.ui.screens.home.HomeViewModel
import com.example.expensetracker.ui.screens.settings.SettingsScreen
import com.example.expensetracker.ui.screens.settings.SettingsViewModel
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
                HomeScreen(viewModel = viewModel)
            }
            composable(Screen.AddTransaction.route) {
                val viewModel: AddTransactionViewModel = viewModel(factory = viewModelFactory)
                AddTransactionScreen(viewModel = viewModel)
            }
            composable(Screen.TransactionsList.route) {
                val viewModel: TransactionsListViewModel = viewModel(factory = viewModelFactory)
                TransactionsListScreen(viewModel = viewModel)
            }
            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
                SettingsScreen(viewModel = viewModel)
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
