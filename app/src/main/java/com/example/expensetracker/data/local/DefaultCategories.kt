package com.example.expensetracker.data.local

import android.graphics.Color
import com.example.expensetracker.data.local.dao.CategoryDao
import com.example.expensetracker.data.local.entity.Category
import com.example.expensetracker.data.local.entity.CategoryType

val defaultCategories: List<Category> = listOf(
    Category(name = "Food", type = CategoryType.EXPENSE, icon = "restaurant", color = Color.parseColor("#EF5350"), isDefault = true),
    Category(name = "Transport", type = CategoryType.EXPENSE, icon = "directions_car", color = Color.parseColor("#42A5F5"), isDefault = true),
    Category(name = "Bills", type = CategoryType.EXPENSE, icon = "receipt_long", color = Color.parseColor("#FFA726"), isDefault = true),
    Category(name = "Shopping", type = CategoryType.EXPENSE, icon = "shopping_bag", color = Color.parseColor("#AB47BC"), isDefault = true),
    Category(name = "Salary", type = CategoryType.INCOME, icon = "payments", color = Color.parseColor("#66BB6A"), isDefault = true),
    Category(name = "Transfer", type = CategoryType.BOTH, icon = "swap_horiz", color = Color.parseColor("#26A69A"), isDefault = true),
    Category(name = "Other", type = CategoryType.BOTH, icon = "category", color = Color.parseColor("#78909C"), isDefault = true)
)

suspend fun seedDefaultCategories(categoryDao: CategoryDao) {
    categoryDao.insertAll(defaultCategories)
}
