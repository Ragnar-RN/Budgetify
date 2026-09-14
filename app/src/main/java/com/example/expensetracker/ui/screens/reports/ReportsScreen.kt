package com.example.expensetracker.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.expensetracker.ui.util.formatCurrency
import com.example.expensetracker.ui.util.formatDate
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Defaults

private val IncomeColor = Color(0xFF2E7D32)
private val ExpenseColor = Color(0xFFC62828)
private val BarColor = Color(0xFF5C6BC0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Reports") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                PeriodSelector(
                    period = uiState.period,
                    onTypeSelected = viewModel::onPeriodTypeSelected,
                    onCustomStartSelected = viewModel::onCustomStartSelected,
                    onCustomEndSelected = viewModel::onCustomEndSelected
                )
            }
            item {
                CategoryBreakdownSection(
                    items = uiState.categoryBreakdown,
                    totalSpend = uiState.totalSpend
                )
            }
            item {
                TrendSection(points = uiState.trendPoints)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodSelector(
    period: ReportPeriod,
    onTypeSelected: (ReportPeriodType) -> Unit,
    onCustomStartSelected: (Long) -> Unit,
    onCustomEndSelected: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = period.type == ReportPeriodType.WEEK,
                onClick = { onTypeSelected(ReportPeriodType.WEEK) },
                label = { Text("Week") }
            )
            FilterChip(
                selected = period.type == ReportPeriodType.MONTH,
                onClick = { onTypeSelected(ReportPeriodType.MONTH) },
                label = { Text("Month") }
            )
            FilterChip(
                selected = period.type == ReportPeriodType.YEAR,
                onClick = { onTypeSelected(ReportPeriodType.YEAR) },
                label = { Text("Year") }
            )
            FilterChip(
                selected = period.type == ReportPeriodType.CUSTOM,
                onClick = { onTypeSelected(ReportPeriodType.CUSTOM) },
                label = { Text("Custom") }
            )
        }
        if (period.type == ReportPeriodType.CUSTOM) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DateButton(
                    label = "From",
                    dateMillis = period.customStart,
                    modifier = Modifier.weight(1f),
                    onDateSelected = onCustomStartSelected
                )
                DateButton(
                    label = "To",
                    dateMillis = period.customEnd,
                    modifier = Modifier.weight(1f),
                    onDateSelected = onCustomEndSelected
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateButton(
    label: String,
    dateMillis: Long?,
    modifier: Modifier = Modifier,
    onDateSelected: (Long) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    OutlinedButton(onClick = { showDialog = true }, modifier = modifier) {
        Text(if (dateMillis != null) "$label: ${formatDate(dateMillis)}" else "$label: Select")
    }

    if (showDialog) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                    showDialog = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun CategoryBreakdownSection(
    items: List<CategoryBreakdownItem>,
    totalSpend: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Category Breakdown", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Total spend: ${formatCurrency(totalSpend)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (items.isEmpty()) {
            Text(
                text = "No expenses in this period",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            CategoryBarChart(items)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items.forEach { item ->
                    CategoryBreakdownRow(item)
                }
            }
        }
    }
}

@Composable
private fun CategoryBarChart(items: List<CategoryBreakdownItem>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val categoryNames = remember(items) { items.map { it.category?.name ?: "Other" } }

    LaunchedEffect(items) {
        modelProducer.runTransaction {
            columnSeries { series(y = items.map { it.total }) }
        }
    }

    val bottomFormatter = remember(categoryNames) {
        CartesianValueFormatter { _, value, _ ->
            categoryNames[value.toInt().coerceIn(0, categoryNames.lastIndex)]
        }
    }

    val columnLayer = rememberColumnCartesianLayer(
        columnProvider = ColumnCartesianLayer.ColumnProvider.series(
            rememberLineComponent(fill = fill(BarColor), thickness = Defaults.COLUMN_WIDTH.dp)
        )
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            columnLayer,
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomFormatter,
                labelRotationDegrees = -45f
            )
        ),
        modelProducer = modelProducer,
        modifier = Modifier.fillMaxWidth().height(220.dp)
    )
}

@Composable
private fun CategoryBreakdownRow(item: CategoryBreakdownItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val dotColor = item.category?.color?.let { Color(it) } ?: MaterialTheme.colorScheme.surfaceVariant
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.category?.name ?: "Other",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${(item.fraction * 100).toInt()}%",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = formatCurrency(item.total), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun TrendSection(points: List<TrendPoint>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Income vs Expense", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LegendDot(color = IncomeColor, label = "Income")
            LegendDot(color = ExpenseColor, label = "Expense")
        }
        if (points.isEmpty()) {
            Text(
                text = "No transactions in this period",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            TrendLineChart(points)
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun TrendLineChart(points: List<TrendPoint>) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val labels = remember(points) { points.map { it.label } }

    LaunchedEffect(points) {
        modelProducer.runTransaction {
            lineSeries {
                series(y = points.map { it.income })
                series(y = points.map { it.expense })
            }
        }
    }

    val bottomFormatter = remember(labels) {
        CartesianValueFormatter { _, value, _ ->
            labels[value.toInt().coerceIn(0, labels.lastIndex)]
        }
    }

    val lineLayer = rememberLineCartesianLayer(
        lineProvider = LineCartesianLayer.LineProvider.series(
            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(IncomeColor))),
            LineCartesianLayer.rememberLine(fill = LineCartesianLayer.LineFill.single(fill(ExpenseColor)))
        )
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            lineLayer,
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomFormatter
            )
        ),
        modelProducer = modelProducer,
        modifier = Modifier.fillMaxWidth().height(220.dp)
    )
}
