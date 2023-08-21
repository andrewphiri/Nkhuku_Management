package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.utils.DateUtils
import kotlinx.parcelize.Parcelize


/**
 * Represents the UI state for a flock
 */
@Parcelize
@RequiresApi(Build.VERSION_CODES.O)
data class FlockUiState(
    val id: Int = 0,
    private var uniqueId: String = "",
    val batchName: String = "",
    val breed: String = "",
    private var datePlaced: String = "",
    val quantity: String = "",
    val donorFlock: String = "",
    private var stock: String = "0",
    private var mortality: String = "0",
    val imageResourceId: Int = R.drawable.chicken,
    private var culls: String = "0",
    val enabled: Boolean = false
) : Parcelable {
    val options = mutableListOf("Hybrid", "Ross", "Zamhatch")

    fun setDate(date: String) {
        datePlaced = derivedStateOf { date }.value
    }

    fun getDate(): String {
        return datePlaced
    }

    fun setUniqueId(uniqueID: String) {
        uniqueId = uniqueID
    }

    fun getUniqueId(): String {
        return uniqueId
    }

    fun setMortality(mMortality: String) {
        mortality = mMortality
    }

    fun getMortality(): String {
        return mortality
    }

    fun setCulls(mCulls: String) {
        culls = derivedStateOf { mCulls }.value
    }

    fun getCulls(): String {
        return culls
    }

    fun setStock(quantity: String, donorFlock: String) {
        stock = (quantity.toInt() + donorFlock.toInt()).toString()
    }

    fun getStock(): String {
        return stock
    }

    fun getBirdsRemaining(): Int = getStock().toInt() - mortality.toInt()

}

/**
 * Extension function to convert [FlockUIState] to [Flock]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun FlockUiState.toFlock(): Flock = Flock(
    id = id,
    uniqueId = getUniqueId(),
    batchName = batchName,
    breed = breed,
    datePlaced = DateUtils().stringToLocalDate(getDate()),
    numberOfChicksPlaced = quantity.toInt(),
    donorFlock = donorFlock.toIntOrNull() ?: 0,
    mortality = getMortality().toInt(),
    stock = getStock().toInt(),
    culls = getCulls().toInt()
)

@RequiresApi(Build.VERSION_CODES.O)
fun checkNumberExceptions(flockUiState: FlockUiState): Boolean {
   return try {
        flockUiState.toFlock()
       true
    }catch (e: NumberFormatException) {
       false
    }
}

/**
 * Extension function to convert [Flock] to [FlockUiState]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun Flock.toFlockUiState(enabled: Boolean = false): FlockUiState =
    FlockUiState(
        id = id,
        uniqueId = uniqueId,
        batchName = batchName,
        breed = breed,
        datePlaced = DateUtils().convertLocalDateToString(datePlaced),
        quantity = numberOfChicksPlaced.toString(),
        donorFlock = donorFlock.toString(),
        mortality = mortality.toString(),
        culls = culls.toString(),
        stock = stock.toString(),
        enabled = enabled
    )


@RequiresApi(Build.VERSION_CODES.O)
fun FlockUiState.isValid(): Boolean {
    Log.i(
        "ENABLED", "breed = ${breed.isNotBlank()} " +
                " Date = ${getDate().isNotBlank()} " +
                " quantity = ${quantity.isNotBlank()} " +
                " donor = ${donorFlock.isNotBlank()}"
    )
    return breed.isNotBlank() &&
            getDate().isNotBlank() &&
            quantity.isNotBlank() &&
            donorFlock.isNotBlank()
            && batchName.isNotBlank()
}

fun FlockUiState.isSingleEntryValid(value: String): Boolean {
    return value.isBlank()
}



