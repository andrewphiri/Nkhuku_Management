package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Income
import com.example.nkhukumanagement.userinterface.flock.FlockUiState
import com.example.nkhukumanagement.utils.DateUtils
import java.time.LocalDate

data class IncomeUiState(
    val id: Int = 0,
    val flockUniqueID: String = "",
    val date: String = "date",
    val incomeName: String = "",
    val pricePerItem: String = "",
    val quantity: String = "",
    val totalIncome: String = "",
    val cumulativeTotalIncome: String = ""
)

@RequiresApi(Build.VERSION_CODES.O)
fun IncomeUiState.toIncome() : Income = Income(
    id = id,
    flockUniqueID = flockUniqueID,
    date = DateUtils().stringToLocalDate(date),
    incomeName = incomeName,
    pricePerItem= pricePerItem.toDouble(),
    quantity = quantity.toDouble(),
    totalIncome = calculateTotalIncome(quantity, pricePerItem),
    cumulativeTotalIncome = calculateCumulativeIncome(cumulativeTotalIncome, totalIncome),
)

@RequiresApi(Build.VERSION_CODES.O)
fun Income.toIncomeUiState(): IncomeUiState = IncomeUiState(
    id = id,
    flockUniqueID = flockUniqueID,
    date = DateUtils().convertLocalDateToString(date),
    incomeName = incomeName,
    pricePerItem = pricePerItem.toString(),
    quantity = quantity.toString(),
    totalIncome = totalIncome.toString(),
    cumulativeTotalIncome = cumulativeTotalIncome.toString()
)

fun calculateTotalIncome(quantity: String, pricePerItem: String): Double {
    return quantity.toDouble() * pricePerItem.toDouble()
}

fun calculateCumulativeIncome(initialIncome: String, totalIncome: String): Double {
    return initialIncome.toDouble() + totalIncome.toDouble()
}

@RequiresApi(Build.VERSION_CODES.O)
fun IncomeUiState.isValid(): Boolean {
    return date.isNotBlank() &&
            incomeName.isNotBlank() &&
            quantity.isNotBlank() &&
            pricePerItem.isNotBlank()
}