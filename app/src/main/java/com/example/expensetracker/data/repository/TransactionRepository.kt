package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.TransactionDao
import com.example.expensetracker.data.local.entity.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getAll(): Flow<List<Transaction>> = transactionDao.getAll()

    fun getByCategory(categoryId: Long): Flow<List<Transaction>> = transactionDao.getByCategory(categoryId)

    fun getByDateRange(startInclusive: Long, endInclusive: Long): Flow<List<Transaction>> =
        transactionDao.getByDateRange(startInclusive, endInclusive)

    suspend fun getById(id: Long): Transaction? = transactionDao.getById(id)

    suspend fun insert(transaction: Transaction): Long = transactionDao.insert(transaction)

    suspend fun update(transaction: Transaction) = transactionDao.update(transaction)

    suspend fun delete(transaction: Transaction) = transactionDao.delete(transaction)
}
