package and.drew.nkhukumanagement.utils

import android.icu.util.Currency
import android.icu.util.ULocale
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

fun formatAmount(amount: Float): String {
    return AmountDecimalFormat.format(amount)
}

private val AmountDecimalFormat = DecimalFormat("#,###.##")

fun currencySymbol() = NumberFormat.getCurrencyInstance(Locale("en", "ZM")).currency?.symbol
fun currencyFormatter(currency: Double, currencyLocale: String): String {
    val formatter = NumberFormat.getCurrencyInstance(ULocale(currencyLocale).toLocale())
    return formatter.format(currency)
}

fun getAllCurrenciesInUse(): Map<ULocale, Currency?> {
    val allCurrencies: MutableMap<ULocale, Currency?> = mutableMapOf()
    val localeList = ULocale.getAvailableLocales()
    val date = Date()
    for (locale in localeList) {
        val currencies: Array<out String>? = Currency.getAvailableCurrencyCodes(locale, date)
        if (currencies != null) {
            for (currency in currencies) {
                Currency.getInstance(currency)?.let { currentCurrency ->
                    allCurrencies[locale] = currentCurrency
                }
            }
        }
    }
    return allCurrencies
}