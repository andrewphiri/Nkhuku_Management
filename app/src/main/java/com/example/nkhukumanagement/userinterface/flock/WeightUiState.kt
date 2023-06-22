package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Weight
import com.example.nkhukumanagement.utils.DateUtils


data class WeightUiState(
    val id: Int = 0,
    val flockUniqueID: String = "",
    val week: String = "",
    val weight: Double = 0.0,
    private var dateMeasured: String = ""
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
    uniqueID = flockUniqueID,
    week = week,
    weight = weight,
    measuredDate = DateUtils().stringToLocalDate(getDate())
)

/**
 * Extension function to convert [Weight] to [WeightUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Weight.toFeedUiState(): WeightUiState = WeightUiState(
    id = id,
    flockUniqueID = uniqueID,
    week = week,
    weight = weight,
    dateMeasured = DateUtils().convertLocalDateToString(measuredDate)
)
