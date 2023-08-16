package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.data.FlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class FlockEntryViewModel @Inject constructor(
    private val flockRepository: FlockRepository,
    state: SavedStateHandle
) : ViewModel() {
    /**
     * Holds the current ui state
     */
    var flockUiState by mutableStateOf(FlockUiState())
        private set

    fun updateUiState(newFlockUiState: FlockUiState) {
        flockUiState = newFlockUiState.copy(enabled = newFlockUiState.isValid())
    }

    suspend fun saveItem() {
        if (flockUiState.isValid()) {
            flockRepository.insertFlock(flockUiState.toFlock())
        }
    }

    suspend fun updateItem(flockUiState: FlockUiState) {
        if (flockUiState.isValid()) {
            flockRepository.updateFlock(flockUiState.toFlock())
            Log.i("FLOCK_UPDATE", flockUiState.toString())
        }
    }

    suspend fun deleteFlock(flockUniqueID: String) {
        flockRepository.deleteFlock(flockUniqueID)
    }

    suspend fun deleteFlockHealth(flockUniqueID: String) {
        flockRepository.deleteFlockHealth(flockUniqueID)
    }

    fun resetAll() {
        flockUiState = flockUiState.copy(
            id = 0,
            uniqueId = "",
            batchName = "",
            breed = "",
            datePlaced = "",
            quantity = "",
            donorFlock = "",
            stock = "0",
            mortality = "0",
            imageResourceId = R.drawable.chicken,
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
            imageResourceId = R.drawable.chicken,
            culls = "0",
        )
    }
}