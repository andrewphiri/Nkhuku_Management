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
class FlockEntryViewModel @Inject constructor(private val flockRepository: FlockRepository, state: SavedStateHandle) : ViewModel() {
    /**
     * Holds the current ui state
     */
    var flockUiState by mutableStateOf(FlockUiState())
        private set

    private val getFlock: StateFlow<FlockUiState> = state.getStateFlow("flock", flockUiState)



//    fun setFlock(flockUiState: FlockUiState) {
//        state["flock"] = flockUiState
//        Log.i("Flock SET ", state.get<FlockUiState>("flock").toString())
//    }

//    fun getFlock(): FlockUiState? {
//        Log.i("GET FLOCK ", state.get<FlockUiState>("flock").toString())
//       return getFlock.value
////        stateIn(
////            scope = viewModelScope,
////            started = SharingStarted.WhileSubscribed(5_000L),
////            initialValue = flockUiState
////        ).value
//    }

    fun updateUiState(newFlockUiState: FlockUiState) {
        flockUiState =   newFlockUiState.copy(enabled = newFlockUiState.isValid())
    }

    suspend fun saveItem() {
        if (flockUiState.isValid()) {
            flockRepository.insertFlock(flockUiState.toFlock())
        }
    }
}