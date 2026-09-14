package com.example.expensetracker.ui.screens.smsrationale

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.expensetracker.sms.SMS_PERMISSIONS
import com.example.expensetracker.sms.hasSmsPermissions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsRationaleScreen(
    onBack: () -> Unit,
    onNavigateToSenderManagement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var permissionsGranted by remember { mutableStateOf(hasSmsPermissions(context)) }
    var wasRequested by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        wasRequested = true
        permissionsGranted = results.values.all { it }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("SMS Auto-Detection") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Why we ask for SMS access", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Many banks and mobile wallets send a text for every transaction. If you " +
                    "enable this, the app can watch for messages from senders you choose (like " +
                    "your bank) to speed up expense entry. Message content is read on your " +
                    "device only — nothing is sent or shared anywhere."
            )
            Text(
                text = "Nothing happens automatically: you choose exactly which senders are " +
                    "watched under \"Watched SMS Senders\". Messages from any other sender are " +
                    "ignored.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "If you don't grant this permission, or change your mind later, every " +
                    "other part of the app — manual entry, categories, reports — keeps working " +
                    "exactly the same.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when {
                permissionsGranted -> {
                    Text(
                        text = "SMS access is granted.",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Button(onClick = onNavigateToSenderManagement, modifier = Modifier.fillMaxWidth()) {
                        Text("Manage Watched Senders")
                    }
                }
                wasRequested -> {
                    Text(
                        text = "Permission was denied. You can grant it later from this app's " +
                            "system Settings page if you change your mind.",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Back to Settings")
                    }
                }
                else -> {
                    Button(
                        onClick = { permissionLauncher.launch(SMS_PERMISSIONS) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue")
                    }
                    TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                        Text("Not now")
                    }
                }
            }
        }
    }
}
