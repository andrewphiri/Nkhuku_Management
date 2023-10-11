package and.drew.nkhukumanagement.data

import androidx.compose.ui.graphics.Color

data class Account(
    val color: Color,
    val description: String,
    val amount: Double,
    val net: Double = 0.0,
    val total: Double = 0.0
)
