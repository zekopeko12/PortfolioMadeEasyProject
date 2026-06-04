package com.example.portfoliomadeeasy.view

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.portfoliomadeeasy.model.FinancialGoal
import com.example.portfoliomadeeasy.viewmodel.ExpenseGoalsViewModel
import com.example.portfoliomadeeasy.R

@Composable
fun GoalsScreen(
    viewModel: ExpenseGoalsViewModel,
    navigation: NavController
) {
    val goals = viewModel.financialGoalData
    var showCompleted by remember { mutableStateOf(false) }
    val filteredGoals = goals.filter { it.completed == showCompleted }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                "Financijski ciljevi",
                fontSize = 50.sp
            )
            if (goals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nema ciljeva",
                        style = TextStyle(fontSize = 20.sp)

                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Button(
                        onClick = { showCompleted = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!showCompleted) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Aktivni ciljevi")
                    }
                    Button(
                        onClick = { showCompleted = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (showCompleted) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Završeni ciljevi")
                    }
                }

                LazyColumn {
                    items(filteredGoals) { goal ->
                        GoalItem(
                            goal = goal,
                            onAddProgress = { amount ->
                                viewModel.updateGoalProgress(goal.id, amount)
                            },
                            onEdit = { toEdit ->
                                navigation.navigate("GoalsEdit/${toEdit.id}")
                            },
                            onDelete = { toDelete ->
                                viewModel.deleteFinancialGoal(toDelete.id)
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = { navigation.navigate(Routes.SCREEN_GOALS_ADD) },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(90.dp)
                .padding(16.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "Dodaj novi rashod"
            )
        }
    }
}

@Composable
fun GoalItem(
    goal: FinancialGoal,
    onAddProgress: (Double) -> Unit,
    onEdit: (FinancialGoal) -> Unit,
    onDelete: (FinancialGoal) -> Unit
) {
    var input by remember { mutableStateOf("") }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(goal.title, fontSize = 18.sp)
            Text("Cilj: $${goal.targetAmount}")
            Text("Ušteđeno: $${goal.currentAmount}")

            val progress = (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0)

            LinearProgressIndicator(
                progress = { progress.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color.Green,
                trackColor = Color.Gray.copy(alpha = 0.3f),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Količina") },
                    modifier = Modifier
                        .weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                Button(
                    onClick = {
                        val amount = input.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            onAddProgress(amount)
                            input = ""
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                ) {
                    Text("Dodaj")
                }
            }

            Spacer(
                modifier = Modifier
                    .height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onEdit(goal) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Uredi")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onDelete(goal) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Obriši")
                }
            }
        }
    }
}