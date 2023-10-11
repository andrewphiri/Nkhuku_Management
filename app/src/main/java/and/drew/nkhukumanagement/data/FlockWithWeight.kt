package and.drew.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * One to many relationship. Single Flock instance with zero or more instances of Weight
 */
data class FlockWithWeight(
    @Embedded val flock: Flock?,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueId"
    )
    val weights: List<Weight>?
)
