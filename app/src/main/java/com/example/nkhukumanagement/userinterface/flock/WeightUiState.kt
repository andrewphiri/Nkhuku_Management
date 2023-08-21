package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Weight
import com.example.nkhukumanagement.utils.DateUtils


data class WeightUiState(
    val id: Int = 0,
    val flockUniqueID: String = "",
    val week: String = "",
    val actualWeight: String = "",
    val standard: String = "",
    private var dateMeasured: String = "",
    val enabled: Boolean = false
) {
    fun setDate(date: String) {
        dateMeasured = date
    }

    fun getDate(): String {
        return dateMeasured
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun checkNumberExceptions(weightUiState: WeightUiState): Boolean {
    return try {
        weightUiState.toWeight()
        true
    }catch (e: NumberFormatException) {
        false
    }
}
/**
 * Extension function to convert [WeightUiState] to [Weight]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun WeightUiState.toWeight(): Weight = Weight(
    id = id,
    flockUniqueId = flockUniqueID,
    week = week,
    weight = actualWeight.toDouble(),
    expectedWeight = standard.toDouble(),
    measuredDate = DateUtils().stringToLocalDate(getDate())
)

/**
 * Extension function to convert [Weight] to [WeightUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Weight.toWeightUiState(): WeightUiState = WeightUiState(
    id = id,
    flockUniqueID = flockUniqueId,
    week = week,
    actualWeight = weight.toString(),
    standard = expectedWeight.toString(),
    dateMeasured = DateUtils().convertLocalDateToString(measuredDate)
)

fun WeightUiState.isValid(): Boolean {
    return week.isNotBlank() &&
            getDate().isNotBlank() &&
            actualWeight.isNotBlank()
}

fun WeightUiState.isSingleEntryValid(value: String): Boolean {
    return value.isBlank()
}
