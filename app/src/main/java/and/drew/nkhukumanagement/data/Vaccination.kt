package and.drew.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

/**
 * Data class that represent a table in the database.
 */
@Entity(tableName = "vaccinations")
data class Vaccination(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flockUniqueId: String,
    val name: String,
    val method: String,
    val date: LocalDate,
    val notes: String,
    val notificationUUID: UUID,
    val hasVaccineBeenAdministered: Boolean
)
