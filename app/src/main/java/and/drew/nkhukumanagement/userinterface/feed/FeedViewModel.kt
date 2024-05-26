package and.drew.nkhukumanagement.userinterface.feed

import and.drew.nkhukumanagement.BaseFlockApplication
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.Feed
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
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel to insert, retrieve and update an item from the [FlockRepository]'s data source.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    val application: BaseFlockApplication,
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val feed: Flow<Feed> =
        savedStateHandle.getStateFlow(key = FeedScreenDestination.feedIdArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getFeedItem(it)
            }

    fun setFlockID(id: Int) {
        savedStateHandle[FeedScreenDestination.flockIdArg] = id
    }

    fun setFeedID(id: Int) {
        savedStateHandle[FeedScreenDestination.feedIdArg] = id
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultFeedInformationList(
        flockUiState: FlockUiState
    ): SnapshotStateList<FeedUiState> {

        return when (flockUiState.flockType) {
            "Broiler" -> {
                defaultBroilerFeedList(
                    flockUiState = flockUiState
                )
            }
            "Layer" -> {
                defaultLayerFeed(
                    flockUiState = flockUiState
                )
            }
            "Village Chicken" -> {
                defaultHybridVillageFeedInformationList(
                    flockUiState = flockUiState
                )
            }
            else -> {
                defaultFeedInformationList(
                    flockUiState = flockUiState
                )
            }
        }
    }
    /**
     * Default layer vaccination dates based on breed and date chicks received
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultLayerFeed(
        flockUiState: FlockUiState
    ): SnapshotStateList<FeedUiState> {

        return when (flockUiState.layerType) {
            "Hybrid Zambro" -> {
                defaultHybridZambroLayerFeedInformationList(
                    flockUiState = flockUiState,
                )
            }
            else -> {
                defaultHybridBrownLayerFeedInformationList(
                    flockUiState = flockUiState
                )
            }
        }
    }



    /**
     * Default/Standard weekly feed program for broilers. This is the initial list inserted into the database
     * and set to [feedList]. Retrieved in [FeedScreen]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultBroilerFeedList(flockUiState: FlockUiState): SnapshotStateList<FeedUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_1),
                standardConsumption =
                String.format(
                    Locale.getDefault(),
                    "%.3f", 0.167 *
                            flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.167",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_2),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.375 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.375",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_3),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.60 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.60",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_4),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.845 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.845",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_5),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    1.015 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "1.015",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_6),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    1.234 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "1.234",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_7),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    1.393 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "1.393",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_8),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    1.491 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "1.491",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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

    /**
     * Default/Standard weekly feed program for  Hybrid Brown Layers. This is the initial list inserted into the database
     * and set to [feedList]. Retrieved in [FeedScreen]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultHybridBrownLayerFeedInformationList(flockUiState: FlockUiState): SnapshotStateList<FeedUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_1),
                standardConsumption =
                String.format(
                    Locale.getDefault(),
                    "%.3f", 0.011 *
                            flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.011",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_2),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.019 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.019",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_3),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.024 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.024",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_4),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.028 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.028",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_5),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.035 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.035",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_6),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.039 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.039",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_7),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.042 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.042",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_8),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.046 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.046",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 56,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_9),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.051 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.051",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 63,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_10),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.054 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.054",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 70,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_11),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.060 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.060",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 77,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_12),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.064 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.064",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 84,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_13),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.069 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.069",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 91,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_14),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.072 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.072",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 98,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_15),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.074 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.074",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 105,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_16),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.077 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.077",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 112,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_17),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.080 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.080",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 119,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_18),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.085 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.085",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 126,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_19),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.088 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.088",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 133,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_20_to_laying_stage),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.094 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.094",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 140,
                    feedUiState = feedUiState
                )
            )
        )
    }

    /**
     * Default/Standard weekly feed program for  Hybrid Zambro Layers. This is the initial list inserted into the database
     * and set to [feedList]. Retrieved in [FeedScreen]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultHybridZambroLayerFeedInformationList(flockUiState: FlockUiState): SnapshotStateList<FeedUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_1),
                standardConsumption =
                String.format(
                    Locale.getDefault(),
                    "%.3f", 0.013 *
                            flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.013",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_2),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.025 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.025",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_3),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.034 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.034",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_4),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.040 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.040",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_5),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.046 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.046",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_6),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.051 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.051",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_7),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.054 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.054",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_8),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.057 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.057",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 56,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_9),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.060 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.060",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 63,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_10),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.064 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.064",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 70,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_11),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.068 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.068",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 77,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_12),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.072 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.072",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 84,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_13),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.074 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.074",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 91,
                    feedUiState = feedUiState
                )
            )
        )
    }

    /**
     * Default/Standard weekly feed program for  Hybrid Zambro Layers. This is the initial list inserted into the database
     * and set to [feedList]. Retrieved in [FeedScreen]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultHybridVillageFeedInformationList(flockUiState: FlockUiState): SnapshotStateList<FeedUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_1),
                standardConsumption =
                String.format(
                    Locale.getDefault(),
                    "%.3f", 0.014 *
                            flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.014",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_2),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.022 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.022",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_3),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.032 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.032",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_4),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.042 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.042",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_5),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.053 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.053",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_6),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.057 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.057",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_7),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.062 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.062",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
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
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_8),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.066 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.066",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 56,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_9),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.069 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.069",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 63,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_10),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.070 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.070",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 70,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_11),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.072 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.072",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 77,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_12),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.074 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.074",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 84,
                    feedUiState = feedUiState
                )
            ),
            FeedUiState(
                flockUniqueID = flockUiState.getUniqueId(), week = application.applicationContext.getString(R.string.week_13),
                standardConsumption = String.format(
                    Locale.getDefault(),
                    "%.3f",
                    0.076 * flockUiState.getStock().toDouble()
                ),
                standardConsumptionPerBird = "0.076",
                actualConsumptionPerBird =
                String
                    .format(
                        Locale.getDefault(),
                        "%.3f",
                        feedUiState.actualConsumed.toDouble() / flockUiState.getStock().toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = dateReceived,
                    day = 91,
                    feedUiState = feedUiState
                )
            )
        )
    }
}