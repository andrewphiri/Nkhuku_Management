package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Feed
import com.example.nkhukumanagement.utils.DateUtils

data class FeedUiState(
    val id: Int = 0,
    val flockUniqueID: String = "",
    val name: String = "",
    val week: String = "",
    val type: String = "",
    val actualConsumed: String = "0.0",
    val standardConsumption: String = "0.0",
    val actualConsumptionPerBird: String = "0.0",
    val standardConsumptionPerBird: String = "0.0",
    private var feedingDate: String = "",
    val enabled: Boolean = false
) {

    fun setDate(date: String) {
        feedingDate = date
    }
    fun getDate(): String {
        return feedingDate
    }
}

/**
 * Extension function to convert [FeedUiState] to [Feed]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun FeedUiState.toFeed(): Feed = Feed (
    id = id,
    flockUniqueId = flockUniqueID,
    name = name,
    week = week,
    type = type,
    consumed = actualConsumed.toDouble(),
    standardConsumption = standardConsumption.toDouble(),
    actualConsumptionPerBird = actualConsumptionPerBird.toDouble(),
    standardConsumptionPerBird = standardConsumptionPerBird.toDouble(),
    feedingDate = DateUtils().stringToLocalDate(getDate())
        )

/**
 * Extension function to convert [Feed] to [FeedUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Feed.toFeedUiState(): FeedUiState = FeedUiState(
    id = id,
    flockUniqueID = flockUniqueId,
    name = name,
    week = week,
    type = type,
    actualConsumed = consumed.toString(),
    standardConsumption = standardConsumption.toString(),
    actualConsumptionPerBird = actualConsumptionPerBird.toString(),
    standardConsumptionPerBird = standardConsumptionPerBird.toString(),
    feedingDate = DateUtils().convertLocalDateToString(feedingDate)
)

fun FeedUiState.isValid() : Boolean {
    return  name.isNotBlank() &&
            getDate().isNotBlank() &&
            type.isNotBlank() &&
            actualConsumed.isNotBlank()
}

fun FeedUiState.isSingleEntryValid(value: String): Boolean {
    return value.isBlank()
}