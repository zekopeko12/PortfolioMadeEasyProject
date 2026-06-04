package com.example.portfoliomadeeasy.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.portfoliomadeeasy.model.Expense
import com.example.portfoliomadeeasy.viewmodel.AuthViewModel
import com.example.portfoliomadeeasy.viewmodel.ExpenseGoalsViewModel

@Composable
fun HomeScreen(
    navigation: NavController,
    expenseGoalsViewModel: ExpenseGoalsViewModel,
    authViewModel: AuthViewModel
) {
    val expenses = expenseGoalsViewModel.expenseData

    val recentExpenses = expenses.sortedByDescending { it.date }.take(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
    )
    {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "Dobro došli!",
                fontSize = 30.sp
            )

            Button(
                onClick = {
                    authViewModel.logout()
                    navigation.navigate(Routes.SCREEN_LOGIN){
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) {
                Text("Odjavi se")
            }
        }

        Text(
            text = "Zadnja 3 rashoda: ",
            fontSize = 20.sp,
            modifier = Modifier
                .padding(10.dp)
        )

        RecentTransactionsList(recentExpenses)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ){
            ButtonGrid(
                navigation
            )
        }
    }
}

@Composable
fun RecentTransactionsList(expenses: List<Expense>) {
    LazyColumn {
        items(expenses) { expense ->
            RecentTransactionItem(expense)
        }
    }
}

@Composable
fun RecentTransactionItem(expense: Expense) {
    val category = expense.categoryname
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = category.iconResId),
            contentDescription = category.displayName,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = expense.title, fontSize = 18.sp)
            Text(text = category.displayName, fontSize = 14.sp)
        }

        Text(text = "$${expense.amount}", fontSize = 18.sp)
    }
}

@Composable
fun ButtonGrid(
    navigation: NavController
) {

    val buttons = listOf(
        "Pregledaj rashode" to Routes.SCREEN_EXPENSES_LIST,
        "Pregledaj prihode" to Routes.SCREEN_INCOME_LIST,
        "Pregledaj ciljeve" to Routes.SCREEN_GOALS_LIST,
        "Statistika" to Routes.SCREEN_STATS,
        "Pregledaj imovinu" to Routes.SCREEN_ASSETS_LIST
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        buttons.forEach{ (text, route) ->
            Button(
                onClick = { navigation.navigate(route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ){
                Text(
                    text = text,
                    fontSize = 16.sp
                )
            }
        }
    }
}