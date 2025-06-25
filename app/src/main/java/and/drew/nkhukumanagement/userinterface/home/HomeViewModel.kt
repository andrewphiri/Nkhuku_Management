package and.drew.nkhukumanagement.userinterface.home

import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * View model to receive all items from database
 */
@HiltViewModel
class HomeViewModel @Inject constructor(val flockRepository: FlockRepository) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }

//    val homeUiState: StateFlow<HomeUiState> =
//        flockRepository.getAllFlockItems().map { HomeUiState().flockList }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(MILLIS),
//                initialValue = HomeUiState()
//            )

    private val _homeUiState = MutableStateFlow(HomeUiState().flockList)
    val homeUiState: StateFlow<List<Flock>?> = _homeUiState

    fun getAllFlockItems() {
        viewModelScope.launch {
            flockRepository.getAllFlockItems().collect {
                if (it != null) {
                    _homeUiState.value = it
                }
            }
        }
    }

}