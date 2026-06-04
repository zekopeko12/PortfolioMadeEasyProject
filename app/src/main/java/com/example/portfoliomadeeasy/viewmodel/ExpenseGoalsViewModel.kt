package com.example.portfoliomadeeasy.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.portfoliomadeeasy.model.Expense
import com.example.portfoliomadeeasy.model.FinancialGoal
import com.example.portfoliomadeeasy.model.Income
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.time.LocalDate

class ExpenseGoalsViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    private val uid: String
        get() = auth.currentUser?.uid ?: ""

    val expenseData = mutableStateListOf<Expense>()
    val incomeData = mutableStateListOf<Income>()
    val financialGoalData = mutableStateListOf<FinancialGoal>()

    init{
        fetchDatabaseData()
    }

    private fun fetchDatabaseData() {
        db.collection("expenses")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { result ->
                for(data in result.documents){
                    val expense = data.toObject(Expense::class.java)

                    if(expense != null){
                        expense.id = data.id
                        expenseData.add(expense)
                    }
                }
            }

        db.collection("income")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { result ->
                for(data in result.documents) {
                    val income = data.toObject(Income::class.java)

                    if(income != null){
                        income.id = data.id
                        incomeData.add(income)
                    }
                }
            }

        db.collection("financialGoal")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { result ->
                for(data in result.documents) {
                    val goal = data.toObject(FinancialGoal::class.java)

                    if(goal != null){
                        goal.id = data.id
                        financialGoalData.add(goal)
                    }
                }
            }
    }

    fun addExpense(expense: Expense){
        val currentUid = auth.currentUser?.uid ?: return
        val expenseWithUid = expense.copy(userId = currentUid)

        db.collection("expenses")
            .add(expenseWithUid)
            .addOnSuccessListener { newexpense ->
                val newExpense = expenseWithUid.copy(id = newexpense.id)
                expenseData.add(newExpense)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding expense", e)
            }
    }

    fun updateExpense(expense: Expense) {
        if (expense.id.isEmpty()) return

        val updatedData = mapOf(
            "title" to expense.title,
            "amount" to expense.amount,
            "date" to expense.date,
            "category" to expense.category,
            "userId" to uid
        )

        db.collection("expenses")
            .document(expense.id)
            .update(updatedData)
            .addOnSuccessListener {
                val index = expenseData.indexOfFirst { it.id == expense.id }
                if (index != -1) {
                    expenseData[index] = expense
                }
            }
    }

    fun deleteExpense(expenseId: String) {
        if (expenseId.isEmpty()) return

        db.collection("expenses")
            .document(expenseId)
            .get()
            .addOnSuccessListener { doc ->
                if(doc.getString("userId") == uid) {
                    db.collection("expenses")
                        .document(expenseId)
                        .delete()
                        .addOnSuccessListener {
                            expenseData.removeAll { it.id == expenseId }
                        }
                }
            }
    }

    fun addIncome(income: Income) {
        val currentUid = auth.currentUser?.uid ?: return
        val incomeWithUid = income.copy(userId = currentUid)
        db.collection("income")
            .add(incomeWithUid)
            .addOnSuccessListener { newincome ->
                val newIncome = incomeWithUid.copy(id = newincome.id)
                incomeData.add(newIncome)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding expense", e)
            }
    }

    fun updateIncome(income: Income) {
        if (income.id.isEmpty()) return

        val updatedData = mapOf(
            "title" to income.title,
            "amount" to income.amount,
            "date" to income.date,
            "userId" to uid
        )

        db.collection("income")
            .document(income.id)
            .update(updatedData)
            .addOnSuccessListener {
                val index = incomeData.indexOfFirst { it.id == income.id }
                if (index != -1) {
                    incomeData[index] = income
                }
            }
    }

    fun deleteIncome(incomeId: String) {
        if (incomeId.isEmpty()) return

        db.collection("income")
            .document(incomeId)
            .get()
            .addOnSuccessListener { doc ->
                if(doc.getString("userId") == uid) {
                    db.collection("income")
                        .document(incomeId)
                        .delete()
                        .addOnSuccessListener{
                            incomeData.removeAll { it.id == incomeId }
                        }
                }
            }
    }

    fun totalIncome(currentMonth: LocalDate): Double {
        return incomeData.filter { income ->
            val incomeDate = income.date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
            incomeDate.year == currentMonth.year && incomeDate.month == currentMonth.month
        }.sumOf { it.amount }
    }

    fun totalExpenses(currentMonth: LocalDate): Double {
        return expenseData.filter { expense ->
            val expenseDate = expense.date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
            expenseDate.year == currentMonth.year && expenseDate.month == currentMonth.month
        }.sumOf { it.amount }
    }

    fun addFinancialGoal(goal: FinancialGoal) {
        val currentUid = auth.currentUser?.uid ?: return
        val goalWithUid = goal.copy(userId = currentUid)

        db.collection("financialGoal")
            .add(goalWithUid)
            .addOnSuccessListener { newgoal ->
                val newGoal = goalWithUid.copy(id = newgoal.id)
                financialGoalData.add(newGoal)
            }
    }

    fun updateFinancialGoal(goal: FinancialGoal) {
        if (goal.id.isEmpty()) return

        val updatedData = mapOf(
            "title" to goal.title,
            "targetAmount" to goal.targetAmount,
            "currentAmount" to goal.currentAmount,
            "userId" to uid
        )

        db.collection("financialGoal")
            .document(goal.id)
            .update(updatedData)
            .addOnSuccessListener {
                val index = financialGoalData.indexOfFirst { it.id == goal.id }
                if (index != -1) {
                    financialGoalData[index] = goal
                }
            }
    }

    fun deleteFinancialGoal(financialGoalId: String) {
        if (financialGoalId.isEmpty()) return

        db.collection("financialGoal")
            .document(financialGoalId)
            .get()
            .addOnSuccessListener { doc ->
                if(doc.getString("userId") == uid){
                    db.collection("financialGoal")
                        .document(financialGoalId)
                        .delete()
                        .addOnSuccessListener {
                            financialGoalData.removeAll { it.id == financialGoalId }
                        }
                }
            }
    }

    fun updateGoalProgress(goalId: String, amount: Double) {
        val index = financialGoalData.indexOfFirst { it.id == goalId && it.userId == uid}
        if (index == -1) return

        val goal = financialGoalData[index]
        val newAmount = goal.currentAmount + amount
        val isCompleted = newAmount >= goal.targetAmount

        val updatedGoal = goal.copy(
            currentAmount = newAmount,
            completed = isCompleted
        )

        financialGoalData[index] = updatedGoal

        db.collection("financialGoal")
            .document(goalId)
            .update(
                mapOf(
                    "currentAmount" to updatedGoal.currentAmount,
                    "completed" to isCompleted,
                    "userId" to uid
                )
            )
    }
}