package and.drew.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

data class FlockAndEggsSummary (
    @Embedded val flock: Flock?,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueID"
    )
    val eggsSummary: EggsSummary?
)