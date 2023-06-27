package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.nkhukumanagement.FeedUiState
import com.example.nkhukumanagement.data.FlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.FlockWithFeed
import com.example.nkhukumanagement.data.FlockWithVaccinations
import com.example.nkhukumanagement.data.FlockWithWeight
import com.example.nkhukumanagement.data.Weight
import com.example.nkhukumanagement.home.HomeUiState
import com.example.nkhukumanagement.home.HomeViewModel
import com.example.nkhukumanagement.isValid
import com.example.nkhukumanagement.toFeed
import kotlinx.coroutines.flow.WhileSubscribed

@HiltViewModel
class FlockDetailsViewModel @Inject constructor(savedStateHandle: SavedStateHandle,
                                                private val flockRepository: FlockRepository) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }

    var weightUiState by mutableStateOf(WeightUiState())
        private set

    var feedUiState by mutableStateOf(FeedUiState())
        private set

    private val flockID: Int = checkNotNull(savedStateHandle[FlockDetailsDestination.flockIdArg])

    @RequiresApi(Build.VERSION_CODES.O)
    val detailsFeedUiState: StateFlow<FlockWithFeed> =
        flockRepository.getAllFlocksWithFeed(flockID)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = FlockWithFeed(flock = null, feedList = null)
            )

    val detailsWeightUiState: StateFlow<FlockWithWeight> =
        flockRepository.getAllFlocksWithWeight(flockID)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = FlockWithWeight(flock = null, weights = null)
            )

    val detailsVaccinationUiState: StateFlow<FlockWithVaccinations> =
        flockRepository.getAllFlocksWithVaccinations(flockID)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = FlockWithVaccinations(flock = null, vaccinations = null)
            )

    fun updateWeightUiState(uiState: WeightUiState) {
        weightUiState =   uiState.copy(enabled = uiState.isValid())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveWeight(weightUiState: WeightUiState) {
        if (weightUiState.isValid()) {
            flockRepository.insertWeight(weightUiState.toWeight())
        }
    }

    suspend fun deleteWeight(flockUniqueId: String) {
        flockRepository.deleteWeight(flockUniqueId)
    }

    fun updateFeedUiState(uiState: FeedUiState) {
        feedUiState =   uiState.copy(enabled = uiState.isValid())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveFeed(feedUiState: FeedUiState) {
        if (feedUiState.isValid()) {
            flockRepository.insertFeed(feedUiState.toFeed())
        }
    }

    suspend fun deleteFeed(flockUniqueId: String) {
        flockRepository.deleteFeed(flockUniqueId)
    }
}