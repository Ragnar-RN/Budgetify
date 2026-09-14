package com.example.expensetracker.data.local.dao

import androidx.room.ColumnInfo

data class CategorySpendTotal(
    @ColumnInfo(name = "category_id") val categoryId: Long,
    val total: Double
)

data class TrendBucketTotal(
    val bucket: String,
    val income: Double,
    val expense: Double
)
