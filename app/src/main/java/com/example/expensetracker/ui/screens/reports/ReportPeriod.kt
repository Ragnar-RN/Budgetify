package com.example.expensetracker.ui.screens.reports

import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class ReportPeriodType {
    WEEK,
    MONTH,
    YEAR,
    CUSTOM
}

private val zoneId: ZoneId = ZoneId.systemDefault()

data class ReportPeriod(
    val type: ReportPeriodType = ReportPeriodType.MONTH,
    val customStart: Long? = null,
    val customEnd: Long? = null
) {
    fun range(): Pair<Long, Long> {
        val today = LocalDate.now(zoneId)
        return when (type) {
            ReportPeriodType.WEEK -> today.minusDays(6).startOfDayMillis() to today.endOfDayMillis()
            ReportPeriodType.MONTH -> {
                val month = YearMonth.from(today)
                month.atDay(1).startOfDayMillis() to month.atEndOfMonth().endOfDayMillis()
            }
            ReportPeriodType.YEAR -> {
                LocalDate.of(today.year, 1, 1).startOfDayMillis() to LocalDate.of(today.year, 12, 31).endOfDayMillis()
            }
            ReportPeriodType.CUSTOM -> {
                val start = customStart ?: YearMonth.from(today).atDay(1).startOfDayMillis()
                val end = customEnd ?: today.endOfDayMillis()
                start to end
            }
        }
    }

    fun bucketSqlFormat(): String {
        return when (type) {
            ReportPeriodType.WEEK, ReportPeriodType.MONTH -> "%Y-%m-%d"
            ReportPeriodType.YEAR -> "%Y-%m"
            ReportPeriodType.CUSTOM -> {
                val (start, end) = range()
                val days = (end - start) / MILLIS_PER_DAY
                when {
                    days <= 62 -> "%Y-%m-%d"
                    days <= 731 -> "%Y-%m"
                    else -> "%Y"
                }
            }
        }
    }

    companion object {
        private const val MILLIS_PER_DAY = 86_400_000L
    }
}

fun formatBucketLabel(bucket: String, bucketFormat: String): String {
    return when (bucketFormat) {
        "%Y-%m-%d" -> LocalDate.parse(bucket).format(DateTimeFormatter.ofPattern("MMM d"))
        "%Y-%m" -> YearMonth.parse(bucket).format(DateTimeFormatter.ofPattern("MMM yyyy"))
        else -> bucket
    }
}

private fun LocalDate.startOfDayMillis(): Long = atStartOfDay(zoneId).toInstant().toEpochMilli()

private fun LocalDate.endOfDayMillis(): Long = atTime(23, 59, 59).atZone(zoneId).toInstant().toEpochMilli()
