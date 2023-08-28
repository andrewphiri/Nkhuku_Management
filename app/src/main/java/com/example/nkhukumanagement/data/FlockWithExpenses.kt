package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

data class FlockWithExpenses (
    @Embedded val flock: Flock,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueID"
    )
    val expenseList: List<Expense> = listOf()
)