package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * One to many relationship. Single Flock instance with zero or more instances of FlockHealth
 */
data class FlockWithHealth(
    @Embedded val flock: Flock?,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueId"
    )
    val health: List<FlockHealth>?
)
