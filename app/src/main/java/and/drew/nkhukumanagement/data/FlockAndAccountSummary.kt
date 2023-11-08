package and.drew.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * One to one relationship. Single instance of Flock corresponds to single of AccountsSummary
 */
data class FlockAndAccountSummary(
    @Embedded val flock: Flock?,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueID"
    )
    val accountsSummary: AccountsSummary?
)
