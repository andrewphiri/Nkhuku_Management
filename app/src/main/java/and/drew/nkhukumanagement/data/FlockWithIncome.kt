package and.drew.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * One to many relationship. Single Flock instance with zero or more instances of Income
 */
data class FlockWithIncome(
    @Embedded val flock: Flock,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueID"
    )
    val incomeList: List<Income> = listOf()
)