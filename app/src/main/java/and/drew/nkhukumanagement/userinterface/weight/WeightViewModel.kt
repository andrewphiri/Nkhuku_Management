package and.drew.nkhukumanagement.userinterface.weight

import and.drew.nkhukumanagement.BaseFlockApplication
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.FlockWithWeight
import and.drew.nkhukumanagement.data.Weight
import and.drew.nkhukumanagement.userinterface.flock.FlockUiState
import and.drew.nkhukumanagement.utils.DateUtils
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * ViewModel to insert, retrieve, update and delete a weight item from the [FlockRepository]'s data source.
 */
@HiltViewModel
class WeightViewModel @Inject constructor(
    val application: BaseFlockApplication,
    val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository
) : ViewModel() {

    companion object {
        private const val MILLIS = 5_000L
    }

    /**
     * Holds the current Ui State
     */
    var weightUiState by mutableStateOf(WeightUiState())
        private set

    private var initialWeightList: SnapshotStateList<WeightUiState> = mutableStateListOf()

    private val flockID: Int = savedStateHandle[WeightScreenDestination.flockIdArg] ?: 0

    @OptIn(ExperimentalCoroutinesApi::class)
    val flockWithWeight: Flow<FlockWithWeight> =
        savedStateHandle.getStateFlow(key = WeightScreenDestination.flockIdArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getAllFlocksWithWeight(it)
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    val weight: Flow<Weight> =
        savedStateHandle.getStateFlow(key = WeightScreenDestination.weightIdArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getWeightItem(it)
            }
//        flockRepository.getAllFlocksWithWeight(flockID)
//            .map { it }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(MILLIS),
//                initialValue = FlockWithWeight(flock = null, weights = listOf())
//            )

    fun setFlockID(id: Int) {
        savedStateHandle[WeightScreenDestination.flockIdArg] = id
    }

    fun setWeightID(id: Int) {
        savedStateHandle[WeightScreenDestination.weightIdArg] = id
    }

    /**
     * Insert a weight item into the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveInitialWeight(weightUiState: WeightUiState) {
        flockRepository.insertWeight(weightUiState.toWeight())
    }

    /**
     * Update a weight item in the database
     */
    suspend fun updateWeight(weights: Weight) {
        flockRepository.updateWeight(weights)
    }

    /**
     * Deletes a weight item from the database
     */
    suspend fun deleteWeight(flockUniqueId: String) {
        flockRepository.deleteWeight(flockUniqueId)
    }

    /**
     * Get the weightlist
     */
    fun getWeightList(): SnapshotStateList<WeightUiState> {
        return initialWeightList
    }

    /**
     * Set the initial weightlist to [@param weightList]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setWeightList(weightList: SnapshotStateList<WeightUiState>) {
        initialWeightList = weightList
    }

    /**
     * Update the WeightUiState List at the specified index
     */
    fun updateWeightState(index: Int, uiState: WeightUiState) {
        initialWeightList[index] = uiState
    }
    fun setWeightState(weighState: WeightUiState) {
        weightUiState = weighState
    }


    /**
     * Default layer vaccination dates based on breed and date chicks received
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultWeights(
        flockUiState: FlockUiState
    ): SnapshotStateList<WeightUiState> {
        val context = application.applicationContext
        val flockTypeOptions = context.resources.getStringArray(R.array.types_of_flocks).toList()
        return when (flockUiState.flockType) {
            flockTypeOptions[0] -> {
                defaultWeightBroilers1(
                    flockUiState = flockUiState
                )
            }
            flockTypeOptions[1] -> {
                defaultLayersFeed(
                    flockUiState = flockUiState
                )
            }
            flockTypeOptions[2] -> {
                defaultWeightHybridVillageChicken(
                    flockUiState = flockUiState
                )
            }
            else -> {
                defaultWeights(
                    flockUiState = flockUiState
                )
            }
        }
    }

    /**
     * Default layer vaccination dates based on breed and date chicks received
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultLayersFeed(
        flockUiState: FlockUiState
    ): SnapshotStateList<WeightUiState> {

        return when (flockUiState.breed) {
            "Hybrid Zambro" -> {
                defaultWeightHybridZambroLayers(
                    flockUiState = flockUiState,
                )
            }
            else -> {
                defaultWeightHybridLayers(
                    flockUiState = flockUiState
                )
            }
        }
    }
    /**
     * Standard weekly weights for broilers
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultWeightBroilers1(flockUiState: FlockUiState): SnapshotStateList<WeightUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            WeightUiState(
                week = application.applicationContext.getString(R.string.initial),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.040",
                dateMeasured = flockUiState.getDate()
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_1),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.180",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 7,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_2),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.440",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 14,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_3),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.850",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 21,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_4),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.400",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 28,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_5),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "2.000",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 35,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_6),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "2.600",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 42,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_7),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "3.200",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 49,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_8),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "3.800",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 56,
                    weightUiState = weightUiState
                )
            )
        )
    }

    /**
     * Standard weekly weights for Hybrid Zambro layers
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultWeightHybridZambroLayers(flockUiState: FlockUiState): SnapshotStateList<WeightUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            WeightUiState(
                week = application.applicationContext.getString(R.string.initial),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.038",
                dateMeasured = flockUiState.getDate()
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_1),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.101",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 7,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_2),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.221",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 14,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_3),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.422",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 21,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_4),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.709",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 28,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_5),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.058",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 35,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_6),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.449",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 42,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_7),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.834",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 49,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_8),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "2.210",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 56,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_9),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "2.567",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 63,
                    weightUiState = weightUiState
                )
            ),
                WeightUiState(
                    week = application.applicationContext.getString(
                        R.string.week_10),
                    flockUniqueID = flockUiState.getUniqueId(),
                    actualWeight = "0",
                    standard = "2.887",
                    dateMeasured = DateUtils().weightDate(
                        date = dateReceived,
                        day = 70,
                        weightUiState = weightUiState
                    )
                ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_11),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "3.174",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 77,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_12),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "3.448",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 84,
                    weightUiState = weightUiState
                )
            ),

        )
    }

    /**
     * Standard weekly weights for Hybrid brown layers
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultWeightHybridLayers(flockUiState: FlockUiState): SnapshotStateList<WeightUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            WeightUiState(
                week = application.applicationContext.getString(R.string.initial),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.038",
                dateMeasured = flockUiState.getDate()
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_1),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.072",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 7,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_2),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.129",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 14,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_3),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.196",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 21,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_4),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.273",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 28,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_5),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.371",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 35,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_6),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.474",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 42,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_7),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.577",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 49,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_8),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.680",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 56,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_9),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.803",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 63,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_10),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.917",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 70,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_11),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.020",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 77,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_12),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.112",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 84,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_13),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.195",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 91,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_14),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.339",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 98,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_15),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1365",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 105,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_16),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.411",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 112,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_17),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.483",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 119,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_18),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.570",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 126,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_19),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.670",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 126,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_20_to_laying_stage),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.730",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 133,
                    weightUiState = weightUiState
                )
            )
        )
    }

    /**
     * Standard weekly weights for Hybrid Village chicken
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultWeightHybridVillageChicken(flockUiState: FlockUiState): SnapshotStateList<WeightUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            WeightUiState(
                week = application.applicationContext.getString(R.string.initial),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.039",
                dateMeasured = flockUiState.getDate()
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_1),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.130",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 7,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_2),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.297",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 14,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_3),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.549",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 21,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_4),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.872",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 28,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_5),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.245",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 35,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_6),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "1.660",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 42,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_7),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "2.102",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 49,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_8),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "2.532",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 56,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_9),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "2.941",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 63,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_10),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "3.308",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 70,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_11),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "3.655",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 77,
                    weightUiState = weightUiState
                )
            ),
            WeightUiState(
                week = application.applicationContext.getString(
                    R.string.week_12),
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "3.952",
                dateMeasured = DateUtils().weightDate(
                    date = dateReceived,
                    day = 84,
                    weightUiState = weightUiState
                )
            )
        )
    }

}