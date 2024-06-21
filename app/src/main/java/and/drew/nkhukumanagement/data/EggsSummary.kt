package and.drew.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "eggs_summary")
data class EggsSummary(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flockUniqueID: String,
    val totalGoodEggs: Int,
    val totalBadEggs: Int,
    val date: LocalDate
)
