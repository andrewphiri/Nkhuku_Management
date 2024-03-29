package and.drew.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Data class that represent the a table in the database.
 */
@Entity(tableName = "feed")
data class Feed(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val flockUniqueId: String,
    val name: String,
    val week: String,
    val type: String,
    val consumed: Double,
    val standardConsumption: Double = 0.0,
    val actualConsumptionPerBird: Double = 0.0,
    val standardConsumptionPerBird: Double = 0.0,
    val feedingDate: LocalDate
)
