package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockAndEggsSummary
import and.drew.nkhukumanagement.data.FlockHealth
import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.FlockWithFeed
import and.drew.nkhukumanagement.data.FlockWithHealth
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel to retrieve and update an item from the [FlockRepository]'s data source.
 */
@HiltViewModel
class EditFlockViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val flockRepository: FlockRepository
) : ViewModel() {

    //Retrieve flock ID
//    val flockId: StateFlow<Int> =
//        savedStateHandle.getStateFlow(key = EditFlockDestination.flockIdArg, initialValue = 0)
//
//    val healthId: StateFlow<Int> =
//        savedStateHandle.getStateFlow(key = EditFlockDestination.healthIdArg, initialValue = 0)

//    //Get flock using the retrieved ID
//    val flock: Flow<Flock?> =
//        flockRepository.getFlock(flockId)

//    val flock: Flow<Flock?> =
//        savedStateHandle.getStateFlow(key = EditFlockDestination.flockIdArg, initialValue = 0)
//            .flatMapLatest {
//                flockRepository.getFlock(it)
//            }


    //    val flockHealth: Flow<FlockHealth?> =
//        flockRepository.getFlockHealthItem(healthId)

//    val flockHealth: Flow<FlockHealth?> =
//        savedStateHandle.getStateFlow(key = EditFlockDestination.healthIdArg, initialValue = 0)
//            .flatMapLatest {
//                flockRepository.getFlockHealthItem(it)
//            }

    private val _flockHealth = MutableStateFlow<FlockHealth?>(null)
    val flockHealth: StateFlow<FlockHealth?> = _flockHealth

    @OptIn(ExperimentalCoroutinesApi::class)
//    val flockWithHealth: Flow<FlockWithHealth?> =
//        savedStateHandle.getStateFlow(key = EditFlockDestination.flockIdArg, initialValue = 0)
//            .flatMapLatest {
//                flockRepository.getFlocksWithHealth(it)
//            }

    private val _flockWithHealth = MutableStateFlow<FlockWithHealth?>(null)
    val flockWithHealth: StateFlow<FlockWithHealth?> = _flockWithHealth

    /**
     * Get all flock and eggsSummary items
     */
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @RequiresApi(Build.VERSION_CODES.O)
//    val flockAndEggsSummaryStateFlow: Flow<FlockAndEggsSummary?> =
//        savedStateHandle.getStateFlow(key = EditFlockDestination.flockIdArg, initialValue = 0)
//            .flatMapLatest {
//                flockRepository.getFlockAndEggsSummary(it)
//            }

    private val _flock = MutableStateFlow<Flock?>(FlockUiState().toFlock())
    val flock: StateFlow<Flock?> = _flock

    private val _flockAndEggsSummary = MutableStateFlow<FlockAndEggsSummary?>(null)
    val flockAndEggsSummaryStateFlow: StateFlow<FlockAndEggsSummary?> = _flockAndEggsSummary


    fun getFlockAndEggsSummary(flockID: Int) {
        viewModelScope.launch {
            flockRepository.getFlockAndEggsSummary(flockID).collect {
                _flockAndEggsSummary.value = it
            }
        }
    }

    fun getFlockHealth(healthID: Int) {
        viewModelScope.launch {
            flockRepository.getFlockHealthItem(healthID).collect {
                _flockHealth.value = it
            }
        }
    }

    fun getFlockWithHealth(id: Int) {
        viewModelScope.launch {
            flockRepository.getFlocksWithHealth(id).collect {
                _flockWithHealth.value = it
            }
        }
    }

    fun getFlock(id: Int) {
        viewModelScope.launch {
            flockRepository.getFlock(id) // This returns Flow<Flock?>
                ?.collect { flockItem -> // Collect the Flow
                    _flock.value = flockItem // Assign the emitted Flock? to the StateFlow's value
                }
        }
    }



    /**
     * Insert the FlockHealth into the database
     */
    suspend fun insertHealth(flockHealth: FlockHealth) {
        flockRepository.insertFlockHealth(flockHealth)
    }

    /**
     * Update the FlockHealth into the database
     */
    suspend fun updateFlock(flock: Flock) {
        flockRepository.updateFlock(flock)
    }

    /**
     * Update the FlockHealth
     */
    suspend fun updateHealth(flockHealth: FlockHealth) {
        flockRepository.updateFlockHealth(flockHealth)
    }

    fun setFlockID(flockID: Int?) {
        savedStateHandle[EditFlockDestination.flockIdArg] = flockID
    }

    fun setHealthID(healthId: Int?) {
        savedStateHandle[EditFlockDestination.healthIdArg] = healthId
    }

    fun setFlockIDForHealthScreen(flockID: Int?) {
        savedStateHandle[FlockHealthScreenDestination.flockIdArg] = flockID
    }

    fun getFlock(uniqueId: String?) : Flow<Flock>? {
        return flockRepository.getFlock(uniqueID = uniqueId)
    }

}