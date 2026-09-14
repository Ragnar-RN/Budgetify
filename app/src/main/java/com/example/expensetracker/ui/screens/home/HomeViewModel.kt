package com.example.expensetracker.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.TransactionRepository

class HomeViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel()
