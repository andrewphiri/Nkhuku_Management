package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.data.FlockWithVaccinations
import com.example.nkhukumanagement.data.FlockWithWeight
import com.example.nkhukumanagement.data.Weight
import com.example.nkhukumanagement.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class WeightViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository
) : ViewModel() {

    companion object {
        private const val MILLIS = 5_000L
    }

    var weightUiState by mutableStateOf(WeightUiState())
        private set

    private var initialWeightList: SnapshotStateList<WeightUiState> = mutableStateListOf()

    private val flockID: Int = savedStateHandle[WeightScreenDestination.flockIdArg] ?: 0

    val flockWithWeight: StateFlow<FlockWithWeight> =
        flockRepository.getAllFlocksWithWeight(flockID)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = FlockWithWeight(flock = null, weights = listOf())
            )

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveInitialWeight(weightUiState: WeightUiState) {
        flockRepository.insertWeight(weightUiState.toWeight())
    }

    suspend fun updateWeight(weights: List<Weight>) {
        flockRepository.updateWeight(weights)
    }

    suspend fun deleteWeight(flockUniqueId: String) {
        flockRepository.deleteWeight(flockUniqueId)
    }

    fun getWeightList(): SnapshotStateList<WeightUiState> {
        return initialWeightList
    }

    fun isUpdateButtonEnabled(weights: List<WeightUiState>): Boolean {

        Log.i("Weight_Get_List", initialWeightList.toMutableList().toString())
        Log.i("Weight_LIST", weights.toString())
        return initialWeightList.toMutableList() != weights
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setWeightList(weightList: SnapshotStateList<WeightUiState>) {
        initialWeightList = weightList
    }

    fun updateWeightState(index: Int, uiState: WeightUiState) {
        initialWeightList[index] = uiState
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultWeight(flockUiState: FlockUiState): SnapshotStateList<WeightUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            WeightUiState(
                week = "Initial",
                flockUniqueID = flockUiState.getUniqueId(),
                actualWeight = "0",
                standard = "0.040",
                dateMeasured = flockUiState.getDate()
            ),
            WeightUiState(
                week = "Week 1",
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
                week = "Week 2",
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
                week = "Week 3",
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
                week = "Week 4",
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
                week = "Week 5",
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
                week = "Week 6",
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
                week = "Week 7",
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
                week = "Week 8",
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