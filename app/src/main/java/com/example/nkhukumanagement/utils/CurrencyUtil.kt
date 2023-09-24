package com.example.nkhukumanagement.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun formatAmount(amount: Float): String {
    return AmountDecimalFormat.format(amount)
}

private val AmountDecimalFormat = DecimalFormat("#,###.##")

fun currencySymbol() = NumberFormat.getCurrencyInstance(Locale("en", "ZM")).currency?.symbol
fun currencyFormatter(currency: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "ZM"))
    return formatter.format(currency)
}