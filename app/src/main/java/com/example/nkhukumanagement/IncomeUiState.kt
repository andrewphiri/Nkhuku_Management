package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Income
import com.example.nkhukumanagement.utils.DateUtils

data class IncomeUiState(
    val id: Int = 0,
    val flockUniqueID: String = "",
    private var date: String = "date",
    val customer: String = "",
    val incomeName: String = "",
    val pricePerItem: String = "",
    val quantity: String = "",
    val initialItemIncome: String = "0",
    val totalIncome: String = "",
    val cumulativeTotalIncome: String = "0",
    val notes: String = "",
    val enabled: Boolean = false
) {
    fun setDate(newDate: String) {
        date = newDate
    }

    fun getDate(): String {
        return date
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun IncomeUiState.toIncome(): Income = Income(
    id = id,
    flockUniqueID = flockUniqueID,
    date = DateUtils().stringToLocalDateShortFormat(getDate()),
    incomeName = incomeName,
    customer = customer,
    pricePerItem = pricePerItem.toDouble(),
    quantity = quantity.toInt(),
    totalIncome = calculateTotalIncome(quantity, pricePerItem),
    cumulativeTotalIncome = calculateCumulativeIncome(cumulativeTotalIncome, totalIncome),
    notes = notes
)

@RequiresApi(Build.VERSION_CODES.O)
fun Income.toIncomeUiState(enabled: Boolean = false): IncomeUiState = IncomeUiState(
    id = id,
    flockUniqueID = flockUniqueID,
    date = DateUtils().dateToStringShortFormat(date),
    incomeName = incomeName,
    customer = customer,
    pricePerItem = pricePerItem.toString(),
    quantity = quantity.toString(),
    initialItemIncome = totalIncome.toString(),
    totalIncome = totalIncome.toString(),
    cumulativeTotalIncome = cumulativeTotalIncome.toString(),
    notes = notes,
    enabled = enabled
)

fun calculateTotalIncome(quantity: String, pricePerItem: String): Double {
    return try {
        if (quantity.isEmpty()) 0.0 else quantity.toInt() *
                if (pricePerItem.isEmpty()) 0.0 else pricePerItem.toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
}

fun calculateCumulativeIncome(initialIncome: String, totalIncome: String): Double {
    return initialIncome.toDouble() + totalIncome.toDouble()
}

fun calculateCumulativeIncomeUpdate(
    initialIncome: String,
    totalIncome: String,
    initialItemIncome: String
): Double {
    return (initialIncome.toDouble() + totalIncome.toDouble()) - initialItemIncome.toDouble()
}

@RequiresApi(Build.VERSION_CODES.O)
fun IncomeUiState.isValid(): Boolean {
    return getDate().isNotBlank() &&
            incomeName.isNotBlank() &&
            quantity.isNotBlank() &&
            pricePerItem.isNotBlank()
}

@RequiresApi(Build.VERSION_CODES.O)
fun handleNumberExceptions(incomeUiState: IncomeUiState): Boolean {
    return try {
        incomeUiState.toIncome()
        true
    } catch (e: NumberFormatException) {
        false
    }
}