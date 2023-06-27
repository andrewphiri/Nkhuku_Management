package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Weight
import com.example.nkhukumanagement.utils.DateUtils


data class WeightUiState(
    val id: Int = 0,
    val flockUniqueID: String = "",
    val week: String = "",
    val weight: String = "",
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

/**
 * Extension function to convert [WeightUiState] to [Weight]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun WeightUiState.toWeight(): Weight = Weight (
    id = id,
    flockUniqueId = flockUniqueID,
    week = week,
    weight = weight.toDouble(),
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
    weight = weight.toString(),
    dateMeasured = DateUtils().convertLocalDateToString(measuredDate)
)

fun WeightUiState.isValid() : Boolean {
    return  week.isNotBlank() &&
            getDate().isNotBlank() &&
            weight.isNotBlank()
}

fun WeightUiState.isSingleEntryValid(value: String): Boolean {
    return value.isBlank()
}
