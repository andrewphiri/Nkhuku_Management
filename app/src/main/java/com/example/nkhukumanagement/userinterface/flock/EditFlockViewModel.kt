package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.data.FlockHealth
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.data.FlockWithHealth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    companion object {
        private const val MILLIS = 5_000L
    }

    //Retrieve flock ID
    val flockId: Int = checkNotNull(savedStateHandle[EditFlockDestination.flockIdArg])

    val healthId = savedStateHandle[EditFlockDestination.healthIdArg] ?: 0

    //Get flock using the retrieved ID
    val flock: Flow<Flock> =
        flockRepository.getFlock(flockId)

    val flockHealth: Flow<FlockHealth> =
        flockRepository.getFlockHealthItem(healthId)

    val flockWithHealth: StateFlow<FlockWithHealth> =
        flockRepository.getFlocksWithHealth(flockId)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = FlockWithHealth(flock = null, health = listOf())
            )


    /**
     * Insert the FlockHealth into the database
     */
    suspend fun insertHealth(flockHealth: FlockHealth) {
        flockRepository.insertFlockHealth(flockHealth)
    }

    /**
     * Update the FlockHealth
     */
    suspend fun updateHealth(flockHealth: FlockHealth) {
        flockRepository.updateFlockHealth(flockHealth)
    }

}