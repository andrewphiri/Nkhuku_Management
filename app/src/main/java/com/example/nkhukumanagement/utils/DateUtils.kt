package com.example.nkhukumanagement.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class DateUtils {

    /**
     * Function to format
     * @param date to a string
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun convertLocalDateToString(date: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())

        //Convert the passed in date to a Long in millis
        val timeInMillis = LocalDate.parse(date.format(dateFormatter), dateFormatter)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        //Then convert the timeInMillis to a LocalDate object
        val dateSelectedInMillis = Instant.ofEpochMilli(timeInMillis)
            .atZone(ZoneId.systemDefault()).toLocalDate()

        //Format dateSelectedInMillis to a string and Return
        return dateFormatter.format(
            dateSelectedInMillis
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertMillisToLocalDate(millis: Long) : LocalDate {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault()).toLocalDate()
    }

    /**
     * Convert a string to a local date object
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToLocalDate(date: String) : LocalDate {
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())
        return LocalDate.parse(date, dateFormatter)
    }
}