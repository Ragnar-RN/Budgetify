package com.example.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.expensetracker.data.local.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction?

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE category_id = :categoryId ORDER BY date DESC")
    fun getByCategory(categoryId: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startInclusive AND :endInclusive ORDER BY date DESC")
    fun getByDateRange(startInclusive: Long, endInclusive: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<Transaction>>

    @Query("SELECT COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE -amount END), 0.0) FROM transactions")
    fun getBalance(): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :startInclusive AND :endInclusive")
    fun getIncomeTotal(startInclusive: Long, endInclusive: Long): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :startInclusive AND :endInclusive")
    fun getExpenseTotal(startInclusive: Long, endInclusive: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM transactions WHERE category_id = :categoryId")
    suspend fun countByCategory(categoryId: Long): Int

    @Query(
        """
        SELECT category_id, SUM(amount) AS total
        FROM transactions
        WHERE type = 'EXPENSE' AND date BETWEEN :startInclusive AND :endInclusive
        GROUP BY category_id
        ORDER BY total DESC
        """
    )
    fun getCategoryExpenseTotals(
        startInclusive: Long,
        endInclusive: Long
    ): Flow<List<CategorySpendTotal>>

    @Query(
        """
        SELECT strftime(:bucketFormat, date / 1000, 'unixepoch') AS bucket,
               SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) AS income,
               SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) AS expense
        FROM transactions
        WHERE date BETWEEN :startInclusive AND :endInclusive
        GROUP BY bucket
        ORDER BY bucket ASC
        """
    )
    fun getTrendTotals(
        startInclusive: Long,
        endInclusive: Long,
        bucketFormat: String
    ): Flow<List<TrendBucketTotal>>

    @Query(
        """
        SELECT * FROM transactions
        WHERE (:type IS NULL OR type = :type)
          AND (:categoryId IS NULL OR category_id = :categoryId)
          AND (:startInclusive IS NULL OR date >= :startInclusive)
          AND (:endInclusive IS NULL OR date <= :endInclusive)
          AND (:keywordPattern IS NULL OR note LIKE :keywordPattern)
        ORDER BY date DESC
        """
    )
    fun getFiltered(
        type: String?,
        categoryId: Long?,
        startInclusive: Long?,
        endInclusive: Long?,
        keywordPattern: String?
    ): Flow<List<Transaction>>
}
