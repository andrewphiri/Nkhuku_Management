package com.example.nkhukumanagement.userinterface.vaccination

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.userinterface.flock.FlockUiState
import com.example.nkhukumanagement.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel to insert, retrieve, update and delete a vaccination item from the [FlockRepository]'s data source.
 */
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class VaccinationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val flockRepository: FlockRepository
) : ViewModel() {

    companion object {
        private const val MILLIS = 5_000L
    }

    private var initialVaccinationList: SnapshotStateList<VaccinationUiState> = mutableStateListOf()

    /**
     * Holds the current Vaccination UI state
     */
    var vaccinationUiState by mutableStateOf(VaccinationUiState())
        private set

    //Dropdown menu items for the vaccination entry
    val options = mutableListOf("Gumburo", "Lasota")

    /**
     * Update the VaccinationUiState List at the specified index
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUiState(index: Int, newVaccinationUiState: VaccinationUiState) {
        initialVaccinationList[index] =
            newVaccinationUiState.copy(actionEnabled = newVaccinationUiState.isValid())
    }

    /**
     * Insert the Vaccination Item into the database
     */
    suspend fun saveVaccination(vaccinationUiState: VaccinationUiState) {
        if (vaccinationUiState.isValid()) {
            flockRepository.insertVaccination(vaccinationUiState.toVaccination())
        }
    }


    /**
     * Delete weight items from the database
     */
    suspend fun deleteWeight(flockUniqueId: String) {
        flockRepository.deleteWeight(flockUniqueId)
    }

    /**
     * Delete feed items from the database
     */
    suspend fun deleteFeed(flockUniqueId: String) {
        flockRepository.deleteFeed(flockUniqueId)
    }


    /**
     * Delete vaccination items from the database
     */
    suspend fun deleteVaccination(flockUniqueID: String) {
        flockRepository.deleteVaccination(flockUniqueID)
    }

    /**
     * Get vaccination list
     */
    fun getInitialVaccinationList(): SnapshotStateList<VaccinationUiState> {
        return initialVaccinationList
    }

    /**
     * Set initial vaccination dates. This will be based on the breed selected
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setInitialVaccinationDates(vaccinationUiStateList: SnapshotStateList<VaccinationUiState>) {
        initialVaccinationList = vaccinationUiStateList
    }

    fun reset() {
        initialVaccinationList = mutableStateListOf()
    }

    /**
     * Default vaccination dates based on breed and date chicks received
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultVaccinationDates(
        flockUiState: FlockUiState,
        vaccinationUiState: VaccinationUiState
    ): SnapshotStateList<VaccinationUiState> {

        return when (flockUiState.breed) {
            "Hybrid" -> {
                defaultHybridVaccinations(
                    flockUiState = flockUiState,
                    vaccinationUiState = vaccinationUiState
                )
            }

            "Ross" -> {
                defaultRossVaccinations(
                    flockUiState = flockUiState,
                    vaccinationUiState = vaccinationUiState
                )
            }

            "Zamhatch" -> {
                defaultZamhatchVaccinations(
                    flockUiState = flockUiState,
                    vaccinationUiState = vaccinationUiState
                )
            }

            "Other" -> {
                defaultOtherVaccinations(
                    flockUiState = flockUiState,
                    vaccinationUiState = vaccinationUiState
                )
            }

            else -> {
                defaultOtherVaccinations(
                    flockUiState = flockUiState,
                    vaccinationUiState = vaccinationUiState
                )
            }
        }
    }

    /**
     * Default hybrid vaccination dates
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultHybridVaccinations(
        flockUiState: FlockUiState,
        vaccinationUiState: VaccinationUiState
    ): SnapshotStateList<VaccinationUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())

        return mutableStateListOf(
            VaccinationUiState(
                vaccinationNumber = 1, name = options[0],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 9,
                    vaccinationUiState = vaccinationUiState
                )
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = options[1],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 13,
                    vaccinationUiState = vaccinationUiState
                )
            ),
            VaccinationUiState(
                vaccinationNumber = 3, name = options[0],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 17,
                    vaccinationUiState = vaccinationUiState
                )
            ),
            VaccinationUiState(
                vaccinationNumber = 4, name = options[1],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 20,
                    vaccinationUiState = vaccinationUiState
                )
            )
        )
    }

    /**
     * Default Ross vaccination dates
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultRossVaccinations(
        flockUiState: FlockUiState,
        vaccinationUiState: VaccinationUiState
    ): SnapshotStateList<VaccinationUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            VaccinationUiState(
                vaccinationNumber = 1, name = options[0],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 9,
                    vaccinationUiState = vaccinationUiState
                )
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = options[1],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 11,
                    vaccinationUiState = vaccinationUiState
                )
            ),
        )
    }

    /**
     * Default Ross vaccination dates
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultZamhatchVaccinations(
        flockUiState: FlockUiState,
        vaccinationUiState: VaccinationUiState
    ): SnapshotStateList<VaccinationUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            VaccinationUiState(
                vaccinationNumber = 1, name = options[0],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 9,
                    vaccinationUiState = vaccinationUiState
                )
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = options[1],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 11,
                    vaccinationUiState = vaccinationUiState
                )
            ),

            )
    }

    /**
     * Default Other vaccination dates
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultOtherVaccinations(
        flockUiState: FlockUiState,
        vaccinationUiState: VaccinationUiState
    ): SnapshotStateList<VaccinationUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())
        return mutableStateListOf(
            VaccinationUiState(
                vaccinationNumber = 1,
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 10,
                    vaccinationUiState = vaccinationUiState
                )
            ),
        )
    }
}