package and.drew.nkhukumanagement.utils

import java.util.Locale

fun convertWeight(
    kg: Double?,
    unitPreference: String
): Double? {
    return when (unitPreference) {
        "Gram (g)" -> kg?.times(1000)
        "Pound (lb)" -> kg?.times(2.20462)
        "Ounce (oz)" -> kg?.times(35.274)
        else -> kg // default to kilograms
    }
}

fun formatConsumption(valueInKg: Double?, unitPreference: String): String {
    val converted = convertWeight(valueInKg, unitPreference)
    return String.format(Locale.getDefault(), "%.2f", converted)
}

fun convertFromKg(kg: Double, unit: String): Double {
    return when (unit) {
        "Pound (lb)" -> kg * 2.20462
        "Gram (g)"-> kg * 1000
        "Ounce (oz)" -> kg * 35.274
        else -> kg
    }
}

fun convertToKg(value: Double?, fromUnit: String): Double? {
    return when (fromUnit) {
        "Pound (lb)" -> value?.times(0.453592)
        "Gram (g)" -> value?.div(1000)
        "Ounce (oz)" -> value?.times(0.0283495)
        else -> value
    }
}

fun formatToKgConsumption(value: Double?, unitPreference: String): String {
    val converted = convertToKg(value, unitPreference)
    return String.format(Locale.getDefault(), "%.2f", converted)
}