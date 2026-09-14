package com.example.expensetracker.ui.screens.addtransaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.entity.Category
import com.example.expensetracker.data.local.entity.CategoryType
import com.example.expensetracker.data.local.entity.Transaction
import com.example.expensetracker.data.local.entity.TransactionSource
import com.example.expensetracker.data.local.entity.TransactionType
import com.example.expensetracker.data.repository.CategoryRepository
import com.example.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val transactionId: Long? = null,
    val amountText: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val allCategories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val dateMillis: Long = System.currentTimeMillis(),
    val note: String = "",
    val paymentMethod: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val showDeleteConfirmation: Boolean = false,
    val saveCompleted: Boolean = false,
    val deleteCompleted: Boolean = false
) {
    val visibleCategories: List<Category>
        get() {
            val matchingCategoryType = if (type == TransactionType.EXPENSE) {
                CategoryType.EXPENSE
            } else {
                CategoryType.INCOME
            }
            return allCategories.filter { it.type == matchingCategoryType || it.type == CategoryType.BOTH }
        }

    val isEditMode: Boolean get() = transactionId != null
}

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private var originalTransaction: Transaction? = null
    private var didDefaultCategory = false

    init {
        viewModelScope.launch {
            categoryRepository.getAll().collect { categories ->
                _uiState.update { current ->
                    val updated = current.copy(allCategories = categories)
                    if (!didDefaultCategory && updated.selectedCategoryId == null) {
                        didDefaultCategory = true
                        updated.copy(selectedCategoryId = updated.visibleCategories.firstOrNull()?.id)
                    } else {
                        updated
                    }
                }
            }
        }
    }

    fun loadTransaction(transactionId: Long) {
        if (_uiState.value.transactionId == transactionId) return
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val transaction = transactionRepository.getById(transactionId)
            if (transaction != null) {
                originalTransaction = transaction
                _uiState.update {
                    it.copy(
                        transactionId = transaction.id,
                        amountText = formatAmountForInput(transaction.amount),
                        type = transaction.type,
                        selectedCategoryId = transaction.categoryId,
                        dateMillis = transaction.date,
                        note = transaction.note ?: "",
                        paymentMethod = transaction.paymentMethod ?: "",
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Transaction not found") }
            }
        }
    }

    fun onAmountChanged(text: String) {
        _uiState.update { it.copy(amountText = text, errorMessage = null) }
    }

    fun onTypeChanged(type: TransactionType) {
        _uiState.update { current ->
            val updated = current.copy(type = type)
            val stillValid = updated.visibleCategories.any { it.id == updated.selectedCategoryId }
            if (stillValid) updated else updated.copy(selectedCategoryId = updated.visibleCategories.firstOrNull()?.id)
        }
    }

    fun onCategorySelected(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onDateChanged(dateMillis: Long) {
        _uiState.update { it.copy(dateMillis = dateMillis) }
    }

    fun onNoteChanged(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onPaymentMethodChanged(paymentMethod: String) {
        _uiState.update { it.copy(paymentMethod = paymentMethod) }
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amountText.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            _uiState.update { it.copy(errorMessage = "Enter a valid amount greater than zero") }
            return
        }
        val categoryId = state.selectedCategoryId
        if (categoryId == null) {
            _uiState.update { it.copy(errorMessage = "Select a category") }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            val existing = originalTransaction
            val transaction = Transaction(
                id = existing?.id ?: 0,
                amount = amount,
                type = state.type,
                categoryId = categoryId,
                date = state.dateMillis,
                note = state.note.trim().ifBlank { null },
                source = existing?.source ?: TransactionSource.MANUAL,
                paymentMethod = state.paymentMethod.trim().ifBlank { null },
                createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            if (existing != null) {
                transactionRepository.update(transaction)
                originalTransaction = transaction
                _uiState.update { it.copy(isSaving = false, saveCompleted = true) }
            } else {
                transactionRepository.insert(transaction)
                _uiState.update {
                    AddTransactionUiState(
                        allCategories = it.allCategories,
                        type = it.type,
                        selectedCategoryId = it.selectedCategoryId,
                        saveCompleted = true
                    )
                }
            }
        }
    }

    fun requestDelete() {
        _uiState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun dismissDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
    }

    fun confirmDelete() {
        val existing = originalTransaction ?: return
        _uiState.update { it.copy(showDeleteConfirmation = false, isSaving = true) }
        viewModelScope.launch {
            transactionRepository.delete(existing)
            _uiState.update { it.copy(isSaving = false, deleteCompleted = true) }
        }
    }

    fun consumeSaveCompleted() {
        _uiState.update { it.copy(saveCompleted = false) }
    }

    fun consumeDeleteCompleted() {
        _uiState.update { it.copy(deleteCompleted = false) }
    }
}

private fun formatAmountForInput(amount: Double): String {
    return if (amount == amount.toLong().toDouble()) amount.toLong().toString() else amount.toString()
}
