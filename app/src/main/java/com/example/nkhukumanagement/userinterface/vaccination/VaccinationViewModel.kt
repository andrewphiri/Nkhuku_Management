package com.example.nkhukumanagement.userinterface.vaccination

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.nkhukumanagement.FlockApplication
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.data.Vaccination
import com.example.nkhukumanagement.userinterface.flock.FlockUiState
import com.example.nkhukumanagement.utils.AlarmReceiver
import com.example.nkhukumanagement.utils.AlarmScheduler
import com.example.nkhukumanagement.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel to insert, retrieve, update and delete a vaccination item from the [FlockRepository]'s data source.
 */
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class VaccinationViewModel @Inject constructor(
    private val application: FlockApplication,
    private val flockRepository: FlockRepository
) : ViewModel(), AlarmScheduler {
    companion object {
        private const val MILLIS = 5_000L
        const val TITLE = "title_notification"
        const val CONTENT_TEXT = "content_text_notification"
        const val BIG_TEXT_CONTENT = "big_text_content_notification"
        const val FLOCK_ID = "flock_ID"
    }

    private val alarmManager =
        application.applicationContext.getSystemService(AlarmManager::class.java)

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

    /**
     * Schedule an alarm to be triggered day before the vaccination date
     */
    override fun schedule(vaccination: Vaccination, flock: FlockUiState) {
        val intent = Intent(application.applicationContext, AlarmReceiver::class.java).apply {
            putExtra(TITLE, "Vaccination Reminder")
            putExtra(CONTENT_TEXT, "${vaccination.name} vaccination due.")
            putExtra(
                BIG_TEXT_CONTENT,
                "${vaccination.name} vaccination for ${flock.batchName} is due tomorrow, " +
                        "${DateUtils().dateToStringLongFormat(vaccination.date)}."
            )
            putExtra(FLOCK_ID, flock.id)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,
            vaccination.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            DateUtils().calculateAlarmDate(vaccination),
            pendingIntent
        )
    }

    override fun cancelAlarm(vaccination: Vaccination) {
        val pendingIntent = PendingIntent.getBroadcast(
            application.applicationContext,
            vaccination.hashCode(),
            Intent(application, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}