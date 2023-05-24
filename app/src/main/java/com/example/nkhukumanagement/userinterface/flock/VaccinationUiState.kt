package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.derivedStateOf
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.data.Vaccination
import com.example.nkhukumanagement.utils.DateUtils

data class VaccinationUiState(
    val id: Int = 0,
    private var name: String = "",
    private var date: String = "",
    val actionEnabled: Boolean = false
){
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
}
/**
 * Extension function to convert [VaccinationUiState] to [Vaccination]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun VaccinationUiState.toVaccination(): Vaccination = Vaccination(
    id = id,
    name  = getName(),
    date = DateUtils().stringToLocalDate(getDate())
)

/**
 * Extension function to convert [Vaccination] to [VaccinationUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Vaccination.toVaccinationUiState(enabled: Boolean = false) : VaccinationUiState = VaccinationUiState(
    id = id,
    name = name,
    date = DateUtils().convertLocalDateToString(date),
    actionEnabled = enabled
)

@RequiresApi(Build.VERSION_CODES.O)
fun VaccinationUiState.isValid() : Boolean {
    return getName().isNotBlank() && getDate().isNotBlank()
}

fun VaccinationUiState.isSingleEntryValid(value:String): Boolean {
    return value.isBlank()
}