package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.SmsSenderPatternDao
import com.example.expensetracker.data.local.entity.SmsSenderPattern
import kotlinx.coroutines.flow.Flow

class SmsSenderPatternRepository(private val dao: SmsSenderPatternDao) {

    fun getAll(): Flow<List<SmsSenderPattern>> = dao.getAll()

    suspend fun getActiveOnce(): List<SmsSenderPattern> = dao.getActiveOnce()

    suspend fun insert(pattern: SmsSenderPattern): Long = dao.insert(pattern)

    suspend fun update(pattern: SmsSenderPattern) = dao.update(pattern)

    suspend fun delete(pattern: SmsSenderPattern) = dao.delete(pattern)
}
