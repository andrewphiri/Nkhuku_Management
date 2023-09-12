package com.example.nkhukumanagement.userinterface.feed

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Feed
import com.example.nkhukumanagement.utils.DateUtils

/**
 * Represents the UI state for [FeedScreen].
 */
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
    val options = mutableListOf("Pre-starter", "Starter", "Grower", "Finisher")

    fun setDate(date: String) {
        feedingDate = date
    }

    fun getDate(): String {
        return feedingDate
    }
}

/**
 * Handle [NumberFormatException]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun checkNumberExceptions(feedUiState: FeedUiState): Boolean {
    return try {
        feedUiState.toFeed()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

/**
 * Extension function to convert [FeedUiState] to [Feed]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun FeedUiState.toFeed(): Feed = Feed(
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
    feedingDate = DateUtils().dateToStringLongFormat(feedingDate)
)

/**
 * Check if entry is valid
 */
fun FeedUiState.isValid(): Boolean {
    return name.isNotBlank() &&
            getDate().isNotBlank() &&
            type.isNotBlank() &&
            actualConsumed.isNotBlank()
}

fun FeedUiState.isSingleEntryValid(value: String): Boolean {
    return value.isNotBlank()
}