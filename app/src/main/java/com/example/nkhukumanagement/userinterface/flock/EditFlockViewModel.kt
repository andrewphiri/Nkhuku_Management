package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.data.FlockHealth
import com.example.nkhukumanagement.data.FlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class EditFlockViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, private val flockRepository: FlockRepository
) : ViewModel() {

    private val id: Int = checkNotNull(savedStateHandle[EditFlockDestination.flockIdArg])

    val flock: Flow<Flock> =
        flockRepository.getFlock(id)

    suspend fun insertHealth(flockHealth: FlockHealth) {
        flockRepository.insertFlockHealth(flockHealth)
    }

}