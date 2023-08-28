package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

data class FlockWithIncome (
    @Embedded val flock: Flock,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueID"
    )
    val incomeList: List<Income> = listOf()
)