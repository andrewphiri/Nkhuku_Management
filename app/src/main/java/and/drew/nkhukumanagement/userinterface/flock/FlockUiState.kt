package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.utils.DateUtils
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.derivedStateOf
import java.time.LocalDate


/**
 * Represents the UI state for [AddFlockScreen] and [FlockDetailsScreen]
 */

@RequiresApi(Build.VERSION_CODES.O)
data class FlockUiState(
    val id: Int = 0,
    private var uniqueId: String = "",
    val batchName: String = "",
    val flockType: String = "",
    val layerType: String = "",
    val breed: String = "",
    private var datePlaced: String = DateUtils().dateToStringLongFormat(LocalDate.now()),
    val quantity: String = "",
    val cost: String = "",
    val donorFlock: String = "",
    private var stock: String = "0",
    private var mortality: String = "0",
    val imageResourceId: Int = R.drawable.add_flock_placeholder,
    private var culls: String = "0",
    val enabled: Boolean = false,
    val active: Boolean = true
) {
    val options = mutableListOf("Hybrid", "Ross", "Ross 308","Zamhatch", "Tiger")
    val flockTypeOptions = mutableListOf("Broiler", "Layer", "Village Chicken")
    val layerTypeOptions = mutableListOf("Hybrid Brown Layer", "Hybrid Zambro", "Lohmann Brown Classic")

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

    /**
     * Calculate total cost of birds ordered.
     * This is used to set the initial account record
     */
    fun totalCostOfBirds(): Double {
        return quantity.toDouble() * cost.toDouble()
    }

    /**
     * Difference between cost and income. This is used to set
     * the initial account record
     */
    fun variance(): Double {
        return 0.0 - totalCostOfBirds()
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
    flockType = flockType,
    layerBreed = layerType,
    breed = breed,
    datePlaced = DateUtils().stringToLocalDate(getDate()),
    numberOfChicksPlaced = quantity.toInt(),
    costPerBird = cost.toDouble(),
    donorFlock = donorFlock.toIntOrNull() ?: 0,
    mortality = getMortality().toInt(),
    stock = getStock().toInt(),
    culls = getCulls().toInt(),
    active = active
)

/**
 * Handle [NumberFormatException]
 */
@RequiresApi(Build.VERSION_CODES.O)
fun checkNumberExceptions(flockUiState: FlockUiState): Boolean {
    return try {
        flockUiState.toFlock()
        true
    } catch (e: NumberFormatException) {
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
        flockType = flockType,
        layerType = layerBreed,
        breed = breed,
        datePlaced = DateUtils().dateToStringLongFormat(datePlaced),
        quantity = numberOfChicksPlaced.toString(),
        cost = costPerBird.toString(),
        donorFlock = donorFlock.toString(),
        mortality = mortality.toString(),
        culls = culls.toString(),
        stock = stock.toString(),
        enabled = enabled,
        active = active
    )

/**
 * Check if entry entered is valid
 */
@RequiresApi(Build.VERSION_CODES.O)
fun FlockUiState.isValid(): Boolean {
    return breed.isNotBlank() &&
            flockType.isNotBlank() &&
            getDate().isNotBlank() &&
            quantity.isNotBlank() &&
            cost.isNotBlank() &&
            donorFlock.isNotBlank() &&
            batchName.isNotBlank() &&
            flockType.isNotBlank()

}

/**
 * Check is entry is valid.Used to display an error on a view
 */
fun FlockUiState.isSingleEntryValid(value: String): Boolean {
    return value.isBlank()
}



