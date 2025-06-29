package and.drew.nkhukumanagement.userinterface.planner

import and.drew.nkhukumanagement.utils.convertWeight
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Represents the Ui State of the [PlannerScreen]
 */
data class PlannerUiState(
    val quantityToOrder: String = "",
    val areFeedersAvailable: Boolean = false,
    val areDrinkersAvailable: Boolean = false,
    val areHeatersAvailable: Boolean = false,
    val flockType: String = "",
) {
    fun calculateStarter(unitPreference: String): Double {
        return quantityToOrder.toDouble().times (convertWeight(1.0, unitPreference = unitPreference) ?: 0.0)
    }

    fun calculateGrower(unitPreference: String): Double {
        return quantityToOrder.toDouble().times (convertWeight(1.5, unitPreference = unitPreference) ?: 0.0)
    }

    fun calculateFinisher(unitPreference: String): Double {
        return quantityToOrder.toDouble().times(convertWeight(1.5, unitPreference = unitPreference) ?: 0.0)
    }

    fun totalFeed(unitPreference: String): String {
        return String.format(Locale.getDefault(),"%.2f", calculateStarter(unitPreference) + calculateGrower(unitPreference) + calculateFinisher(unitPreference))
    }

    fun calculateStarterBags(bagSize: Int, unitPreference: String): Int {
        return (calculateStarter(unitPreference) / bagSize).roundToInt()
    }

    fun calculateGrowerBags(bagSize: Int, unitPreference: String): Int {
        return (calculateGrower(unitPreference) / bagSize).roundToInt()
    }

    fun calculateFinisherBags(bagSize: Int, unitPreference: String): Int {
        return (calculateFinisher(unitPreference) / bagSize).roundToInt()
    }

    fun totalBags(bagSize: Int, unitPreference: String): Int {
        return calculateStarterBags(bagSize, unitPreference) + calculateGrowerBags(bagSize, unitPreference) + calculateGrowerBags(bagSize, unitPreference)
    }

    fun calculateChickTrays(): Int {
        return ((quantityToOrder.toDouble() / 100) * 3).roundToInt()
    }

    fun calculateSmallFeeders(): Int {
        return ((quantityToOrder.toDouble() / 100) * 3).roundToInt()
    }

    fun calculateBigFeeders(): Int {
        return ((quantityToOrder.toDouble() / 100) * 3).roundToInt()
    }

    fun calculateSmallDrinkers(): Int {
        return ((quantityToOrder.toDouble() / 100) * 3).roundToInt()
    }

    fun calculateBigDrinkers(): Int {
        return ((quantityToOrder.toDouble() / 100) * 3).roundToInt()
    }

    fun calculateAutomaticDrinkers(): Int {
        return ((quantityToOrder.toDouble() / 100) * 1).roundToInt()
    }

    fun calculateNippleDrinkers(): Int {
        return ((quantityToOrder.toDouble() / 100) * 10).roundToInt()
    }

    fun calculateLayerStarter(unitPreference: String): Double {
        return quantityToOrder.toDouble().times (convertWeight(0.054, unitPreference = unitPreference) ?: 0.0)
    }

    fun calculatePulletLayerStarter(unitPreference: String): Double {
        return quantityToOrder.toDouble().times (convertWeight(0.190, unitPreference = unitPreference ) ?: 0.0)
    }

    fun calculatePulletGrower(unitPreference: String): Double {
        return quantityToOrder.toDouble().times (convertWeight(0.444, unitPreference = unitPreference) ?: 0.0)
    }

    fun calculatePreLayer(unitPreference: String): Double {
        return quantityToOrder.toDouble().times (convertWeight(0.330, unitPreference = unitPreference) ?: 0.0)
    }

    fun calculateLayersMash(unitPreference: String): Double {
        return quantityToOrder.toDouble().times (convertWeight(5.64, unitPreference = unitPreference) ?: 0.0)
    }

    fun totalFeedLayers(unitPreference: String): String {
        return String.format(Locale.getDefault(),"%.2f", calculateLayerStarter(unitPreference) + calculatePulletLayerStarter(unitPreference) + calculatePulletGrower(unitPreference) + calculatePreLayer(unitPreference) + calculateLayersMash(unitPreference))
    }

    fun calculateLayerStarterBags(bagSize: Int, unitPreference: String): Int {
        return (calculateLayerStarter(unitPreference) / bagSize).roundToInt()
    }

    fun calculatePulletLayerStarterBags(bagSize: Int,unitPreference: String): Int {
        return (calculatePulletLayerStarter(unitPreference) / bagSize).roundToInt()
    }

    fun calculatePulletGrowerBags(bagSize: Int, unitPreference: String): Int {
        return (calculatePulletGrower(unitPreference) / bagSize).roundToInt()
    }

    fun calculatePreLayerBags(bagSize: Int, unitPreference: String): Int {
        return (calculatePreLayer(unitPreference) / bagSize).roundToInt()
    }

    fun calculateLayerMashBags(bagSize: Int, unitPreference: String): Int {
        return (calculateLayersMash(unitPreference) / bagSize).roundToInt()
    }

    fun totalLayersBags(bagSize: Int, unitPreference: String): Int {
        return calculateLayerStarterBags(bagSize, unitPreference)+ calculatePulletLayerStarterBags(bagSize, unitPreference)+ calculatePulletGrowerBags(bagSize, unitPreference) + calculatePreLayerBags(bagSize, unitPreference) + calculateLayerMashBags(bagSize, unitPreference)
    }
}

/**
 * Handle [NumberFormatException]
 */
fun checkNumberExceptions(plannerUiState: PlannerUiState, bagSize: Int, unitPreference: String): Boolean {
    return try {
        plannerUiState.toPlanner(bagSize, unitPreference)
        true
    } catch (e: NumberFormatException) {
        false
    }
}

/**
 * Extension function to convert [PlannerUiState] to [Planner]
 */
fun PlannerUiState.toPlanner(bagSize: Int, unitPreference: String): Planner =
        Planner(
            quantityToOrder = quantityToOrder.toInt(),
            areFeedersAvailable = areFeedersAvailable,
            areDrinkersAvailable = areDrinkersAvailable,
            starterNeeded = calculateStarter(unitPreference),
            growerNeeded = calculateGrower(unitPreference),
            finisherNeeded = calculateFinisher(unitPreference),
            chicksTray = calculateChickTrays(),
            bigDrinkersNeeded = calculateBigDrinkers(),
            smallDrinkersNeeded = calculateSmallDrinkers(),
            bigFeedersNeeded = calculateBigFeeders(),
            smallFeedersNeeded = calculateSmallFeeders(),
            automaticDrinkers = calculateAutomaticDrinkers(),
            nippleDrinkers = calculateNippleDrinkers(),
            totalBags = totalBags(bagSize, unitPreference),
            totalFeed = totalFeed(unitPreference).toDouble(),
            totalFinisherBags = calculateFinisherBags(bagSize, unitPreference),
            totalGrowerBags = calculateGrowerBags(bagSize, unitPreference),
            totalStarterBags = calculateStarterBags(bagSize, unitPreference),
        )


/**
 * Check if quantityToOrder is not blank
 */
fun PlannerUiState.isValid(): Boolean {
    return quantityToOrder.isNotBlank() &&
            flockType.isNotBlank()
}