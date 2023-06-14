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
    val flockUniqueId: String = "",
    val vaccinationNumber: Int = 1,
    private var name: String = "",
    private var date: String = "",
    val notes: String = "",
    val actionEnabled: Boolean = false
) : Parcelable {

    val options = listOf("Gumburo", "Lasota")
    fun setDate(mDate: String) {
        date = derivedStateOf { mDate }.value
    }
    fun getDate(): String {
        return date
    }

    fun setName(mName: String) {
        name = derivedStateOf { mName }.value
    }
    fun getName(): String {
        return name
    }
//    fun setNotes(mNotes: String) {
//        notes = mNotes
//    }
//    fun getNotes(): String {
//        return notes
//    }

}



/**
 * Extension function to convert [VaccinationUiState] to [Vaccination]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun VaccinationUiState.toVaccination(): Vaccination = Vaccination(
    id = id,
    flockUniqueId = flockUniqueId,
    name  = getName(),
    date = DateUtils().stringToLocalDate(getDate()),
    notes = notes
)

/**
 * Extension function to convert [Vaccination] to [VaccinationUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Vaccination.toVaccinationUiState(enabled: Boolean = false) : VaccinationUiState = VaccinationUiState(
    id = id,
    flockUniqueId = flockUniqueId,
    name = name,
    date = DateUtils().convertLocalDateToString(date),
    notes = notes,
    actionEnabled = enabled
)

@RequiresApi(Build.VERSION_CODES.O)
fun VaccinationUiState.isValid() : Boolean {
    return getName().isNotBlank() &&
            getDate().isNotBlank()
}

fun VaccinationUiState.isSingleEntryValid(value: kotlin.String): Boolean {
    return value.isBlank()
}