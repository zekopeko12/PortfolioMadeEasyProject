package com.example.portfoliomadeeasy.view

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.portfoliomadeeasy.viewmodel.ExpenseGoalsViewModel
import com.example.portfoliomadeeasy.R
import com.example.portfoliomadeeasy.model.Income
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@Composable
fun IncomeScreen(
    viewModel: ExpenseGoalsViewModel,
    navigation: NavController
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth((1))) }

    val incomesInMonth = viewModel.incomeData.filter {
        val cal = Calendar.getInstance().apply { time = it.date }
        cal.get(Calendar.YEAR) == currentMonth.year && cal.get(Calendar.MONTH) == currentMonth.monthValue - 1
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

            if (incomesInMonth.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nema prihoda u ovom mjesecu",
                        style = TextStyle(fontSize = 20.sp)
                    )
                }
            } else {
                LazyColumn {
                    items(incomesInMonth) { income ->
                        IncomeItem(
                            income = income,
                            onDelete = { toDelete -> viewModel.deleteIncome(toDelete.id) },
                            onEdit = { toEdit ->
                                navigation.navigate("IncomeEdit/${toEdit.id}")
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = { navigation.navigate(Routes.SCREEN_INCOME_ADD) },
            shape = CircleShape,
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            contentPadding = PaddingValues(0.dp),
        ){
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "Add new expense"
            )
        }
    }
}

@Composable
fun IncomeItem(
    income: Income,
    onDelete: (Income) -> Unit,
    onEdit: (Income) -> Unit
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
                text = income.title,
                modifier = Modifier
                    .padding(4.dp)
            )

            Text(
                text = "$${income.amount}",
                modifier = Modifier
                    .padding(4.dp)
            )

            Text(
                text = dateFormat.format(income.date),
                modifier = Modifier
                    .padding(4.dp)
            )
        }

        Row{
            Button(
                onClick = { onEdit(income) },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ){
                Text("Uredi")
            }

            Button(
                onClick = { onDelete(income) },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            ) {
                Text("Obriši")
            }
        }
    }
}