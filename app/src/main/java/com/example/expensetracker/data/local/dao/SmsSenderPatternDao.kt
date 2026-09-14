package com.example.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.local.entity.SmsSenderPattern
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsSenderPatternDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(pattern: SmsSenderPattern): Long

    @Update
    suspend fun update(pattern: SmsSenderPattern)

    @Delete
    suspend fun delete(pattern: SmsSenderPattern)

    @Query("SELECT * FROM sms_sender_patterns ORDER BY label ASC")
    fun getAll(): Flow<List<SmsSenderPattern>>

    @Query("SELECT * FROM sms_sender_patterns WHERE is_active = 1")
    suspend fun getActiveOnce(): List<SmsSenderPattern>
}
