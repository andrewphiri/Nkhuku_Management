package and.drew.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data class that represent the a table in the database.
 */
@Entity("accounts_summary")
class AccountsSummary(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flockUniqueID: String,
    val batchName: String,
    val totalIncome: Double,
    val totalExpenses: Double,
    val variance: Double,
    val flockActive: Boolean = true
)

