package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.utils.DateUtils
import kotlinx.parcelize.Parcelize


/**
 * Represents the UI state for a flock
 */
@Parcelize
@RequiresApi(Build.VERSION_CODES.O)
data class FlockUiState (
    val id: Int = 0,
    val breed: String = "",
    private var datePlaced: String = "",
    val quantity: String = "",
    val donorFlock: String = "",
    val mortality: Int = 0,
    val imageResourceId: Int = R.drawable.chicken,
    val culls: String = "",
    val enabled : Boolean = false
) : Parcelable {
    val options = listOf("Hybrid", "Ross", "Zamhatch", "Other")

    fun setDate(date: String) {
        datePlaced = derivedStateOf { date }.value
    }
    fun getDate(): String {
        return datePlaced
    }

}
    /**
     * Extension function to convert [FlockUIState] to [Flock]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun FlockUiState.toFlock(): Flock = Flock(
        id = id,
        breed = breed,
        datePlaced = DateUtils().stringToLocalDate(getDate()),
        numberOfChicksPlaced = quantity.toInt(),
        donorFlock = donorFlock.toIntOrNull() ?: 0,
        mortality = mortality,
        culls = culls.toInt()

    )

    /**
     * Extension function to convert [Flock] to [FlockUiState]
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun Flock.toFlockUiState(enabled: Boolean = false): FlockUiState =
        FlockUiState(
            id = id,
            breed = breed,
            datePlaced = DateUtils().convertLocalDateToString(datePlaced),
            quantity = numberOfChicksPlaced.toString(),
            donorFlock = donorFlock.toString(),
            mortality = mortality,
            culls = culls.toString(),
            enabled = enabled
        )


    @RequiresApi(Build.VERSION_CODES.O)
    fun FlockUiState.isValid() : Boolean {
        Log.i("ENABLED", "breed = ${breed.isNotBlank()} " +
                " Date = ${getDate().isNotBlank()} " +
                " quantity = ${quantity.isNotBlank()} " +
                " donor = ${donorFlock.isNotBlank()}")
        return  breed.isNotBlank() &&
                getDate().isNotBlank() &&
                quantity.isNotBlank() &&
                donorFlock.isNotBlank()
    }

    fun FlockUiState.isSingleEntryValid(value: String): Boolean{
    return value.isBlank()
}



