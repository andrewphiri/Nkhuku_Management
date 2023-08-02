package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.nkhukumanagement.data.Vaccination
import com.example.nkhukumanagement.utils.DateUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class VaccinationUiState(
    val id: Int = 0,
    private var flockUniqueId: String = "",
    val vaccinationNumber: Int = 1,
    private var name: String = "",
    private var date: String = "",
    val notes: String = "",
    val actionEnabled: Boolean = false,
    var isExpanded: Boolean = false
) : Parcelable {

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
    name  = getName(),
    date = DateUtils().stringToLocalDate(getDate()),
    notes = notes
)

/**
 * Extension function to convert [Vaccination] to [VaccinationUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Vaccination.toVaccinationUiState(enabled: Boolean = false, vaccinationNumber: Int = 1) : VaccinationUiState = VaccinationUiState(
    id = id,
    flockUniqueId = flockUniqueId,
    name = name,
    date = DateUtils().convertLocalDateToString(date),
    notes = notes,
    actionEnabled = enabled,
    vaccinationNumber = vaccinationNumber
)

@RequiresApi(Build.VERSION_CODES.O)
fun VaccinationUiState.isValid() : Boolean {
    return getName().isNotBlank() &&
            getDate().isNotBlank()
}

fun VaccinationUiState.isSingleEntryValid(value: String): Boolean {
    return value.isBlank()
}