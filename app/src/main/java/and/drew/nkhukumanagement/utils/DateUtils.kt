package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.userinterface.feed.FeedUiState
import and.drew.nkhukumanagement.userinterface.vaccination.VaccinationUiState
import and.drew.nkhukumanagement.userinterface.weight.WeightUiState
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class DateUtils {

    /**
     * Function to format
     * @param date to a string
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateToStringLongFormat(date: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())

        val dateSelectedInMillis = convertLocalDate(date, dateFormatter)

        //Format dateSelectedInMillis to a string and Return
        return dateFormatter.format(
            dateSelectedInMillis
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateToStringShortFormat(date: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.getDefault())

        val dateSelectedInMillis = convertLocalDate(date, dateFormatter)

        return dateFormatter.format(
            dateSelectedInMillis
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertLocalDate(date: LocalDate, dateFormatter: DateTimeFormatter): LocalDate {
        //Convert the passed in date to a Long in millis
        val timeInMillis = LocalDate.parse(date.format(dateFormatter), dateFormatter)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        //Then convert the timeInMillis to a LocalDate object
        return Instant.ofEpochMilli(timeInMillis)
            .atZone(ZoneId.systemDefault()).toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertMillisToLocalDate(millis: Long): LocalDate {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault()).toLocalDate()
    }

    /**
     * Convert a string to a local date object
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToLocalDate(date: String?): LocalDate {
        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy", Locale.getDefault())
        val timeInMillis = LocalDate.parse(date, dateFormatter)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return Instant.ofEpochMilli(timeInMillis)
            .atZone(ZoneId.systemDefault()).toLocalDate()
    }

    /**
     * Convert a string to a local date object
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToLocalDateShortFormat(myDate: String): LocalDate {
        val dateFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.getDefault())
        val timeInMillis = LocalDate.parse(myDate, dateFormatter)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return Instant.ofEpochMilli(timeInMillis)
            .atZone(ZoneId.systemDefault()).toLocalDate()
    }

    /**
     * Function to calculate vaccination dates based on date
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateDate(date: LocalDate, day: Long): LocalDate {
        return date.plusDays(day)
    }

    /**
     * Function to set vaccination date
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun vaccinationDate(
        date: LocalDate,
        day: Long,
        vaccinationUiState: VaccinationUiState
    ): String {
        val calculateDate = calculateDate(date = date, day = day)
        val dateToString = dateToStringLongFormat(calculateDate)
        vaccinationUiState.setDate(dateToString)
        return vaccinationUiState.getDate()
    }

    /**
     * Function to set weight date
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun weightDate(date: LocalDate, day: Long, weightUiState: WeightUiState): String {
        val calculateDate = calculateDate(date = date, day = day)
        val dateToString = dateToStringLongFormat(calculateDate)
        weightUiState.setDate(dateToString)
        return weightUiState.getDate()
    }

    /**
     * Function to set weight date
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun feedDate(date: LocalDate, day: Long, feedUiState: FeedUiState): String {
        val calculateDate = calculateDate(date = date, day = day)
        val dateToString = dateToStringLongFormat(calculateDate)
        feedUiState.setDate(dateToString)
        return feedUiState.getDate()
    }

    /**
     * Fun to calculate age of birds
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateAge(birthDate: LocalDate): Long {
        return ChronoUnit.DAYS.between(birthDate.minusDays(1), LocalDate.now())
    }

    /**
     * Set alarm date to day before the vaccination
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateVaccineNotificationDate(vaccination: Vaccination): Long {
        return vaccination.date.minusDays(1).atTime(21, 40)
            .atZone(ZoneId.systemDefault())
            .toEpochSecond()
//        LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC)

    }

    /**
     * Set notification date to day before the vaccination
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateVaccineNotificationDate(
        currentTime: LocalDateTime = LocalDateTime.now(),
        vaccination: Vaccination,
        hour: Int,
        minutes: Int
    ): Long {
        return ChronoUnit.MINUTES.between(
            currentTime.plusDays(1),
            vaccination.date.atTime(hour, minutes)
        )
    }

    /**
     * Set notification date on the day of the vaccination
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateConfirmVaccineNotificationDate(
        currentTime: LocalDateTime = LocalDateTime.now(),
        vaccination: Vaccination,
        hour: Int,
        minutes: Int
    ): Long {
        return ChronoUnit.MINUTES.between(
            currentTime,
            vaccination.date.atTime(hour, minutes)
        )
    }
}