package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.data.Income
import and.drew.nkhukumanagement.utils.DateUtils
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Represents the UI state for [AddIncomeScreen]
 */
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

/**
 * Extension function to convert [IncomeUiState] to [Income]
 */
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

/**
 * Extension function to convert [Income] to [IncomeUiState]
 */
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

/**
 * Calculate total income earned
 */
fun calculateTotalIncome(quantity: String, pricePerItem: String): Double {
    return try {
        if (quantity.isEmpty()) 0.0 else quantity.toInt() *
                if (pricePerItem.isEmpty()) 0.0 else pricePerItem.toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
}

/**
 * Add the [calculateTotalIncome] to the initial income
 */
fun calculateCumulativeIncome(initialIncome: String, totalIncome: String): Double {
    return initialIncome.toDouble() + totalIncome.toDouble()
}

/**
 * Used to update the AccountsSummary and Income when an income is being updated.
 * Add the updated income earned to the current cumulative income, then subtract the initial item Income being updated
 */
fun calculateCumulativeIncomeUpdate(
    initialIncome: String,
    totalIncome: String,
    initialItemIncome: String
): Double {
    return (initialIncome.toDouble() + totalIncome.toDouble()) - initialItemIncome.toDouble()
}

/**
 * Check if entry is valid
 */
@RequiresApi(Build.VERSION_CODES.O)
fun IncomeUiState.isValid(): Boolean {
    return getDate().isNotBlank() &&
            incomeName.isNotBlank() &&
            quantity.isNotBlank() &&
            pricePerItem.isNotBlank()
}

/**
 * Handle [NumberFormatException]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun handleNumberExceptions(incomeUiState: IncomeUiState): Boolean {
    return try {
        incomeUiState.toIncome()
        true
    } catch (e: NumberFormatException) {
        false
    }
}