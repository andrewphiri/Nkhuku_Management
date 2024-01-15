package and.drew.nkhukumanagement.userinterface.feed

import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.FlockWithFeed
import and.drew.nkhukumanagement.userinterface.flock.FlockUiState
import and.drew.nkhukumanagement.utils.DateUtils
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * ViewModel to insert, retrieve and update an item from the [FlockRepository]'s data source.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository
) : ViewModel() {

    companion object {
        private const val MILLIS = 5_000L
    }

    /**
     * Holds current feed ui state
     */
    var feedUiState by mutableStateOf(FeedUiState())
        private set

    private var feedList: SnapshotStateList<FeedUiState> = mutableStateListOf()

    private val flockId: Int = savedStateHandle[FeedScreenDestination.flockIdArg] ?: 0

    @OptIn(ExperimentalCoroutinesApi::class)
    val flockWithFeed: Flow<FlockWithFeed> =
        savedStateHandle.getStateFlow(key = FeedScreenDestination.flockIdArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getAllFlocksWithFeed(it)
            }

//        flockRepository.getAllFlocksWithFeed(flockId)
//            .map { it }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(MILLIS),
//                initialValue = FlockWithFeed(flock = null, feedList = listOf())
//            )

    fun setFlockID(id: Int) {
        savedStateHandle[FeedScreenDestination.flockIdArg] = id
    }

    fun setFeedState(feedState: FeedUiState) {
        feedUiState = feedState
    }

    /**
     * Update Feed Item from the list at the specified index
     */
    fun updateFeedState(index: Int, uiState: FeedUiState) {
        feedList[index] = uiState
    }

    /**
     * Insert the Feed Item into the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveFeed(feedUiState: FeedUiState) {
        flockRepository.insertFeed(feedUiState.toFeed())
    }

    /**
     * Deletes the Feed item from the database
     */
    suspend fun deleteFeed(flockUniqueID: String) {
        flockRepository.deleteFeed(flockUniqueID)
    }

    /**
     * Update the Feed Item in the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateFeed(feedUiState: FeedUiState) {
        flockRepository.updateFeed(feedUiState.toFeed())
    }

    /**
     * Get the feedList
     */
    fun getFeedList(): SnapshotStateList<FeedUiState> {
        return feedList
    }

    /**
     * Set the value of the feed List
     */
    fun setFeedList(feed: SnapshotStateList<FeedUiState>) {
        feedList = feed
    }

    /**
     * Default/Standard weekly feed program for broilers. This is the initial list inserted into the database
     * and set to [feedList]. Retrieved in [FeedScreen]
     */
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