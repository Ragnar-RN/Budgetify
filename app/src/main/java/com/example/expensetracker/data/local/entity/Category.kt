package com.example.expensetracker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CategoryType {
    EXPENSE,
    INCOME,
    BOTH
}

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: CategoryType,
    val icon: String,
    val color: Int,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false
)
