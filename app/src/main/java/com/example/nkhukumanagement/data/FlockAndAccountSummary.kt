package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

data class FlockAndAccountSummary(
    @Embedded val flock: Flock,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueID"
    )
    val accountsSummary: AccountsSummary
)
