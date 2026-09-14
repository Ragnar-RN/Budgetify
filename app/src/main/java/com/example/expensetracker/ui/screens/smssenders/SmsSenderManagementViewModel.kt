package com.example.expensetracker.ui.screens.smssenders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.local.entity.SmsSenderPattern
import com.example.expensetracker.data.repository.SmsSenderPatternRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SmsSenderManagementUiState(
    val senders: List<SmsSenderPattern> = emptyList(),
    val isDialogOpen: Boolean = false,
    val senderIdInput: String = "",
    val labelInput: String = "",
    val dialogError: String? = null,
    val pendingDelete: SmsSenderPattern? = null
)

class SmsSenderManagementViewModel(
    private val repository: SmsSenderPatternRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmsSenderManagementUiState())
    val uiState: StateFlow<SmsSenderManagementUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAll().collect { senders ->
                _uiState.update { it.copy(senders = senders) }
            }
        }
    }

    fun openAddDialog() {
        _uiState.update { it.copy(isDialogOpen = true, senderIdInput = "", labelInput = "", dialogError = null) }
    }

    fun closeDialog() {
        _uiState.update { it.copy(isDialogOpen = false) }
    }

    fun onSenderIdChanged(value: String) {
        _uiState.update { it.copy(senderIdInput = value, dialogError = null) }
    }

    fun onLabelChanged(value: String) {
        _uiState.update { it.copy(labelInput = value) }
    }

    fun saveSender() {
        val state = _uiState.value
        val senderId = state.senderIdInput.trim()
        if (senderId.isEmpty()) {
            _uiState.update { it.copy(dialogError = "Sender ID cannot be empty") }
            return
        }
        val label = state.labelInput.trim().ifEmpty { senderId }

        viewModelScope.launch {
            repository.insert(SmsSenderPattern(senderId = senderId, label = label, isActive = true))
            _uiState.update { it.copy(isDialogOpen = false) }
        }
    }

    fun toggleActive(pattern: SmsSenderPattern) {
        viewModelScope.launch {
            repository.update(pattern.copy(isActive = !pattern.isActive))
        }
    }

    fun requestDelete(pattern: SmsSenderPattern) {
        _uiState.update { it.copy(pendingDelete = pattern) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(pendingDelete = null) }
    }

    fun confirmDelete() {
        val pattern = _uiState.value.pendingDelete ?: return
        viewModelScope.launch {
            repository.delete(pattern)
            _uiState.update { it.copy(pendingDelete = null) }
        }
    }
}
