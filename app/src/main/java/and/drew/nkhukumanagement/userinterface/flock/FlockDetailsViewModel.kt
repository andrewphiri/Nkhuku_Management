package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockAndEggsSummary
import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.FlockWithEggs
import and.drew.nkhukumanagement.data.FlockWithFeed
import and.drew.nkhukumanagement.data.FlockWithVaccinations
import and.drew.nkhukumanagement.data.FlockWithWeight
import and.drew.nkhukumanagement.userinterface.home.HomeUiState
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel to retrieve a Flock item from the [FlockRepository]'s data source.
 */
@HiltViewModel
class FlockDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val flockRepository: FlockRepository
) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }
    //val id: StateFlow<Int> = savedStateHandle.getStateFlow(key = FlockDetailsDestination.flockId, 1)

    var flockID: StateFlow<Int> =
        savedStateHandle
            .getStateFlow(key = FlockDetailsDestination.flockId, 0)



    @OptIn(ExperimentalCoroutinesApi::class)
    val flock: Flow<Flock> =
        savedStateHandle
            .getStateFlow(key = FlockDetailsDestination.flockId, 0)
            .flatMapLatest {
                flockRepository.getFlock(it)
            }


    var allFlocks: StateFlow<HomeUiState> =
        flockRepository.getAllFlockItems().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = HomeUiState()
            )

    /**
     * Get all flock with feed items
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    var flockWithFeedStateFlow: Flow<FlockWithFeed?> =
        savedStateHandle.getStateFlow(key = FlockDetailsDestination.flockId, initialValue = 0)
            .flatMapLatest {
                flockRepository.getAllFlocksWithFeed(it)
            }

    /**
     * Get all flock with eggs items
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    var flockAndEggsSummaryStateFlow: Flow<FlockAndEggsSummary> =
        savedStateHandle.getStateFlow(key = FlockDetailsDestination.flockId, initialValue = 0)
            .flatMapLatest {
                flockRepository.getFlockAndEggsSummary(it)
            }


    /**
     * Get all flock with weight items
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    var flockWithWeightStateFlow: Flow<FlockWithWeight?> =
        savedStateHandle.getStateFlow(key = FlockDetailsDestination.flockId, 0)
            .flatMapLatest {
                flockRepository.getAllFlocksWithWeight(it)
            }


    /**
     * Get all flock with vaccinations items.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    var flockWithVaccinationsStateFlow: Flow<FlockWithVaccinations?> =
        savedStateHandle.getStateFlow(key = FlockDetailsDestination.flockId, 0)
            .flatMapLatest {
                flockRepository.getAllFlocksWithVaccinations(it)
            }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setFlockID(id: Int) {
        savedStateHandle[FlockDetailsDestination.flockId] = id
    }
}