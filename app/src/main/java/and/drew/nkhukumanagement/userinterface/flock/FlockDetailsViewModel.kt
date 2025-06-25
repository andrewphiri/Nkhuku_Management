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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

//    var flockID: StateFlow<Int> =
//        savedStateHandle
//            .getStateFlow(key = FlockDetailsDestination.flockId, 0)


    private val _flock = MutableStateFlow<Flock?>(null)
    val flock: StateFlow<Flock?> = _flock


//    var allFlocks: StateFlow<HomeUiState> =
//        flockRepository.getAllFlockItems().map { HomeUiState(it) }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(MILLIS),
//                initialValue = HomeUiState()
//            )

    private val _allFlocks = MutableStateFlow<List<Flock>?>(HomeUiState().flockList)
    val allFlocks: StateFlow<List<Flock>?> = _allFlocks

    /**
     * Get all flock with feed items
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
//    var flockWithFeedStateFlow: Flow<FlockWithFeed?> =
//        savedStateHandle.getStateFlow(key = FlockDetailsDestination.flockId, initialValue = 0)
//            .flatMapLatest {
//                flockRepository.getAllFlocksWithFeed(it)
//            }

    private val _flockWithFeed = MutableStateFlow<FlockWithFeed?>(null)
    val flockWithFeedStateFlow: StateFlow<FlockWithFeed?> = _flockWithFeed

    /**
     * Get all flock with eggs items
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
//    var flockAndEggsSummaryStateFlow: Flow<FlockAndEggsSummary?> =
//        savedStateHandle.getStateFlow(key = FlockDetailsDestination.flockId, initialValue = 0)
//            .flatMapLatest {
//                flockRepository.getFlockAndEggsSummary(it)
//            }

    private val _flockAndEggsSummary = MutableStateFlow<FlockAndEggsSummary?>(null)
    val flockAndEggsSummaryStateFlow: StateFlow<FlockAndEggsSummary?> = _flockAndEggsSummary


    /**
     * Get all flock with weight items
     */
    @OptIn(ExperimentalCoroutinesApi::class)
//    var flockWithWeightStateFlow: Flow<FlockWithWeight?> =
//        savedStateHandle.getStateFlow(key = FlockDetailsDestination.flockId, 0)
//            .flatMapLatest {
//                flockRepository.getAllFlocksWithWeight(it)
//            }

    private val _flockWithWeightStateFlow = MutableStateFlow<FlockWithWeight?>(null)
    val flockWithWeightStateFlow: StateFlow<FlockWithWeight?> = _flockWithWeightStateFlow


    /**
     * Get all flock with vaccinations items.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
//    var flockWithVaccinationsStateFlow: Flow<FlockWithVaccinations?> =
//        savedStateHandle.getStateFlow(key = FlockDetailsDestination.flockId, 0)
//            .flatMapLatest {
//                flockRepository.getAllFlocksWithVaccinations(it)
//            }

    private val _flockWithVaccinationsStateFlow = MutableStateFlow<FlockWithVaccinations?>(null)
    val flockWithVaccinationsStateFlow: StateFlow<FlockWithVaccinations?> = _flockWithVaccinationsStateFlow


    @RequiresApi(Build.VERSION_CODES.O)
    fun setFlockID(id: Int) {
        savedStateHandle[FlockDetailsDestination.flockId] = id
    }

    fun getFlock(id: Int) {
        viewModelScope.launch {
            flockRepository.getFlock(id) // This returns Flow<Flock?>
                ?.collect { flockItem -> // Collect the Flow
                    _flock.value = flockItem // Assign the emitted Flock? to the StateFlow's value
                }
        }
    }

    fun getAllFlocks() {
        viewModelScope.launch {
            flockRepository.getAllFlockItems().collect {
                _allFlocks.value = it
            }
        }
    }

    fun getFlockWithFeed(id: Int) {
        viewModelScope.launch {
            flockRepository.getAllFlocksWithFeed(id).collect {
                _flockWithFeed.value = it
            }
        }
    }

    fun getFlockAndEggsSummary(id: Int) {
        viewModelScope.launch {
            flockRepository.getFlockAndEggsSummary(id).collect {
                _flockAndEggsSummary.value = it
            }
        }
    }

    fun getFlockWithWeight(id: Int) {
        viewModelScope.launch {
            flockRepository.getAllFlocksWithWeight(id).collect {
                _flockWithWeightStateFlow.value = it
            }
        }
    }

    fun getFlockWithVaccinations(id: Int) {
        viewModelScope.launch {
            flockRepository.getAllFlocksWithVaccinations(id).collect {
                _flockWithVaccinationsStateFlow.value = it
            }
        }
    }
}