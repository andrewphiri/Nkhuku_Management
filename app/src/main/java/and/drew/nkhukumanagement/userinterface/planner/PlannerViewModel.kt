package and.drew.nkhukumanagement.userinterface.planner

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * ViewModel that holds the [PlannerUiState]
 */
class PlannerViewModel : ViewModel() {

    var plannerUiState by mutableStateOf(PlannerUiState())

    fun updateUiState(plannerState: PlannerUiState) {
        plannerUiState = plannerState
    }
}