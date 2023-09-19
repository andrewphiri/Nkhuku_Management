package com.example.nkhukumanagement.utils

import java.text.DecimalFormat

fun formatAmount(amount: Float): String {
    return AmountDecimalFormat.format(amount)
}

private val AmountDecimalFormat = DecimalFormat("#,###.##")