package com.example.portfoliomadeeasy.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.portfoliomadeeasy.viewmodel.ExpenseGoalsViewModel
import com.example.portfoliomadeeasy.R
import com.example.portfoliomadeeasy.model.Expense
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.text.toFloat
import kotlin.times

enum class StatsTab(val label: String) {
    PIE("Rashodi"),
    REPORT("Izvještaj"),
    HISTOGRAM("Histogram"),
    TOP5("Top 5"),
    LINE("Trend")
}

@Composable
fun StatsScreen(
    viewModel: ExpenseGoalsViewModel,
    navigation: NavController,
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }
    var selectedTab by remember { mutableStateOf(StatsTab.PIE) }

    val monthlyExpenses = viewModel.expenseData.filter { expense ->
        val expenseDate = expense.date.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        expenseDate.year == currentMonth.year && expenseDate.month == currentMonth.month
    }

    val totalSpent = monthlyExpenses.sumOf { it.amount }

    val categorySpending: Map<Category, Double> = monthlyExpenses
        .groupBy { it.categoryname}
        .mapValues { entry ->
            entry.value.sumOf { it.amount }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(StatsTab.values()) { tab ->
                Button(
                    onClick = { selectedTab = tab }
                ) {
                    Text(tab.label)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            StatsTab.PIE -> {
                MonthPicking(
                    currentMonth,
                    onMonthChange = { newMonth -> currentMonth = newMonth }
                )

                if (totalSpent == 0.0) {
                    Text("Nema trošenja u ovom mjesecu")
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ukupno trošenje: $${"%.2f".format(totalSpent)}",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    PieChart(categorySpending)
                }
            }

            StatsTab.REPORT -> {
                Column{
                    MonthPicking(
                        currentMonth,
                        onMonthChange = { newMonth -> currentMonth = newMonth }
                    )
                }

                FinancialReport(viewModel, currentMonth)
            }

            StatsTab.HISTOGRAM -> {
                Column{
                    MonthPicking(
                        currentMonth,
                        onMonthChange = { newMonth -> currentMonth = newMonth }
                    )
                }

                val monthlyExpenses = viewModel.expenseData.filter { expense ->
                    val date = expense.date.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate()
                    date.year == currentMonth.year && date.month == currentMonth.month
                }

                Histogram(monthlyExpenses)
            }

            StatsTab.TOP5 -> {
                Column {
                    MonthPicking(
                        currentMonth,
                        onMonthChange = { newMonth -> currentMonth = newMonth }
                    )
                }

                Top5Expenses(monthlyExpenses)
            }

            StatsTab.LINE -> {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = { currentYear-- }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_leftarrow),
                                contentDescription = "Previous year"
                            )
                        }

                        Text(
                            text = "$currentYear",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Button(onClick = { currentYear++ }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_rightarrow),
                                contentDescription = "Next year"
                            )
                        }
                    }

                    val yearlyExpenses = viewModel.expenseData.filter { expense ->
                        val date = expense.date.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        date.year == currentYear
                    }

                    LineChart(yearlyExpenses)
                }
            }
        }
    }
}

@Composable
fun MonthPicking(
    currentMonth: LocalDate,
    onMonthChange: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                onMonthChange(currentMonth.minusMonths(1))
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_leftarrow),
                contentDescription = "Left arrow"
            )
        }

        Text(
            text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
            style = MaterialTheme.typography.titleLarge
        )

        Button(
            onClick = {
                onMonthChange(currentMonth.plusMonths(1))
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_rightarrow),
                contentDescription = "Right arrow"
            )
        }
    }
}

@Composable
fun PieChart(
    data: Map<Category, Double>,
) {
    val total = data.values.sum()
    if (total == 0.0) return

    val angles = data.mapValues { (it.value / total * 360f).toFloat() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ){
        Text(
            text = "Podjela rashoda",
            fontSize = 18.sp,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )

        Spacer(Modifier.height(15.dp))

        Canvas(
            modifier = Modifier
                .size(250.dp)
                .padding(16.dp),
        ) {
            var startAngle = 0f
            data.entries.forEachIndexed { index, (category, value) ->
                val sweepAngle = angles[category] ?: 0f
                drawArc(
                    color = category.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.width, size.height),
                    topLeft = Offset(0f, 0f)
                )
                startAngle += sweepAngle
            }
        }

        Spacer(Modifier.height(15.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            data.entries.forEach { (category, value) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(category.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${category.displayName} - $${"%.2f".format(value)}",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialReport(
    viewModel: ExpenseGoalsViewModel,
    currentMonth: LocalDate
) {
    val totalIncome = viewModel.totalIncome(currentMonth)
    val totalExpenses = viewModel.totalExpenses(currentMonth)
    val netSavings = totalIncome - totalExpenses

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("Financijski izvještaj za: ${currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))}", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Ukupni prihodi: $${"%.2f".format(totalIncome)}",
            fontSize = 18.sp
        )

        Text(
            "Ukupni rashodi: $${"%.2f".format(totalExpenses)}",
            fontSize = 18.sp
        )

        Text(
            "Ušteđeno: $${"%.2f".format(netSavings)}",
            fontSize = 18.sp,
            color = if (netSavings >= 0) Color.Green else Color.Red
        )
    }
}

@Composable
fun Top5Expenses(monthlyExpenses: List<Expense>) {
    val top5 = monthlyExpenses.sortedByDescending { it.amount }.take(5)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Top 5 troškova", fontSize = 18.sp, modifier = Modifier.padding(bottom = 8.dp))
        if (top5.isEmpty()) {
            Text("Nema troškova u ovom mjesecu")
        } else {
            top5.forEach { expense ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(expense.categoryname.displayName)
                    Text("$${"%.2f".format(expense.amount)}")
                }
            }
        }
    }
}

@Composable
fun LineChart(
    expenses: List<Expense>
) {

    val expensesByMonth = (1..12).map { month ->
        val expenses = expenses.filter { expense ->
            val date = expense.date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
            date.monthValue == month && date.year == LocalDate.now().year
        }
        expenses.sumOf { it.amount }
    }

    val maxExpense = expensesByMonth.maxOrNull() ?: 0.0

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Trend potrošnje (${LocalDate.now().year})", fontSize = 18.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
        ) {
            if (maxExpense > 0) {
                val stepX = size.width / (expensesByMonth.size - 1)
                val scaleY = size.height / maxExpense.toFloat()

                var prevPoint: Offset? = null
                expensesByMonth.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = size.height - (value.toFloat() * scaleY)
                    val point = Offset(x, y)

                    prevPoint?.let {
                        drawLine(
                            color = Color.Blue,
                            start = it,
                            end = point,
                            strokeWidth = 4f
                        )
                    }
                    prevPoint = point

                    drawCircle(
                        color = Color.Red,
                        radius = 6f,
                        center = point
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Mjeseci (1–12)", fontSize = 12.sp)
    }
}

fun getHistogramBins(expenses: List<Expense>, binSize: Double = 50.0): Map<String, Int> {
    if (expenses.isEmpty()) return emptyMap()

    val maxAmount = expenses.maxOf { it.amount }
    val binCount = (maxAmount / binSize).toInt() + 1

    val bins = mutableMapOf<String, Int>()
    for (i in 0 until binCount) {
        val rangeLabel = "${i * binSize.toInt()}-${((i + 1) * binSize).toInt()}"
        bins[rangeLabel] = 0
    }

    expenses.forEach { expense ->
        val index = (expense.amount / binSize).toInt()
        val rangeLabel = "${index * binSize.toInt()}-${((index + 1) * binSize).toInt()}"
        bins[rangeLabel] = bins[rangeLabel]?.plus(1) ?: 1
    }

    return bins
}
@Composable
fun Histogram(
    expenses: List<Expense>,
    binSize: Double = 50.0
) {
    val bins = getHistogramBins(expenses, binSize)
    val maxCount = bins.values.maxOrNull() ?: 1

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Histogram troškova", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier
            .height(250.dp)
            .fillMaxWidth()) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .padding(start = 40.dp, bottom = 30.dp, end = 16.dp, top = 16.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val stepX = canvasWidth / bins.size

                drawLine(
                    color = Color.Black,
                    start = Offset(0f, 0f),
                    end = Offset(0f, canvasHeight),
                    strokeWidth = 2f
                )

                drawLine(
                    color = Color.Black,
                    start = Offset(0f, canvasHeight),
                    end = Offset(canvasWidth, canvasHeight),
                    strokeWidth = 2f
                )

                bins.entries.forEachIndexed { index, (range, count) ->
                    val barHeight = if (maxCount == 0) 0f else (count.toFloat() / maxCount) * canvasHeight
                    val x = index * stepX + stepX * 0.1f
                    val y = canvasHeight - barHeight
                    drawRect(
                        color = Color(0xFF4CAF50),
                        topLeft = Offset(x, y),
                        size = Size(stepX * 0.8f, barHeight)
                    )

                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            range,
                            x + stepX * 0.4f,
                            canvasHeight + 15f,
                            android.graphics.Paint().apply {
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = 24f
                                color = android.graphics.Color.BLACK
                            }
                        )
                    }
                }

                listOf(0f, maxCount / 2f, maxCount.toFloat()).forEach { value ->
                    val yPos = canvasHeight - (value / maxCount) * canvasHeight

                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, yPos),
                        end = Offset(canvasWidth, yPos),
                        strokeWidth = 1f
                    )

                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            value.toInt().toString(),
                            -10f,
                            yPos + 8f,
                            android.graphics.Paint().apply {
                                textAlign = android.graphics.Paint.Align.RIGHT
                                textSize = 24f
                                color = android.graphics.Color.BLACK
                            }
                        )
                    }
                }
            }
        }
    }
}