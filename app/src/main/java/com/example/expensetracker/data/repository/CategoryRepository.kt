package com.example.expensetracker.data.repository

import com.example.expensetracker.data.local.dao.CategoryDao
import com.example.expensetracker.data.local.entity.Category
import com.example.expensetracker.data.local.entity.CategoryType
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAll(): Flow<List<Category>> = categoryDao.getAll()

    fun getByType(type: CategoryType): Flow<List<Category>> = categoryDao.getByType(type)

    suspend fun getById(id: Long): Category? = categoryDao.getById(id)

    suspend fun insert(category: Category): Long = categoryDao.insert(category)

    suspend fun update(category: Category) = categoryDao.update(category)

    suspend fun delete(category: Category) = categoryDao.delete(category)
}
