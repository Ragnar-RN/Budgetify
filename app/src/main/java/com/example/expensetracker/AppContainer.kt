package com.example.expensetracker

import android.content.Context
import com.example.expensetracker.data.local.AppDatabase
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.SmsSenderPatternRepository
import com.example.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class AppContainer(context: Context) {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database: AppDatabase = AppDatabase.getInstance(context, applicationScope)

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(database.categoryDao())
    }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(database.transactionDao())
    }

    val smsSenderPatternRepository: SmsSenderPatternRepository by lazy {
        SmsSenderPatternRepository(database.smsSenderPatternDao())
    }
}
