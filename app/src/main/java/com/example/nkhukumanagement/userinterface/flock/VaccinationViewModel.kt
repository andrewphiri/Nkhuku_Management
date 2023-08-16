package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.nkhukumanagement.FeedUiState
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.toFeed
import com.example.nkhukumanagement.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

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
    var vaccinationUiState by mutableStateOf(VaccinationUiState())
        private set
    val options = listOf("Gumburo", "Lasota")
    private val flockID: Int = savedStateHandle[AddVaccinationsDestination.flockIdArg] ?: -1


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUiState(index: Int, newVaccinationUiState: VaccinationUiState) {
        initialVaccinationList[index] =
            newVaccinationUiState.copy(actionEnabled = newVaccinationUiState.isValid())
    }

    suspend fun saveVaccination(vaccinationUiState: VaccinationUiState) {
        if (vaccinationUiState.isValid()) {
            flockRepository.insertVaccination(vaccinationUiState.toVaccination())
        }
    }

    suspend fun saveInitialWeight() {
        flockRepository.insertWeight(
            WeightUiState(
                flockUniqueID = vaccinationUiState.getUniqueId(),
                week = "Initial Weight",
                standard = "0.04",
                actualWeight = "0.04",
                dateMeasured = DateUtils().convertLocalDateToString(LocalDate.now())
            ).toWeight()
        )

    }

    suspend fun deleteWeight(flockUniqueId: String) {
        flockRepository.deleteWeight(flockUniqueId)
    }

    suspend fun deleteFeed(flockUniqueId: String) {
        flockRepository.deleteFeed(flockUniqueId)
    }

    suspend fun saveInitialFeed() {
        flockRepository.insertFeed(
            FeedUiState(
                flockUniqueID = vaccinationUiState.getUniqueId(),
                name = "N/A",
                type = "Starter",
                actualConsumed = "0",
                feedingDate = DateUtils().convertLocalDateToString(LocalDate.now()),
            ).toFeed()
        )
    }

    suspend fun deleteVaccination(flockUniqueID: String) {
        flockRepository.deleteVaccination(flockUniqueID)
    }

    fun getInitialVaccinationList(): SnapshotStateList<VaccinationUiState> {
        return initialVaccinationList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setInitialDates(vaccinationUiStateList: SnapshotStateList<VaccinationUiState>) {
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
        Log.i("Breed selected ", flockUiState.breed)
        Log.i("Breed DATE ", flockUiState.getDate())
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