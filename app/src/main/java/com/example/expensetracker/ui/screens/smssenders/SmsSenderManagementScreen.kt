package com.example.expensetracker.ui.screens.smssenders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.expensetracker.data.local.entity.SmsSenderPattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsSenderManagementScreen(
    viewModel: SmsSenderManagementViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Watched SMS Senders") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openAddDialog() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add sender")
            }
        }
    ) { innerPadding ->
        if (uiState.senders.isEmpty()) {
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = "No watched senders yet. Add a bank or wallet's sender ID to start.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
                items(uiState.senders, key = { it.id }) { pattern ->
                    SenderRow(
                        pattern = pattern,
                        onToggleActive = { viewModel.toggleActive(pattern) },
                        onDelete = { viewModel.requestDelete(pattern) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (uiState.isDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.closeDialog() },
            title = { Text("Add Watched Sender") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.senderIdInput,
                        onValueChange = viewModel::onSenderIdChanged,
                        label = { Text("Sender ID or phone number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.labelInput,
                        onValueChange = viewModel::onLabelChanged,
                        label = { Text("Label (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    uiState.dialogError?.let { error ->
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.saveSender() }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeDialog() }) { Text("Cancel") }
            }
        )
    }

    uiState.pendingDelete?.let { pattern ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            title = { Text("Remove sender?") },
            text = { Text("Remove \"${pattern.label}\"? Messages from it will no longer be watched.") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDelete() }) { Text("Remove") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteDialog() }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SenderRow(
    pattern: SmsSenderPattern,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = pattern.label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = pattern.senderId,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = pattern.isActive, onCheckedChange = { onToggleActive() })
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Remove ${pattern.label}")
        }
    }
}
