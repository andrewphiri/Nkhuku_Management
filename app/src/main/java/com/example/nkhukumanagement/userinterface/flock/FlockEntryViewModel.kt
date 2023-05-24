package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nkhukumanagement.data.FlockRepository

@RequiresApi(Build.VERSION_CODES.O)
class FlockEntryViewModel(private val flockRepository: FlockRepository) : ViewModel() {
    /**
     * Holds the current ui state
     */
    var flockUiState by mutableStateOf(FlockUiState())
        private set


    fun updateUiState(newFlockUiState: FlockUiState) {
        flockUiState =   newFlockUiState.copy(enabled = newFlockUiState.isValid())
    }

    suspend fun saveItem() {
        if (flockUiState.isValid()) {
            flockRepository.insertFlock(flockUiState.toFlock())
        }
    }
}