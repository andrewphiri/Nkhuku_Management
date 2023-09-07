package com.example.nkhukumanagement

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

class PlannerViewModel : ViewModel() {

    var plannerUiState by mutableStateOf(PlannerUiState())

    fun updateUiState(plannerState: PlannerUiState) {
        plannerUiState = plannerState
    }
}