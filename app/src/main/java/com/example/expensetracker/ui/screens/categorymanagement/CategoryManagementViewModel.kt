package com.example.expensetracker.ui.screens.categorymanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.entity.Category
import com.example.expensetracker.data.local.entity.CategoryType
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val CATEGORY_PALETTE = listOf(
    0xFFEF5350.toInt(),
    0xFF42A5F5.toInt(),
    0xFFFFA726.toInt(),
    0xFFAB47BC.toInt(),
    0xFF66BB6A.toInt(),
    0xFF26A69A.toInt(),
    0xFF78909C.toInt(),
    0xFFEC407A.toInt()
)

data class CategoryManagementUiState(
    val categories: List<Category> = emptyList(),
    val isDialogOpen: Boolean = false,
    val editingCategory: Category? = null,
    val nameInput: String = "",
    val typeInput: CategoryType = CategoryType.EXPENSE,
    val dialogError: String? = null,
    val pendingDelete: Category? = null,
    val blockedDeleteMessage: String? = null
)

class CategoryManagementViewModel(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryManagementUiState())
    val uiState: StateFlow<CategoryManagementUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.getAll().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    fun openAddDialog() {
        _uiState.update {
            it.copy(
                isDialogOpen = true,
                editingCategory = null,
                nameInput = "",
                typeInput = CategoryType.EXPENSE,
                dialogError = null
            )
        }
    }

    fun openEditDialog(category: Category) {
        _uiState.update {
            it.copy(
                isDialogOpen = true,
                editingCategory = category,
                nameInput = category.name,
                typeInput = category.type,
                dialogError = null
            )
        }
    }

    fun closeDialog() {
        _uiState.update { it.copy(isDialogOpen = false) }
    }

    fun onNameChanged(name: String) {
        _uiState.update { it.copy(nameInput = name, dialogError = null) }
    }

    fun onTypeChanged(type: CategoryType) {
        _uiState.update { it.copy(typeInput = type) }
    }

    fun saveCategory() {
        val state = _uiState.value
        val name = state.nameInput.trim()
        if (name.isEmpty()) {
            _uiState.update { it.copy(dialogError = "Name cannot be empty") }
            return
        }

        viewModelScope.launch {
            val editing = state.editingCategory
            if (editing != null) {
                categoryRepository.update(editing.copy(name = name, type = state.typeInput))
            } else {
                val nextColor = CATEGORY_PALETTE[state.categories.size % CATEGORY_PALETTE.size]
                categoryRepository.insert(
                    Category(
                        name = name,
                        type = state.typeInput,
                        icon = "category",
                        color = nextColor,
                        isDefault = false
                    )
                )
            }
            _uiState.update { it.copy(isDialogOpen = false) }
        }
    }

    fun requestDelete(category: Category) {
        _uiState.update { it.copy(pendingDelete = category) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(pendingDelete = null) }
    }

    fun confirmDelete() {
        val category = _uiState.value.pendingDelete ?: return
        viewModelScope.launch {
            val usageCount = transactionRepository.countByCategory(category.id)
            if (usageCount > 0) {
                _uiState.update {
                    it.copy(
                        pendingDelete = null,
                        blockedDeleteMessage = "\"${category.name}\" is used by $usageCount transaction(s). Reassign or delete those first."
                    )
                }
            } else {
                categoryRepository.delete(category)
                _uiState.update { it.copy(pendingDelete = null) }
            }
        }
    }

    fun dismissBlockedMessage() {
        _uiState.update { it.copy(blockedDeleteMessage = null) }
    }
}
