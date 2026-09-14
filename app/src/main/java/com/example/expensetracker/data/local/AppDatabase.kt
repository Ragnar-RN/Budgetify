package com.example.expensetracker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.expensetracker.data.local.dao.CategoryDao
import com.example.expensetracker.data.local.dao.TransactionDao
import com.example.expensetracker.data.local.entity.Category
import com.example.expensetracker.data.local.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Category::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    private class SeedCallback(private val scope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    seedDefaultCategories(database.categoryDao())
                }
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "expense_tracker.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .addCallback(SeedCallback(scope))
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
