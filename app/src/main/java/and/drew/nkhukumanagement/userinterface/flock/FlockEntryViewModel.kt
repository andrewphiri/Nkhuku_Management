package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.FlockRepository
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel to insert, retrieve, update and delete a flock item from the [FlockRepository]'s data source.
 */
@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class FlockEntryViewModel @Inject constructor(
    private val flockRepository: FlockRepository,
    state: SavedStateHandle
) : ViewModel() {
    /**
     * Holds the new flock entry ui state
     */
    var flockUiState by mutableStateOf(FlockUiState())
        private set

    /**
     * Update the FlockUiState with the passed in value
     */
    fun updateUiState(newFlockUiState: FlockUiState) {
        flockUiState = newFlockUiState.copy(enabled = newFlockUiState.isValid())
    }


    /**
     * Insert the Flock into the database
     */
    suspend fun saveItem() {
        if (flockUiState.isValid()) {
            flockRepository.insertFlock(flockUiState.toFlock())
        }
    }

    /**
     * Update the Flock
     */
    suspend fun updateItem(flockUiState: FlockUiState) {
        if (flockUiState.isValid()) {
            flockRepository.updateFlock(flockUiState.toFlock())
        }
    }

    /**
     * Deletes the Flock from the database
     */
    suspend fun deleteFlock(flockUniqueID: String) {
        flockRepository.deleteFlock(flockUniqueID)
    }

    /**
     * Deletes the Flock from the database
     */
    suspend fun deleteFlockHealth(flockUniqueID: String) {
        flockRepository.deleteFlockHealth(flockUniqueID)
    }

    /**
     * Reset the FlockUiState to default values
     */
    fun resetAll() {
        flockUiState = flockUiState.copy(
            id = 0,
            uniqueId = "",
            batchName = "",
            breed = "",
            datePlaced = "",
            quantity = "",
            donorFlock = "",
            cost = "",
            stock = "0",
            mortality = "0",
            imageResourceId = R.drawable.add_flock_placeholder,
            culls = "0",
            enabled = false
        )
    }

    fun reset() {
        flockUiState = flockUiState.copy(
            uniqueId = "",
            batchName = "",
            donorFlock = "",
            stock = "0",
            mortality = "0",
            imageResourceId = R.drawable.add_flock_placeholder,
            culls = "0",
        )
    }
}