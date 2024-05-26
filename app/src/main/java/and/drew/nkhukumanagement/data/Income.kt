package and.drew.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Data class that represent the a table in the database.
 */
@Entity(tableName = "income")
data class Income(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flockUniqueID: String,
    val date: LocalDate,
    val incomeName: String,
    val incomeType: String,
    val customer: String,
    val pricePerItem: Double,
    val quantity: Int,
    val totalIncome: Double,
    val cumulativeTotalIncome: Double,
    val notes: String
)
