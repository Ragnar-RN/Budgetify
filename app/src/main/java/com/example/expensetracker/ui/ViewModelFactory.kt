package com.example.expensetracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.SmsSenderPatternRepository
import com.example.expensetracker.data.repository.TransactionRepository
import com.example.expensetracker.ui.screens.addtransaction.AddTransactionViewModel
import com.example.expensetracker.ui.screens.categorymanagement.CategoryManagementViewModel
import com.example.expensetracker.ui.screens.home.HomeViewModel
import com.example.expensetracker.ui.screens.reports.ReportsViewModel
import com.example.expensetracker.ui.screens.settings.SettingsViewModel
import com.example.expensetracker.ui.screens.smssenders.SmsSenderManagementViewModel
import com.example.expensetracker.ui.screens.transactionslist.TransactionsListViewModel

class ViewModelFactory(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val smsSenderPatternRepository: SmsSenderPatternRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            HomeViewModel::class.java ->
                HomeViewModel(transactionRepository, categoryRepository) as T
            AddTransactionViewModel::class.java ->
                AddTransactionViewModel(transactionRepository, categoryRepository) as T
            TransactionsListViewModel::class.java ->
                TransactionsListViewModel(transactionRepository, categoryRepository) as T
            SettingsViewModel::class.java ->
                SettingsViewModel(categoryRepository) as T
            CategoryManagementViewModel::class.java ->
                CategoryManagementViewModel(categoryRepository, transactionRepository) as T
            ReportsViewModel::class.java ->
                ReportsViewModel(transactionRepository, categoryRepository) as T
            SmsSenderManagementViewModel::class.java ->
                SmsSenderManagementViewModel(smsSenderPatternRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
