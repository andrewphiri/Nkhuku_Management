package and.drew.nkhukumanagement.userinterface.planner

import kotlin.math.roundToInt

/**
 * Represents the Ui State of the [PlannerScreen]
 */
data class PlannerUiState(
    val quantityToOrder: String = "",
    val areFeedersAvailable: Boolean = false,
    val areDrinkersAvailable: Boolean = false,
    val areHeatersAvailable: Boolean = false,
) {
    fun calculateStarter(): Double {
        return quantityToOrder.toDouble() * 1.0
    }

    fun calculateGrower(): Double {
        return quantityToOrder.toDouble() * 1.5
    }

    fun calculateFinisher(): Double {
        return quantityToOrder.toDouble() * 1.5
    }

    fun totalFeed(): String {
        return String.format("%.2f", calculateStarter() + calculateGrower() + calculateFinisher())
    }

    fun calculateStarterBags(): Int {
        return (calculateStarter() / 50).roundToInt()
    }

    fun calculateGrowerBags(): Int {
        return (calculateGrower() / 50).roundToInt()
    }

    fun calculateFinisherBags(): Int {
        return (calculateFinisher() / 50).roundToInt()
    }

    fun totalBags(): Int {
        return calculateStarterBags() + calculateGrowerBags() + calculateGrowerBags()
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
}

/**
 * Handle [NumberFormatException]
 */
fun checkNumberExceptions(plannerUiState: PlannerUiState): Boolean {
    return try {
        plannerUiState.toPlanner()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

/**
 * Extension function to convert [PlannerUiState] to [Planner]
 */
fun PlannerUiState.toPlanner(): Planner =
        Planner(
            quantityToOrder = quantityToOrder.toInt(),
            areFeedersAvailable = areFeedersAvailable,
            areDrinkersAvailable = areDrinkersAvailable,
            starterNeeded = calculateStarter(),
            growerNeeded = calculateGrower(),
            finisherNeeded = calculateFinisher(),
            chicksTray = calculateChickTrays(),
            bigDrinkersNeeded = calculateBigDrinkers(),
            smallDrinkersNeeded = calculateSmallDrinkers(),
            bigFeedersNeeded = calculateBigFeeders(),
            smallFeedersNeeded = calculateSmallFeeders(),
            automaticDrinkers = calculateAutomaticDrinkers(),
            nippleDrinkers = calculateNippleDrinkers(),
            totalBags = totalBags(),
            totalFeed = totalFeed().toDouble(),
            totalFinisherBags = calculateFinisherBags(),
            totalGrowerBags = calculateGrowerBags(),
            totalStarterBags = calculateStarterBags(),
        )


/**
 * Check if quantityToOrder is not blank
 */
fun PlannerUiState.isValid(): Boolean {
    return quantityToOrder.isNotBlank()
}