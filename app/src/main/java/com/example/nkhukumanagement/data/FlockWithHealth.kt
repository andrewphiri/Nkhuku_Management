package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

data class FlockWithHealth(
    @Embedded val flock: Flock?,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueId"
    )
    val health: List<FlockHealth>?
)
