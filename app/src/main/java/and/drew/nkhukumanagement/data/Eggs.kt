package and.drew.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "eggs")
data class Eggs(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: LocalDate,
    val flockUniqueId: String,
    val goodEggs: Int,
    val badEggs: Int
)
