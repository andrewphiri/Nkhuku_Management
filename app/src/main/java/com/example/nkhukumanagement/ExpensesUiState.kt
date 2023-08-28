package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Expense
import com.example.nkhukumanagement.utils.DateUtils

data class ExpensesUiState(
    val id: Int = 0,
    val flockUniqueID: String = "",
    val date: String = "",
    val expenseName: String = "",
    val costPerItem: String = "",
    val quantity: String = "",
    val totalExpense: String = "",
    val cumulativeTotalExpense: String = ""
)

@RequiresApi(Build.VERSION_CODES.O)
fun ExpensesUiState.toExpense() : Expense = Expense(
    id = id,
    flockUniqueID = flockUniqueID,
    date = DateUtils().stringToLocalDate(date),
    expenseName = expenseName,
    costPerItem= costPerItem.toDouble(),
    quantity = quantity.toDouble(),
    totalExpense = calculateTotalExpense(quantity, costPerItem),
    cumulativeTotalExpense = calculateCumulativeExpense(cumulativeTotalExpense, totalExpense),
)

@RequiresApi(Build.VERSION_CODES.O)
fun Expense.toExpenseUiState(): ExpensesUiState = ExpensesUiState(
    id = id,
    flockUniqueID = flockUniqueID,
    date = DateUtils().convertLocalDateToString(date),
    expenseName = expenseName,
    costPerItem = costPerItem.toString(),
    quantity = quantity.toString(),
    totalExpense = totalExpense.toString(),
    cumulativeTotalExpense = cumulativeTotalExpense.toString()
)

fun calculateTotalExpense(quantity: String, pricePerItem: String): Double {
    return quantity.toDouble() * pricePerItem.toDouble()
}

fun calculateCumulativeExpense(initialExpense: String, totalExpense: String): Double {
    return initialExpense.toDouble() + totalExpense.toDouble()
}

@RequiresApi(Build.VERSION_CODES.O)
fun ExpensesUiState.isValid(): Boolean {
    return date.isNotBlank() &&
            expenseName.isNotBlank() &&
            quantity.isNotBlank() &&
            costPerItem.isNotBlank()
}
