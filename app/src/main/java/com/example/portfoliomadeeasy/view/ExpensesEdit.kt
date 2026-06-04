package com.example.portfoliomadeeasy.view

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.portfoliomadeeasy.model.Expense
import com.example.portfoliomadeeasy.viewmodel.ExpenseGoalsViewModel
import com.example.portfoliomadeeasy.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ExpensesEdit(
    expense: Expense,
    onSave: (Expense) -> Unit,
    navigation: NavController,
    viewModel: ExpenseGoalsViewModel
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(expense.title) }
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var selectedCategory by remember { mutableStateOf(expense.categoryname) }
    var selectedDate by remember { mutableStateOf(expense.date) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = calendar.time
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Uredite rashod",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Naziv rashoda") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Količina") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Kategorija", style = MaterialTheme.typography.bodyLarge)
        CategoryDropdown(selectedCategory) { selectedCategory = it }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Datum: ${dateFormat.format(selectedDate)}", modifier = Modifier.weight(1f))
            Icon(painter = painterResource(R.drawable.ic_calendar), contentDescription = "Odaberite datum")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isBlank() || amount.isBlank()) {
                    Toast.makeText(context, "Ispunite sva polja", Toast.LENGTH_SHORT).show()
                } else {
                    val updatedExpense = expense.copy(
                        title = title,
                        amount = amount.toDouble(),
                        date = selectedDate,
                        category = selectedCategory.name,
                    )
                    onSave(updatedExpense)
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Spremite promjene")
        }
    }
}