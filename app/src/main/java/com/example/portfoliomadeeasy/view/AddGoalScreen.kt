package com.example.portfoliomadeeasy.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.portfoliomadeeasy.model.FinancialGoal
import com.example.portfoliomadeeasy.viewmodel.AuthViewModel
import com.example.portfoliomadeeasy.viewmodel.ExpenseGoalsViewModel

@Composable
fun AddGoalScreen(
    viewModel: ExpenseGoalsViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    var title by remember { mutableStateOf("") }
    var targetAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Dodaj novi cilj",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Naziv cilja") },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = targetAmount,
            onValueChange = { targetAmount = it },
            label = { Text("Ciljana količina") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val target = targetAmount.toDoubleOrNull()
                if (title.isNotBlank() && target != null) {
                    val newGoal = authViewModel.currentUser.value?.let {
                        FinancialGoal(
                            title = title,
                            targetAmount = target,
                            currentAmount = 0.0,
                            completed = false,
                            userId = it.uid
                        )
                    }

                    if (newGoal != null) {
                        viewModel.addFinancialGoal(newGoal)
                    }
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Spremi cilj")
        }
    }
}
