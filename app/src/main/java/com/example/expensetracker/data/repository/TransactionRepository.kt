package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.CategorySpendTotal
import com.example.expensetracker.data.local.dao.TransactionDao
import com.example.expensetracker.data.local.dao.TrendBucketTotal
import com.example.expensetracker.data.local.entity.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getAll(): Flow<List<Transaction>> = transactionDao.getAll()

    fun getByCategory(categoryId: Long): Flow<List<Transaction>> = transactionDao.getByCategory(categoryId)

    fun getByDateRange(startInclusive: Long, endInclusive: Long): Flow<List<Transaction>> =
        transactionDao.getByDateRange(startInclusive, endInclusive)

    fun getRecent(limit: Int): Flow<List<Transaction>> = transactionDao.getRecent(limit)

    fun getBalance(): Flow<Double> = transactionDao.getBalance()

    fun getIncomeTotal(startInclusive: Long, endInclusive: Long): Flow<Double> =
        transactionDao.getIncomeTotal(startInclusive, endInclusive)

    fun getExpenseTotal(startInclusive: Long, endInclusive: Long): Flow<Double> =
        transactionDao.getExpenseTotal(startInclusive, endInclusive)

    suspend fun countByCategory(categoryId: Long): Int = transactionDao.countByCategory(categoryId)

    fun getCategoryExpenseTotals(startInclusive: Long, endInclusive: Long): Flow<List<CategorySpendTotal>> =
        transactionDao.getCategoryExpenseTotals(startInclusive, endInclusive)

    fun getTrendTotals(startInclusive: Long, endInclusive: Long, bucketFormat: String): Flow<List<TrendBucketTotal>> =
        transactionDao.getTrendTotals(startInclusive, endInclusive, bucketFormat)

    fun getFiltered(
        type: String?,
        categoryId: Long?,
        startInclusive: Long?,
        endInclusive: Long?,
        keywordPattern: String?
    ): Flow<List<Transaction>> =
        transactionDao.getFiltered(type, categoryId, startInclusive, endInclusive, keywordPattern)

    suspend fun getById(id: Long): Transaction? = transactionDao.getById(id)

    suspend fun insert(transaction: Transaction): Long = transactionDao.insert(transaction)

    suspend fun update(transaction: Transaction) = transactionDao.update(transaction)

    suspend fun delete(transaction: Transaction) = transactionDao.delete(transaction)
}
