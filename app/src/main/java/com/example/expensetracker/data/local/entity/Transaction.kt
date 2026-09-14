package com.example.expensetracker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType {
    EXPENSE,
    INCOME
}

enum class TransactionSource {
    MANUAL,
    SMS_AUTO
}

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("category_id")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    val date: Long,
    val note: String? = null,
    val source: TransactionSource = TransactionSource.MANUAL,
    @ColumnInfo(name = "payment_method")
    val paymentMethod: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
