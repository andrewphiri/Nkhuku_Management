package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.FeedUiState
import com.example.nkhukumanagement.data.Feed
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.data.FlockWithFeed
import com.example.nkhukumanagement.toFeed
import com.example.nkhukumanagement.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository
) : ViewModel() {

    companion object {
        private const val MILLIS = 5_000L
    }

    var feedUiState by mutableStateOf(FeedUiState())
        private set
    private var feedList: SnapshotStateList<FeedUiState> = mutableStateListOf()

    private val flockId: Int = savedStateHandle[FeedScreenDestination.flockIdArg] ?: 0

    val flockWithFeed: StateFlow<FlockWithFeed> =
        flockRepository.getAllFlocksWithFeed(flockId)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = FlockWithFeed(flock = null, feedList = listOf())
            )


    fun setFeedState(feedState: FeedUiState) {
        feedUiState = feedState
    }

    fun updateFeedState(index: Int, uiState: FeedUiState) {
        feedList[index] = uiState
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveFeed(feedUiState: FeedUiState) {
        flockRepository.insertFeed(feedUiState.toFeed())
    }

    suspend fun deleteFeed(flockUniqueID: String) {
        flockRepository.deleteFeed(flockUniqueID)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateFeed(feedUiState: FeedUiState) {
        flockRepository.updateFeed(feedUiState.toFeed())
    }

    fun getFeedList(): SnapshotStateList<FeedUiState> {
        return feedList
    }

    fun setFeedList(feed: SnapshotStateList<FeedUiState>) {
        feedList = feed
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultFeedInformationList(flockUiState: FlockUiState): SnapshotStateList<FeedUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = "Week 1",
                standardConsumption =
                String.format(
                    "%.3f", 0.167 *
                            flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.167",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 7,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = "Week 2",
                standardConsumption = String.format(
                    "%.3f",
                    0.375 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.375",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 14,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = "Week 3",
                standardConsumption = String.format(
                    "%.3f",
                    0.60 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.60",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 21,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = "Week 4",
                standardConsumption = String.format(
                    "%.3f",
                    0.845 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.845",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 28,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = "Week 5",
                standardConsumption = String.format(
                    "%.3f",
                    1.015 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "1.015",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 35,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = "Week 6",
                standardConsumption = String.format(
                    "%.3f",
                    1.234 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "1.234",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 42,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = "Week 7",
                standardConsumption = String.format(
                    "%.3f",
                    1.393 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "1.393",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 49,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = "Week 8",
                standardConsumption = String.format(
                    "%.3f",
                    1.491 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "1.491",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 56,
                    feedUiState = feedUiState
                )
            )
        )
    }
}