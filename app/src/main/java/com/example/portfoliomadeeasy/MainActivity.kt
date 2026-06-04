package com.example.portfoliomadeeasy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.portfoliomadeeasy.repository.AssetsRepository
import com.example.portfoliomadeeasy.ui.theme.PortfolioMadeEasyTheme
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
            finnhubKey = "d2nmchhr01qs6r4cjgd0d2nmchhr01qs6r4cjgdg",
            metalsKey = "YR7YA0NHTCULB6S406CR946S406CR"
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
