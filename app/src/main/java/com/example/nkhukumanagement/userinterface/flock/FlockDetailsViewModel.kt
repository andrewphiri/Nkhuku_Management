package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.data.FlockWithFeed
import com.example.nkhukumanagement.data.FlockWithVaccinations
import com.example.nkhukumanagement.data.FlockWithWeight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel to retrieve a Flock item from the [FlockRepository]'s data source.
 */
@HiltViewModel
class FlockDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val flockRepository: FlockRepository
) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }

    private val flockID: Int = checkNotNull(savedStateHandle[FlockDetailsDestination.flockIdArg])

    val flock: Flow<Flock> =
        flockRepository.getFlock(flockID)

    /**
     * Get all flock with feed items
     */
    @RequiresApi(Build.VERSION_CODES.O)
    val flockWithFeedStateFlow: StateFlow<FlockWithFeed> =
        flockRepository.getAllFlocksWithFeed(flockID)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = FlockWithFeed(flock = null, feedList = null)
            )

    /**
     * Get all flock with weight items
     */
    val flockWithWeightStateFlow: StateFlow<FlockWithWeight> =
        flockRepository.getAllFlocksWithWeight(flockID)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = FlockWithWeight(flock = null, weights = null)
            )

    /**
     * Get all flock with vaccinations items.
     */
    val flockWithVaccinationsStateFlow: StateFlow<FlockWithVaccinations> =
        flockRepository.getAllFlocksWithVaccinations(flockID)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = FlockWithVaccinations(flock = null, vaccinations = listOf())
            )

}