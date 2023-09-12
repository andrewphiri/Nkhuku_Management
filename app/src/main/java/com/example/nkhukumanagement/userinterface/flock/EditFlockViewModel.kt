package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.data.FlockHealth
import com.example.nkhukumanagement.data.FlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ViewModel to retrieve and update an item from the [FlockRepository]'s data source.
 */
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class EditFlockViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle, private val flockRepository: FlockRepository
) : ViewModel() {

    //Retrieve flock ID
    private val id: Int = checkNotNull(savedStateHandle[EditFlockDestination.flockIdArg])

    //Get flock using the retrieved ID
    val flock: Flow<Flock> =
        flockRepository.getFlock(id)


    /**
     * Insert the FlockHealth into the database
     */
    suspend fun insertHealth(flockHealth: FlockHealth) {
        flockRepository.insertFlockHealth(flockHealth)
    }

}