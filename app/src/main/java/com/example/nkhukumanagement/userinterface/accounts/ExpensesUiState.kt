package com.example.nkhukumanagement.userinterface.accounts

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Expense
import com.example.nkhukumanagement.utils.DateUtils

/**
 * Represents the UI state for [AddExpenseScreen]
 */
data class ExpensesUiState(
    val id: Int = 0,
    val flockUniqueID: String = "",
    private var date: String = "",
    val expenseName: String = "",
    val supplier: String = "",
    val costPerItem: String = "",
    val quantity: String = "",
    val initialItemExpense: String = "0",
    val totalExpense: String = calculateTotalExpense(quantity, costPerItem).toString(),
    val cumulativeTotalExpense: String = "0",
    val notes: String = "",
    val isEnabled: Boolean = false
) {
    fun setDate(newDate: String) {
        date = newDate
    }

    fun getDate(): String {
        return date
    }
}

/**
 * Extension function to convert [ExpensesUiState] to [Expense]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun ExpensesUiState.toExpense(): Expense = Expense(
    id = id,
    flockUniqueID = flockUniqueID,
    date = DateUtils().stringToLocalDateShortFormat(getDate()),
    expenseName = expenseName,
    supplier = supplier,
    costPerItem = costPerItem.toDouble(),
    quantity = quantity.toInt(),
    totalExpense = calculateTotalExpense(quantity, costPerItem),
    cumulativeTotalExpense = calculateCumulativeExpense(cumulativeTotalExpense, totalExpense),
    notes = notes
)

/**
 * Extension function to convert [Expense] to [ExpensesUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Expense.toExpenseUiState(enabled: Boolean = false): ExpensesUiState = ExpensesUiState(
    id = id,
    flockUniqueID = flockUniqueID,
    date = DateUtils().dateToStringShortFormat(date),
    expenseName = expenseName,
    supplier = supplier,
    costPerItem = costPerItem.toString(),
    quantity = quantity.toString(),
    initialItemExpense = totalExpense.toString(),
    totalExpense = totalExpense.toString(),
    cumulativeTotalExpense = cumulativeTotalExpense.toString(),
    notes = notes,
    isEnabled = enabled
)

/**
 * Calculate total expense incurred
 */
fun calculateTotalExpense(quantity: String, pricePerItem: String): Double {
    return try {
        if (quantity.isEmpty()) 0.0 else quantity.toInt() *
                if (pricePerItem.isEmpty()) 0.0 else pricePerItem.toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
}

/**
 * Add the [calculateTotalExpense] to the initial expense
 */
fun calculateCumulativeExpense(initialExpense: String, totalExpense: String): Double {
    return initialExpense.toDouble() + totalExpense.toDouble()
}

/**
 * Used to update the AccountsSummary and Expense when an expense is being updated.
 * Add the new expense to the current cumulative expense, then subtract the initial item expense being updated
 */
fun calculateCumulativeExpenseUpdate(
    initialExpense: String,
    totalExpense: String,
    initialItemExpense: String
): Double {
    return (initialExpense.toDouble() + totalExpense.toDouble()) - initialItemExpense.toDouble()
}

/**
 * Check if entry is valid
 */
@RequiresApi(Build.VERSION_CODES.O)
fun ExpensesUiState.isValid(): Boolean {
    return getDate().isNotBlank() &&
            expenseName.isNotBlank() &&
            quantity.isNotBlank() &&
            costPerItem.isNotBlank()
}

/**
 * Handle [NumberFormatException]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun handleNumberExceptions(expensesUiState: ExpensesUiState): Boolean {
    return try {
        expensesUiState.toExpense()
        true
    } catch (e: NumberFormatException) {
        false
    }
}
