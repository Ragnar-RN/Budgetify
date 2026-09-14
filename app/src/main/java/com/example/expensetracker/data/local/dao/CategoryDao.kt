package com.example.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.local.entity.Category
import com.example.expensetracker.data.local.entity.CategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(category: Category): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(categories: List<Category>)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): Category?

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAll(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE type = :type OR type = 'BOTH' ORDER BY name ASC")
    fun getByType(type: CategoryType): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}
