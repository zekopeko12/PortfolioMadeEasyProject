package com.example.portfoliomadeeasy.view

import android.icu.util.Calendar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.portfoliomadeeasy.R
import com.example.portfoliomadeeasy.model.Expense
import com.example.portfoliomadeeasy.viewmodel.ExpenseGoalsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class Category(val displayName: String, val color: Color, val iconResId: Int){
    HEALTH_AND_EXERCISE("Zdravlje i tjelovježba", Color.Red, R.drawable.ic_muscle_icon),
    SHOPPING("Kupovina", Color.Blue, R.drawable.ic_shopping_cart),
    FUN("Zabava", Color.Green, R.drawable.ic_football),
    BILLS("Računi", Color.Yellow, R.drawable.ic_moneybag),
    FOOD("Hrana", Color.Gray, R.drawable.ic_food),
    ASSET("Imovina", Color.Magenta, R.drawable.ic_asset)
}

@Composable
fun Expenses(
    navigation: NavController,
    viewModel: ExpenseGoalsViewModel
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth((1))) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

    val expensesInCurrentMonth = viewModel.expenseData.filter { expense ->
        val cal = Calendar.getInstance().apply { time = expense.date }
        val sameMonth = cal.get(Calendar.YEAR) == currentMonth.year &&
                cal.get(Calendar.MONTH) == currentMonth.monthValue - 1
        val matchesCategory = selectedCategory == null || selectedCategory == expense.categoryname
        sameMonth && matchesCategory
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        currentMonth = currentMonth.minusMonths(1)
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
                        currentMonth = currentMonth.plusMonths(1)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_rightarrow),
                        contentDescription = "Right arrow"
                    )
                }
            }

            var expanded by remember { mutableStateOf(false) }

            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Button(onClick = { expanded = true }) {
                    Text(selectedCategory?.displayName ?: "Odaberite kategoriju")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sve kategorije") },
                        onClick = {
                            selectedCategory = null
                            expanded = false
                        }
                    )

                    Category.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.displayName) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            if(expensesInCurrentMonth.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nema rashoda u ovom mjesecu",
                        style = TextStyle(fontSize = 20.sp)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                ) {
                    items(expensesInCurrentMonth) { expense ->
                        ExpenseItem(
                            expense = expense,
                            onDelete = { toDelete -> viewModel.deleteExpense(toDelete.id) },
                            onEdit = { toEdit ->
                                navigation.navigate("ExpensesEdit/${toEdit.id}")
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = { navigation.navigate(Routes.SCREEN_EXPENSES_ADD) },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(90.dp)
                .padding(16.dp),
            contentPadding = PaddingValues(0.dp),
        ){
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "Dodaj novi rashod"
            )
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Expense,
    onDelete: (Expense) -> Unit,
    onEdit: (Expense) -> Unit
) {
    val dateFormat = remember { java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            Text(
                text = expense.categoryname.displayName,
                modifier = Modifier
                    .padding(4.dp)
            )

            Text(
                text = expense.title,
                modifier = Modifier
                    .padding(4.dp)
            )

            Text(
                text = "$${expense.amount}",
                modifier = Modifier
                    .padding(4.dp)
            )

            Text(
                text = dateFormat.format(expense.date),
                modifier = Modifier
                    .padding(4.dp)
            )
        }

        Row {
            Button(
                onClick = { onEdit(expense) },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ){
                Text("Uredi")
            }

            Button(
                onClick = { onDelete(expense) },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Text("Obriši")
            }
        }
    }
}