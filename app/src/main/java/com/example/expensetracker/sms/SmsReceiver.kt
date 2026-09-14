package com.example.expensetracker.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast
import com.example.expensetracker.ExpenseTrackerApplication
import com.example.expensetracker.data.local.entity.SmsSenderPattern
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "SmsReceiver"

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) return

        val sender = messages[0].originatingAddress ?: return
        val body = messages.joinToString(separator = "") { it.messageBody ?: "" }

        val appContext = context.applicationContext
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository =
                    (appContext as ExpenseTrackerApplication).container.smsSenderPatternRepository
                val activeSenders = repository.getActiveOnce()
                val matched = activeSenders.firstOrNull { pattern -> sender.matchesSenderPattern(pattern) }

                if (matched != null) {
                    Log.d(TAG, "Matched SMS from \"${matched.label}\" (sender=$sender): $body")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            appContext,
                            "Expense SMS detected from ${matched.label}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Log.d(TAG, "SMS from $sender did not match any watched sender; ignoring")
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}

private fun String.matchesSenderPattern(pattern: SmsSenderPattern): Boolean {
    val sender = trim()
    val target = pattern.senderId.trim()
    if (target.isEmpty()) return false
    return sender.equals(target, ignoreCase = true) || sender.contains(target, ignoreCase = true)
}
