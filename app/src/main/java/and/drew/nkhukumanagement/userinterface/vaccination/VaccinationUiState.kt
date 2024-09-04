package and.drew.nkhukumanagement.userinterface.vaccination

import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.utils.DateUtils
import android.os.Build
import android.os.Parcelable
import androidx.annotation.RequiresApi
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 * Represents the UI state for [AddVaccinationsScreen].
 */
data class VaccinationUiState(
    val id: Int = 0,
    private var flockUniqueId: String = "",
    val vaccinationNumber: Int = 1,
    private var name: String = "",
    private var date: String = "",
    val method: String = "",
    val notes: String = "",
    val actionEnabled: Boolean = false,
    var isExpanded: Boolean = false,
    val notificationUUID: UUID = UUID.randomUUID(),
    val notificationUUID2: UUID = UUID.randomUUID(),
    val vaccineAdministered: Boolean = false
)  {
val methodsVaccineAdministration = listOf("Drinking water", "Subcutaneous injection(SC)","Intramuscular injection(IM)","Spray vaccination","Ocular instillation")
    fun setDate(mDate: String) {
        date = mDate
    }

    fun getDate(): String {
        return date
    }

    fun setName(mName: String) {
        name = mName
    }

    fun getName(): String {
        return name
    }

    fun setUniqueId(uniqueID: String) {
        flockUniqueId = uniqueID
    }

    fun getUniqueId(): String {
        return flockUniqueId
    }

}


/**
 * Extension function to convert [VaccinationUiState] to [Vaccination]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun VaccinationUiState.toVaccination(): Vaccination = Vaccination(
    id = id,
    flockUniqueId = getUniqueId(),
    name = getName(),
    date = DateUtils().stringToLocalDate(getDate()),
    notes = notes,
    hasVaccineBeenAdministered = vaccineAdministered,
    notificationUUID = notificationUUID,
    notificationUUID2 = notificationUUID2,
    method = method
)

/**
 * Extension function to convert [Vaccination] to [VaccinationUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Vaccination.toVaccinationUiState(
    enabled: Boolean = false,
    vaccinationNumber: Int = 1
): VaccinationUiState = VaccinationUiState(
    id = id,
    flockUniqueId = flockUniqueId,
    name = name,
    date = DateUtils().dateToStringLongFormat(date),
    notes = notes,
    actionEnabled = enabled,
    vaccinationNumber = vaccinationNumber,
    notificationUUID = notificationUUID,
    notificationUUID2 = notificationUUID2,
    vaccineAdministered = hasVaccineBeenAdministered
)

/**
 * Check if entry entered is valid
 */
@RequiresApi(Build.VERSION_CODES.O)
fun VaccinationUiState.isValid(): Boolean {
    return getName().isNotBlank() &&
            getDate().isNotBlank()
}

fun VaccinationUiState.isSingleEntryValid(value: String): Boolean {
    return value.isBlank()
}