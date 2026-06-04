package com.example.portfoliomadeeasy.model

import java.util.Date
import com.example.portfoliomadeeasy.view.Category

data class Expense(
    var id: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val date: Date = Date(),
    val category: String = Category.FOOD.name,
    val userId: String = ""
){
    val categoryname : Category
        get() = Category.valueOf(category)
}