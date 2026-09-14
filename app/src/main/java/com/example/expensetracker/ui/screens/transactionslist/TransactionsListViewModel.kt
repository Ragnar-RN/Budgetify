package com.example.expensetracker.ui.screens.transactionslist

import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.TransactionRepository

class TransactionsListViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel()
