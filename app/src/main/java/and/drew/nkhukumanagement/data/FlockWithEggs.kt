package and.drew.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

data class FlockWithEggs(
    @Embedded val flock: Flock?,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueId"
    )
    val eggs: List<Eggs>?
)
