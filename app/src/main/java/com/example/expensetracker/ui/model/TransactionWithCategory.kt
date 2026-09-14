package com.example.expensetracker.ui.model

import com.example.expensetracker.data.local.entity.Category
import com.example.expensetracker.data.local.entity.Transaction

data class TransactionWithCategory(
    val transaction: Transaction,
    val category: Category?
)
