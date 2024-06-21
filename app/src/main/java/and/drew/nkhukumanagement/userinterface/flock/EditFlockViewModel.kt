package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockAndEggsSummary
import and.drew.nkhukumanagement.data.FlockHealth
import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.FlockWithHealth
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * ViewModel to retrieve and update an item from the [FlockRepository]'s data source.
 */
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class EditFlockViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val flockRepository: FlockRepository
) : ViewModel() {

    //Retrieve flock ID
    val flockId: StateFlow<Int> =
        savedStateHandle.getStateFlow(key = EditFlockDestination.flockIdArg, initialValue = 0)

    val healthId: StateFlow<Int> =
        savedStateHandle.getStateFlow(key = EditFlockDestination.healthIdArg, initialValue = 0)

//    //Get flock using the retrieved ID
//    val flock: Flow<Flock?> =
//        flockRepository.getFlock(flockId)

    @OptIn(ExperimentalCoroutinesApi::class)
    val flock: Flow<Flock?> =
        savedStateHandle.getStateFlow(key = EditFlockDestination.flockIdArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getFlock(it)
            }


    //    val flockHealth: Flow<FlockHealth?> =
//        flockRepository.getFlockHealthItem(healthId)
    @OptIn(ExperimentalCoroutinesApi::class)
    val flockHealth: Flow<FlockHealth?> =
        savedStateHandle.getStateFlow(key = EditFlockDestination.healthIdArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getFlockHealthItem(it)
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    val flockWithHealth: Flow<FlockWithHealth?> =
        savedStateHandle.getStateFlow(key = EditFlockDestination.flockIdArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getFlocksWithHealth(it)
            }

    /**
     * Get all flock and eggsSummary items
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val flockAndEggsSummaryStateFlow: Flow<FlockAndEggsSummary?> =
        savedStateHandle.getStateFlow(key = EditFlockDestination.flockIdArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getFlockAndEggsSummary(it)
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

    fun getFlock(uniqueId: String) : Flow<Flock>? {
        return flockRepository.getFlock(uniqueID = uniqueId)
    }

}