package com.example.portfoliomadeeasy.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.portfoliomadeeasy.viewmodel.AssetsViewModel
import com.example.portfoliomadeeasy.viewmodel.AuthViewModel
import com.example.portfoliomadeeasy.viewmodel.ExpenseGoalsViewModel

object Routes{
    const val SCREEN_REGISTER = "Registration"
    const val SCREEN_LOGIN = "LoginScreen"

    const val SCREEN_HOME = "HomeScreen"

    const val SCREEN_ASSETS_LIST = "AssetsScreen"
    const val SCREEN_PORTFOLIO = "Portfolio"

    const val SCREEN_EXPENSES_LIST = "RecentExpenses"
    const val SCREEN_EXPENSES_ADD = "AddExpenseScreen"
    const val SCREEN_EXPENSE_EDIT = "ExpensesEdit/{expenseId}"

    const val SCREEN_INCOME_LIST = "IncomeScreen"
    const val SCREEN_INCOME_ADD = "AddIncomeScreen"
    const val SCREEN_INCOME_EDIT = "IncomeEdit/{incomeId}"

    const val SCREEN_GOALS_LIST = "GoalsScreen"
    const val SCREEN_GOALS_ADD = "AddGoalsScreen"
    const val SCREEN_GOALS_EDIT = "GoalsEdit/{goalId}"

    const val SCREEN_STATS = "StatsScreen"
}

@Composable
fun NavigationController(
    expenseGoalsViewModel: ExpenseGoalsViewModel,
    assetsViewModel: AssetsViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val startDestination = if (authViewModel.currentUser.value != null) Routes.SCREEN_HOME else Routes.SCREEN_LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.SCREEN_REGISTER){
            Registration(
                navController,
                authViewModel
            )
        }

        composable(Routes.SCREEN_LOGIN){
            LoginScreen(
                navController,
                authViewModel,
                assetsViewModel,
                expenseGoalsViewModel,
            )
        }

        composable(Routes.SCREEN_HOME) {
            HomeScreen(
                navController,
                expenseGoalsViewModel,
                authViewModel
            )
        }

        composable(Routes.SCREEN_ASSETS_LIST) {
            AssetsScreen(
                navController,
                assetsViewModel
            )
        }

        composable(Routes.SCREEN_PORTFOLIO){
            Portfolio(
                assetsViewModel
            )
        }

        composable(Routes.SCREEN_EXPENSES_LIST) {
            Expenses(
                navController,
                viewModel = expenseGoalsViewModel
            )
        }

        composable(Routes.SCREEN_EXPENSES_ADD) {
            AddExpenseScreen(
                navController,
                expenseGoalsViewModel,
                authViewModel
            )
        }


        composable(
            Routes.SCREEN_EXPENSE_EDIT,
            arguments = listOf(
                navArgument("expenseId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("expenseId")?.let { expenseId ->
                ExpensesEdit(
                    expense = expenseGoalsViewModel.expenseData.first { it.id == expenseId },
                    onSave = { updatedExpense ->
                        expenseGoalsViewModel.updateExpense(updatedExpense)
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Routes.SCREEN_INCOME_LIST) {
            IncomeScreen(
                expenseGoalsViewModel,
                navController
            )
        }

        composable(Routes.SCREEN_INCOME_ADD) {
            AddIncomeScreen(
                expenseGoalsViewModel,
                authViewModel,
                navController
            )
        }

        composable(
            Routes.SCREEN_INCOME_EDIT,
            arguments = listOf(
                navArgument("incomeId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("incomeId")?.let { incomeId ->
                IncomeEdit(
                    income = expenseGoalsViewModel.incomeData.first { it.id == incomeId },
                    onSave = { updatedIncome ->
                        expenseGoalsViewModel.updateIncome(updatedIncome)
                        navController.popBackStack()
                    }
                )
            }

        }

        composable(Routes.SCREEN_GOALS_LIST) {
            GoalsScreen(
                expenseGoalsViewModel,
                navController
            )
        }

        composable(Routes.SCREEN_GOALS_ADD) {
            AddGoalScreen(
                expenseGoalsViewModel,
                authViewModel,
                navController
            )
        }

        composable(
            Routes.SCREEN_GOALS_EDIT,
            arguments = listOf(
                navArgument("goalId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("goalId")?.let { goalId ->
                GoalEdit(
                    goal = expenseGoalsViewModel.financialGoalData.first { it.id == goalId },
                    onSave = { updatedGoal ->
                        expenseGoalsViewModel.updateFinancialGoal(updatedGoal)
                        navController.popBackStack()
                    },
                    navigation = navController
                )
            }

        }

        composable(Routes.SCREEN_STATS) {
            StatsScreen(
                expenseGoalsViewModel
            )
        }
    }
}