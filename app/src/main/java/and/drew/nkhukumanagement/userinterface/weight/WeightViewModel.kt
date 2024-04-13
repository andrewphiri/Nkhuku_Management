package and.drew.nkhukumanagement.userinterface.weight

import and.drew.nkhukumanagement.FlockApplication
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
    val application: FlockApplication,
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
     * Standard weekly weights for broilers
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultWeight(flockUiState: FlockUiState): SnapshotStateList<WeightUiState> {
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
}