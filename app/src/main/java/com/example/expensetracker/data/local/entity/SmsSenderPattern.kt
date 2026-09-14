package com.example.expensetracker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_sender_patterns")
data class SmsSenderPattern(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "sender_id")
    val senderId: String,
    val label: String,
    @ColumnInfo(name = "regex_amount")
    val regexAmount: String? = null,
    @ColumnInfo(name = "regex_type_indicator")
    val regexTypeIndicator: String? = null,
    @ColumnInfo(name = "date_format")
    val dateFormat: String? = null,
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
