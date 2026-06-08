package com.example.portfoliomadeeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.portfoliomadeeasy.repository.AssetsRepository
import com.example.portfoliomadeeasy.view.NavigationController
import com.example.portfoliomadeeasy.viewmodel.AssetsViewModel
import com.example.portfoliomadeeasy.viewmodel.AssetsViewModelFactory
import com.example.portfoliomadeeasy.viewmodel.AuthViewModel
import com.example.portfoliomadeeasy.viewmodel.ExpenseGoalsViewModel
import kotlin.getValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val expensesViewModel by viewModels<ExpenseGoalsViewModel>()

        val repository = AssetsRepository(
            finnhubKey = "d8j9oehr01qgth6j8aa0d8j9oehr01qgth6j8aag",
            metalsKey = "WXDYFYU7JVOOKY5KJSBU4865KJSBU"
        )

        val assetsViewModel by viewModels<AssetsViewModel> {
            AssetsViewModelFactory(repository, expensesViewModel)
        }

        val authViewModel by viewModels<AuthViewModel>()

        setContent {
            NavigationController(expensesViewModel, assetsViewModel, authViewModel)
        }
    }
}
