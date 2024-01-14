package and.drew.nkhukumanagement.userinterface.planner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * ViewModel that holds the [PlannerUiState]
 */
class PlannerViewModel @Inject constructor() : ViewModel() {

    var plannerUiState by mutableStateOf(PlannerUiState())

    fun updateUiState(plannerState: PlannerUiState) {
        plannerUiState = plannerState
    }
}