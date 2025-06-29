package and.drew.nkhukumanagement.userinterface.vaccination

import and.drew.nkhukumanagement.BaseFlockApplication
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.FlockWithVaccinations
import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.userinterface.flock.FlockUiState
import and.drew.nkhukumanagement.utils.Constants.BIG_TEXT_CONTENT
import and.drew.nkhukumanagement.utils.Constants.BIG_TEXT_CONTENT_TWO
import and.drew.nkhukumanagement.utils.Constants.CONTENT_TEXT
import and.drew.nkhukumanagement.utils.Constants.CONTENT_TEXT_TWO
import and.drew.nkhukumanagement.utils.Constants.FLOCK_ID
import and.drew.nkhukumanagement.utils.Constants.TITLE
import and.drew.nkhukumanagement.utils.Constants.TITLE_TWO
import and.drew.nkhukumanagement.utils.Constants.VACCINATION_ADMINISTERED
import and.drew.nkhukumanagement.utils.Constants.VACCINATION_DATE
import and.drew.nkhukumanagement.utils.Constants.VACCINATION_FLOCK_UNIQUE_ID
import and.drew.nkhukumanagement.utils.Constants.VACCINATION_ID
import and.drew.nkhukumanagement.utils.Constants.VACCINATION_NAME
import and.drew.nkhukumanagement.utils.Constants.VACCINATION_NOTES
import and.drew.nkhukumanagement.utils.Constants.VACCINATION_NOTIFICATION_UUID
import and.drew.nkhukumanagement.utils.Constants.VACCINATION_NOTIFICATION_UUID2
import and.drew.nkhukumanagement.utils.Constants.VACCINE_NOTIFICATION_ID
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.NotificationScheduler
import and.drew.nkhukumanagement.utils.VaccinationConfirmationWorker
import and.drew.nkhukumanagement.utils.VaccinationReminderWorker
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
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
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel to insert, retrieve, update and delete a vaccination item from the [FlockRepository]'s data source.
 */
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class VaccinationViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val application: BaseFlockApplication,
    private val flockRepository: FlockRepository
) : ViewModel(), NotificationScheduler {
    companion object {
        private const val MILLIS = 5_000L
    }

//    val flockID: Int = savedStateHandle[AddVaccinationsDestination.flockIdArg] ?: 0

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

//    val getAllVaccinationItems: StateFlow<List<Vaccination>> =
//        flockRepository.getAllVaccinationItems()
//            .map { it }
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(MILLIS),
//                initialValue = listOf()
//            )

    private val _getAllVaccinationItems = MutableStateFlow<List<Vaccination>?>(listOf())
    val getAllVaccinationItems: StateFlow<List<Vaccination>?> = _getAllVaccinationItems

    /**
     * Get all flock with vaccinations items.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
//    val flockWithVaccinationsStateFlow: Flow<FlockWithVaccinations?> =
//        savedStateHandle.getStateFlow(key = AddVaccinationsDestination.flockIdArg, initialValue = 0)
//            .flatMapLatest {
//                flockRepository.getAllFlocksWithVaccinations(it)
//            }

    private val _flockWithVaccinationsStateFlow = MutableStateFlow<FlockWithVaccinations?>(null)
    val flockWithVaccinationsStateFlow: StateFlow<FlockWithVaccinations?> = _flockWithVaccinationsStateFlow


    fun getFlockWithVaccinations(flockID: Int) {
        viewModelScope.launch {
            flockRepository.getAllFlocksWithVaccinations(flockID).collect {
                _flockWithVaccinationsStateFlow.value = it
            }
        }
    }

    fun getAllVaccinationItems() {
        viewModelScope.launch {
            flockRepository.getAllVaccinationItems().collect {
                _getAllVaccinationItems.value = it
            }
        }
    }

    fun setFlockID(id: Int) {
        savedStateHandle[AddVaccinationsDestination.flockIdArg] = id
    }

    /**
     * Update the VaccinationUiState List at the specified index
     */
    fun updateUiState(index: Int, newVaccinationUiState: VaccinationUiState) {
        initialVaccinationList[index] =
            newVaccinationUiState.copy(actionEnabled = newVaccinationUiState.isValid())
    }

    /**
     * Insert the Vaccination Item into the database
     */
    suspend fun saveVaccination(vaccinationUiState: VaccinationUiState) {
        if (vaccinationUiState.isValid()) {
            Log.i("CHECK_ID", vaccinationUiState.isValid().toString())
            Log.i("CHECK_ID", vaccinationUiState.toVaccination().toString())
            flockRepository.insertVaccination(vaccinationUiState.toVaccination())
        }
    }

    suspend fun updateVaccination(vaccination: Vaccination) {
        flockRepository.updateVaccination(vaccination)
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
        val flockTypeOptions = application.resources.getStringArray(R.array.types_of_flocks).toList()
        return when (flockUiState.flockType) {
            flockTypeOptions[0] -> {
                broilerVaccinationDates(
                    flockUiState = flockUiState,
                    vaccinationUiState = vaccinationUiState
                )
            }

            flockTypeOptions[1] -> {
                defaultLayerVaccinationDates(
                    flockUiState = flockUiState,
                    vaccinationUiState = vaccinationUiState
                )
            }

            flockTypeOptions[2] -> {
                defaultHybridVillageVaccinations(
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
     * Default vaccination dates based on breed and date chicks received
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultLayerVaccinationDates(
        flockUiState: FlockUiState,
        vaccinationUiState: VaccinationUiState
    ): SnapshotStateList<VaccinationUiState> {

        return when (flockUiState.breed) {
            "Hybrid Brown Layer" -> {
                defaultHybridBrownLayerVaccinations(
                    flockUiState = flockUiState,
                    vaccinationUiState = vaccinationUiState
                )
            }
            "Hybrid Zambro" -> {
                defaultHybridZambroLayerVaccinations(
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
     * Default vaccination dates based on breed and date chicks received
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun broilerVaccinationDates(
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

            "Tiger" -> {
                defaultTigerVaccinations(
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
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = options[1],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 13,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 3, name = options[0],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 17,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 4, name = options[1],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 20,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
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
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = options[1],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 11,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
        )
    }

    /**
     * Default Zamhatch vaccination dates
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
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = options[1],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 11,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
        )
    }

    /**
     * Default hybrid layer vaccination dates
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultHybridZambroLayerVaccinations(
        flockUiState: FlockUiState,
        vaccinationUiState: VaccinationUiState
    ): SnapshotStateList<VaccinationUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())

        return mutableStateListOf(
            VaccinationUiState(
                vaccinationNumber = 1, name = "Gumboro",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 9,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = "ND + IB",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 13,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
            ),
            VaccinationUiState(
                vaccinationNumber = 3, name = "Gumboro",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 17,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 4, name = "ND + IB",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 20,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
            ),
            VaccinationUiState(
                vaccinationNumber = 5, name = "Ma5 + clone 30",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 48,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
            ),
            VaccinationUiState(
                vaccinationNumber = 6, name = "CEO Pox + Diluent",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 55,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Wing web"
            ),
            VaccinationUiState(
                vaccinationNumber = 7, name = "IB + ND",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 69,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Intramuscular injection"
            ),
            VaccinationUiState(
                vaccinationNumber = 7, name = "Coryza",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 83,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Subcutaneous injection"
            ),
            VaccinationUiState(
                vaccinationNumber = 7, name = "Ma5 + clone 30",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 111,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
            ),
        )
    }

    /**
     * Default Tiger vaccination dates
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultTigerVaccinations(
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
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = options[1],
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 11,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
        )
    }

    /**
     * Default hybrid layer vaccination dates
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultHybridVillageVaccinations(
        flockUiState: FlockUiState,
        vaccinationUiState: VaccinationUiState
    ): SnapshotStateList<VaccinationUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())

        return mutableStateListOf(
            VaccinationUiState(
                vaccinationNumber = 1, name = "IDB MB",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 9,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = "ND + IB",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 13,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
            ),
            VaccinationUiState(
                vaccinationNumber = 3, name = "IDB MB",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 17,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 4, name = "ND Lasota",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 20,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
            ),
            VaccinationUiState(
                vaccinationNumber = 5, name = "Prox AE",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 48,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Wing web"
            ),
        )
    }

    /**
     * Default hybrid layer vaccination dates
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun defaultHybridBrownLayerVaccinations(
        flockUiState: FlockUiState,
        vaccinationUiState: VaccinationUiState
    ): SnapshotStateList<VaccinationUiState> {
        val dateReceived = DateUtils().stringToLocalDate(flockUiState.getDate())

        return mutableStateListOf(
            VaccinationUiState(
                vaccinationNumber = 1, name = "Gumboro",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 9,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = "IB 4/91",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 13,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
            ),
            VaccinationUiState(
                vaccinationNumber = 3, name = "Gumboro",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 17,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Drinking water"
            ),
            VaccinationUiState(
                vaccinationNumber = 4, name = "VH",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 20,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
            ),
            VaccinationUiState(
                vaccinationNumber = 5, name = "Ma5 + clone 30",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 48,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
            ),
            VaccinationUiState(
                vaccinationNumber = 6, name = "CEO Pox + Diluent",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 55,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Wing web"
            ),
            VaccinationUiState(
                vaccinationNumber = 7, name = "NOBILIS IB + ND",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 69,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Intramuscular injection"
            ),
            VaccinationUiState(
                vaccinationNumber = 8, name = "IB 4/91",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 76,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
            ),
            VaccinationUiState(
                vaccinationNumber = 9, name = "NOBIVAC CORYZA S.C. 0.5ML",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 83,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Subcutaneous injection"
            ),
            VaccinationUiState(
                vaccinationNumber = 10, name = "AE +POX + DIL",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 83,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Subcutaneous injection"
            ),
            VaccinationUiState(
                vaccinationNumber = 11, name = "NOBIVAC CORYZA S.C. 0.5ML",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 111,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Subcutaneous injection"
            ),
            VaccinationUiState(
                vaccinationNumber = 12, name = "NOBILIS IB + ND",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 111,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Intramuscular injection"
            ),
            VaccinationUiState(
                vaccinationNumber = 13, name = "Ma5 + clone 30",
                date = DateUtils().vaccinationDate(
                    date = dateReceived, day = 118,
                    vaccinationUiState = vaccinationUiState
                ),
                method = "Spray vaccination"
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
                ),
                method = "Drinking water"
            ),
        )
    }

//    /**
//     * Schedule an alarm to be triggered day before the vaccination date
//     */
//    override fun schedule(vaccination: Vaccination, flock: FlockUiState) {
//        val intent = Intent(application.applicationContext, AlarmReceiver::class.java).apply {
//            putExtra(TITLE, "Vaccination Reminder")
//            putExtra(CONTENT_TEXT, "${vaccination.name} vaccination due.")
//            putExtra(
//                BIG_TEXT_CONTENT,
//                "${vaccination.name} vaccination for ${flock.batchName} is due tomorrow, " +
//                        "${DateUtils().dateToStringLongFormat(vaccination.date)}."
//            )
//            putExtra(FLOCK_ID, flock.id)
//            putExtra(VACCINE_NOTIFICATION_ID, vaccination.id)
//        }
//        val pendingIntent = PendingIntent.getBroadcast(
//            application.applicationContext,
//            vaccination.hashCode(),
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        alarmManager.set(
//            AlarmManager.RTC_WAKEUP,
//            DateUtils().calculateVaccineNotificationDate(vaccination),
//            pendingIntent
//        )
//    }

//    /**
//     * Schedule an alarm to be triggered day before the vaccination date
//     */
//    override fun schedule(vaccination: Vaccination, flock: FlockUiState, notificationID: Int) {
//        val intent = Intent(application.applicationContext, AlarmReceiver::class.java).apply {
//            putExtra(TITLE, "Vaccination Reminder")
//            putExtra(CONTENT_TEXT, "${vaccination.name} vaccination due.")
//            putExtra(
//                BIG_TEXT_CONTENT,
//                "${vaccination.name} vaccination for ${flock.batchName} is due tomorrow, " +
//                        "${DateUtils().dateToStringLongFormat(vaccination.date)}."
//            )
//            putExtra(FLOCK_ID, flock.id)
//            putExtra(VACCINE_NOTIFICATION_ID, notificationID)
//        }
//        Log.i("CHECK_ID_EXTRA", flock.id.toString())
//        val pendingIntent = PendingIntent.getBroadcast(
//            application.applicationContext,
//            vaccination.hashCode(),
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//        alarmManager.set(
//            AlarmManager.RTC_WAKEUP,
//            DateUtils().calculateVaccineNotificationDate(vaccination),
//            pendingIntent
//        )
//    }

    /**
     * Schedule a notification to be triggered day before the vaccination date
     * and another on day of vaccine to confirm if vaccine has been administered
     */
    override fun schedule(vaccination: Vaccination, flock: FlockUiState, notificationID: Int) {
        val firstNotificationData = buildDataObjectFirstNotification(
            vaccination = vaccination,
            flock = flock,
            notificationID = notificationID
        )

        val secondNotificationData = buildDataObjectSecondNotification(
            vaccination = vaccination,
            flock = flock,
            notificationID = notificationID
        )

        val firstNotificationWorkerRequest =
            OneTimeWorkRequest.Builder(VaccinationReminderWorker::class.java)
                .setInputData(firstNotificationData)
                .setInitialDelay(
                    DateUtils().calculateVaccineNotificationDate(
                        vaccination = vaccination,
                        hour = 8, minutes = 0
                    ), TimeUnit.MINUTES
                )
                .build()
        val secondNotificationWorkerRequest =
            OneTimeWorkRequest.Builder(VaccinationConfirmationWorker::class.java)
                .setInputData(secondNotificationData)
                .setInitialDelay(
                    DateUtils().calculateConfirmVaccineNotificationDate(
                        vaccination = vaccination,
                        hour = 10, minutes = 0
                    ), TimeUnit.MINUTES
                )
                .build()

        if (LocalDate.now().isBefore(vaccination.date)) {
            WorkManager.getInstance(application.applicationContext)
                .beginUniqueWork(vaccination.notificationUUID.toString(), ExistingWorkPolicy.REPLACE, firstNotificationWorkerRequest)
                .enqueue()

            WorkManager.getInstance(application.applicationContext)
                .beginUniqueWork(vaccination.notificationUUID2.toString(), ExistingWorkPolicy.REPLACE, secondNotificationWorkerRequest)
                .enqueue()
            //Log.i("UISTATE__NAME", vaccination.name)
        } else if (LocalDate.now().isEqual(vaccination.date)) {
            WorkManager.getInstance(application.applicationContext)
                .beginUniqueWork(vaccination.notificationUUID2.toString(), ExistingWorkPolicy.REPLACE, secondNotificationWorkerRequest)
                .enqueue()
        }
    }

    /**
     * Schedule a notification to be triggered day before the vaccination date
     * and another on day of vaccine to confirm if vaccine has been administered
     */
    override fun schedule(vaccination: Vaccination, flock: FlockUiState) {
        val firstNotificationData = buildDataObjectFirstNotification(
            vaccination = vaccination,
            flock = flock,
            notificationID = vaccination.id
        )
        val secondNotificationData = buildDataObjectSecondNotification(
            vaccination = vaccination,
            flock = flock,
            notificationID = vaccination.id
        )

        val firstNotificationWorkerRequest =
            OneTimeWorkRequest.Builder(VaccinationReminderWorker::class.java)
                .setInputData(firstNotificationData)
                .setInitialDelay(
                    DateUtils().calculateVaccineNotificationDate(
                        vaccination = vaccination,
                        hour = 8, minutes = 0
                    ), TimeUnit.MINUTES
                )
                .build()



        val secondNotificationWorkerRequest =
            OneTimeWorkRequest.Builder(VaccinationConfirmationWorker::class.java)
                .setInputData(secondNotificationData)
                .setInitialDelay(
                    DateUtils().calculateConfirmVaccineNotificationDate(
                        vaccination = vaccination,
                        hour = 10, minutes = 0
                    ), TimeUnit.MINUTES
                )
                .build()


        if (LocalDate.now().isBefore(vaccination.date)) {
            WorkManager.getInstance(application.applicationContext)
                .beginUniqueWork(vaccination.notificationUUID.toString(), ExistingWorkPolicy.REPLACE, firstNotificationWorkerRequest)
                .enqueue()

            WorkManager.getInstance(application.applicationContext)
                .beginUniqueWork(vaccination.notificationUUID2.toString(), ExistingWorkPolicy.REPLACE, secondNotificationWorkerRequest)
                .enqueue()
        } else if (LocalDate.now().isEqual(vaccination.date)) {
            WorkManager.getInstance(application.applicationContext)
                .beginUniqueWork(vaccination.notificationUUID2.toString(), ExistingWorkPolicy.REPLACE, secondNotificationWorkerRequest)
                .enqueue()
        }
    }

    fun buildDataObjectFirstNotification(
        vaccination: Vaccination,
        flock: FlockUiState,
        notificationID: Int
    ): Data {
        return Data.Builder().apply {
            putString(TITLE, "Vaccination Reminder")
            putString(CONTENT_TEXT, "${vaccination.name} vaccination due.")
            putString(
                BIG_TEXT_CONTENT,
                "${vaccination.name} vaccination for ${flock.batchName} is due tomorrow, " +
                        "${DateUtils().dateToStringLongFormat(vaccination.date)}."
            )
            putInt(FLOCK_ID, flock.id)
            putInt(VACCINE_NOTIFICATION_ID, vaccination.notificationUUID.hashCode())
            putString(VACCINATION_NOTIFICATION_UUID, vaccination.notificationUUID.toString())
            putString(VACCINATION_NOTIFICATION_UUID2, vaccination.notificationUUID2.toString())
        }.build()
    }

    fun buildDataObjectSecondNotification(
        vaccination: Vaccination,
        flock: FlockUiState,
        notificationID: Int
    ): Data {
        return Data.Builder().apply {
            putString(TITLE_TWO, "Vaccination Reminder")
            putString(CONTENT_TEXT_TWO, "${vaccination.name} vaccination due.")
            putString(
                BIG_TEXT_CONTENT_TWO,
                "${vaccination.name} vaccination for ${flock.batchName} is due today, " +
                        "Have you administered the vaccine?"
            )
            putInt(FLOCK_ID, flock.id)
            putInt(VACCINE_NOTIFICATION_ID, vaccination.notificationUUID2.hashCode())
            putInt(VACCINATION_ID, notificationID)
            putString(VACCINATION_FLOCK_UNIQUE_ID, vaccination.flockUniqueId)
            putString(VACCINATION_NAME, vaccination.name)
            putString(VACCINATION_NOTES, vaccination.notes)
            putString(VACCINATION_DATE, DateUtils().dateToStringLongFormat(vaccination.date))
            putString(VACCINATION_NOTIFICATION_UUID, vaccination.notificationUUID.toString())
            putString(VACCINATION_NOTIFICATION_UUID2, vaccination.notificationUUID2.toString())
            putBoolean(VACCINATION_ADMINISTERED, vaccination.hasVaccineBeenAdministered)
        }.build()
    }


    override fun cancelNotification(vaccination: Vaccination) {
        WorkManager.getInstance(application.applicationContext)
            .cancelUniqueWork(vaccination.notificationUUID.toString())
        WorkManager.getInstance(application.applicationContext)
            .cancelUniqueWork(vaccination.notificationUUID2.toString())

    }

}