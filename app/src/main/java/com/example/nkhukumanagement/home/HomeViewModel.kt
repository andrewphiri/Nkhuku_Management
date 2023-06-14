package com.example.nkhukumanagement.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.FlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * View model to receive all items from database
 */
@HiltViewModel
class HomeViewModel @Inject constructor(flockRepository: FlockRepository) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }

    val homeUiState: StateFlow<HomeUiState> =
        flockRepository.getAllFlockItems().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = HomeUiState()
            )
}