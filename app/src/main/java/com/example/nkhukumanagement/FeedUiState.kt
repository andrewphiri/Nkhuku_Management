package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.nkhukumanagement.data.Feed
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.utils.DateUtils

data class FeedUiState(
    val id: Int = 0,
    val flockUniqueID: String = "",
    val name: String = "",
    val type: String = "",
    val consumed: Double = 0.0,
    private var feedingDate: String = ""
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
    uniqueID = flockUniqueID,
    name = name,
    type = type,
    consumed = consumed,
    feedingDate = DateUtils().stringToLocalDate(getDate())
        )

/**
 * Extension function to convert [Feed] to [FeedUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Feed.toFeedUiState(): FeedUiState = FeedUiState(
    id = id,
    flockUniqueID = uniqueID,
    name = name,
    type = type,
    consumed = consumed,
    feedingDate = DateUtils().convertLocalDateToString(feedingDate)
)