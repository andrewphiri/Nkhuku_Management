package com.example.nkhukumanagement.userinterface.flock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.nkhukumanagement.data.FlockRepository

class VaccinationViewModel(private val flockRepository: FlockRepository): ViewModel() {

    var vaccinationUiState by mutableStateOf(VaccinationUiState())
        private set

    fun updateUiState(newVaccinationUiState: VaccinationUiState) {
        vaccinationUiState =   newVaccinationUiState.copy(actionEnabled = newVaccinationUiState.isValid())
    }
}