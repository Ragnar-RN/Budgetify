package com.example.expensetracker.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.repository.CategoryRepository

class SettingsViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel()
