package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

class FlockWithVaccinations (
    @Embedded val flock: Flock,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueId"
    )
    val vaccinations: List<Vaccination>
)