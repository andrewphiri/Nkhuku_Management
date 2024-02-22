package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.userinterface.feed.FeedUiState
import and.drew.nkhukumanagement.userinterface.vaccination.VaccinationUiState
import and.drew.nkhukumanagement.userinterface.weight.WeightUiState
import and.drew.nkhukumanagement.utils.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class DateUtilTests {
    val dateUtil = DateUtils()

    @Test
    fun dateToStringLongFormat() {
        val myDate = dateUtil.dateToStringLongFormat(
            LocalDate.of(
                2024, 1, 25
            )
        )
        assertEquals(
            "Thursday, 25 January, 2024",
            myDate
        )
    }

    @Test
    fun dateToStringShortFormat() {
        val myDate = dateUtil.dateToStringShortFormat(
            LocalDate.of(
                2024, 1, 25
            )
        )
        assertEquals(
            "25 Jan, 2024",
            myDate
        )
    }

    @Test
    fun stringToLocalDateLongFormat() {
        val myDate = dateUtil.stringToLocalDate("Thursday, 25 January, 2024")
        assertEquals(
            LocalDate.of(
                2024, 1, 25
            ),
            myDate
        )
    }

    @Test
    fun stringToLocalDateShortFormat() {
        val myDate = dateUtil.stringToLocalDateShortFormat("25 Jan, 2024")
        assertEquals(
            LocalDate.of(
                2024, 1, 25
            ),
            myDate
        )
    }

    @Test
    fun calculateAge() {
        val flockAge = dateUtil.calculateAge(
            LocalDate.of(
                2024, 1, 1
            )
        )
        assertEquals(25, flockAge)
    }

    @Test
    fun calculateVaccinationDate() {
        val vaccinationUiState = VaccinationUiState(
            id = 1, name = "Gumburro",
            flockUniqueId = "1",
            notes = ""
        )
        val calculateVaccineDate = dateUtil.vaccinationDate(
            date = LocalDate.of(
                2024, 1, 1
            ),
            day = 10,
            vaccinationUiState = vaccinationUiState
        )
        assertEquals("Thursday, 11 January, 2024", calculateVaccineDate)
    }

    @Test
    fun calculateFeedDate() {
        val feedUiState = FeedUiState()
        val calculateFeedDate = dateUtil.feedDate(
            date = LocalDate.of(
                2024, 1, 1
            ),
            day = 7,
            feedUiState
        )
        assertEquals("Monday, 08 January, 2024", calculateFeedDate)
    }

    @Test
    fun calculateWeightDate() {
        val weightUiState = WeightUiState()
        val calculateWeightDate = dateUtil.weightDate(
            date = LocalDate.of(
                2024, 1, 1
            ),
            day = 14,
            weightUiState
        )
        assertEquals("Monday, 15 January, 2024", calculateWeightDate)
    }

    @Test
    fun calculateNotificationDate() {
        val vaccination = Vaccination(
            id = 1, name = "Gumburro",
            flockUniqueId = "1",
            notes = "",
            date = LocalDate.of(
                2024, 1, 1
            ),
            hasVaccineBeenAdministered = false,
            notificationUUID = UUID.randomUUID()
        )
        val calculateVaccineAlarmDate = dateUtil.calculateVaccineNotificationDate(vaccination)
        val actualAlarmDateLong = LocalDate.of(
            2024, 1, 1
        ).minusDays(1).atTime(8, 0)
            .atZone(ZoneId.systemDefault())
            .toEpochSecond() * 1000
        assertEquals(actualAlarmDateLong, calculateVaccineAlarmDate)
    }

    /**
     * Check if notification date set is before date of vaccine
     * Vaccine date 2024-01-01 at 08:00
     * Notification Date 2023-12-31 at 08:00
     * In this case -1440 should the difference in minutes between the two dates
     */
    @Test
    fun calculateVaccineNotificationDateTests() {
        val vaccination = Vaccination(
            id = 1, name = "Gumburro",
            flockUniqueId = "1",
            notes = "",
            date = LocalDate.of(
                2024, 1, 1
            ),
            hasVaccineBeenAdministered = false,
            notificationUUID = UUID.randomUUID()
        )
        val notificationTime = dateUtil.calculateVaccineNotificationDate(
            currentTime = LocalDateTime.of(2024, 1, 1, 8, 0),
            vaccination = vaccination, hour = 8, minutes = 0
        )
        assertEquals(-1440, notificationTime)
    }

    /**
     * Check if notification date set on date of vaccine
     *  Vaccine date 2024-01-01 at 08:00
     *  Notification Date 2024-01-01 at 08:00
     * In this case 0 should the difference in minutes between the two dates
     */
    @Test
    fun calculateConfirmVaccineNotificationDateTests() {
        val vaccination = Vaccination(
            id = 1, name = "Gumburro",
            flockUniqueId = "1",
            notes = "",
            date = LocalDate.of(
                2024, 1, 1
            ),
            hasVaccineBeenAdministered = false,
            notificationUUID = UUID.randomUUID()
        )
        val notificationTime = dateUtil.calculateConfirmVaccineNotificationDate(
            currentTime = LocalDateTime.of(2024, 1, 1, 7, 0),
            vaccination = vaccination, hour = 8, minutes = 0
        )
        assertEquals(0, notificationTime)
    }
}