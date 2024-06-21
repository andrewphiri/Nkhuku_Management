package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.data.Eggs
import and.drew.nkhukumanagement.data.EggsSummary
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockAndEggsSummary
import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.FlockWithEggs
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

@HiltViewModel
class EggsInventoryViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository
): ViewModel() {

    //Retrieve flock ID
    val flockId: StateFlow<Int> =
        savedStateHandle.getStateFlow(key = EggsInventoryScreenDestination.idArg, initialValue = 0)

    val eggsID: StateFlow<Int> =
        savedStateHandle.getStateFlow(key = EditEggsDestination.eggsIdArg, initialValue = 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val flock: Flow<Flock?> =
        savedStateHandle.getStateFlow(key = EditEggsDestination.flockArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getFlock(it)
            }


    //    val flockHealth: Flow<FlockHealth?> =
//        flockRepository.getFlockHealthItem(healthId)
    @OptIn(ExperimentalCoroutinesApi::class)
    val egg: Flow<Eggs?> =
        savedStateHandle.getStateFlow(key = EditEggsDestination.eggsIdArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getEggItem(it)
            }

    /**
     * Get all flock and eggsSummary items
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val flockAndEggsSummaryStateFlow: Flow<FlockAndEggsSummary?> =
        savedStateHandle.getStateFlow(key = EditEggsDestination.flockArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getFlockAndEggsSummary(it)
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    val flockWithEggs: Flow<FlockWithEggs?> =
        savedStateHandle.getStateFlow(key = EggsInventoryScreenDestination.idArg, initialValue = 0)
            .flatMapLatest {
                flockRepository.getAllFlocksWithEggs(it)
            }


    /**
     * Insert the eggs into the database
     */
    suspend fun insertEgg(eggs: Eggs) {
        flockRepository.insertEgg(eggs)
    }

    /**
     * Insert the eggSummary into the database
     */
    suspend fun insertEggSummary(eggsSummary: EggsSummary) {
        flockRepository.insertEggsSummary(eggsSummary)
    }

    /**
     * Update the eggs into the database
     */
    suspend fun updateEggs(eggs: Eggs) {
        flockRepository.updateEggs(eggs)
    }

    /**
     * Update the eggs into the database
     */
    suspend fun updateEggsSummary(eggsSummary: EggsSummary) {
        flockRepository.updateEggsSummary(eggsSummary)
    }

    /**
     * Delete eggs
     */
    suspend fun deleteEggs(eggs: Eggs) {
        flockRepository.deleteEggs(eggs)
    }

    /**
     * Delete eggs
     */
    suspend fun deleteEggsSummary(uniqueId: String) {
        flockRepository.deleteEggsSummary(flockUniqueID = uniqueId)
    }

    /**
     * Delete eggs
     */
    suspend fun deleteEggs(uniqueId: String) {
        flockRepository.deleteEggs(uniqueId)
    }

    fun setFlockID(flockID: Int?) {
        savedStateHandle[EggsInventoryScreenDestination.idArg] = flockID
    }

    fun setEggID(eggId: Int?) {
        savedStateHandle[EditEggsDestination.eggsIdArg] = eggId
    }

    fun getFlock(uniqueId: String) : Flow<Flock>? {
        return flockRepository.getFlock(uniqueID = uniqueId)
    }

}