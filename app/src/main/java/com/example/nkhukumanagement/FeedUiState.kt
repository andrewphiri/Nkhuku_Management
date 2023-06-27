package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Feed
import com.example.nkhukumanagement.userinterface.flock.WeightUiState
import com.example.nkhukumanagement.utils.DateUtils

data class FeedUiState(
    val id: Int = 0,
    val flockUniqueID: String = "",
    val name: String = "",
    val type: String = "",
    val consumed: String = "",
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
    type = type,
    consumed = consumed.toDouble(),
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
    type = type,
    consumed = consumed.toString(),
    feedingDate = DateUtils().convertLocalDateToString(feedingDate)
)

fun FeedUiState.isValid() : Boolean {
    return  name.isNotBlank() &&
            getDate().isNotBlank() &&
            type.isNotBlank() &&
            consumed.isNotBlank()
}

fun FeedUiState.isSingleEntryValid(value: String): Boolean {
    return value.isBlank()
}