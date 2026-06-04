package com.example.portfoliomadeeasy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.portfoliomadeeasy.repository.AssetsRepository

class AssetsViewModelFactory(
    private val repository: AssetsRepository,
    private val expenseViewModel: ExpenseGoalsViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssetsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssetsViewModel(repository, expenseViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}