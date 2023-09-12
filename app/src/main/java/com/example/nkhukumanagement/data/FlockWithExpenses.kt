package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * One to many relationship. Single Flock instance with zero or more instances of Expense
 */
data class FlockWithExpenses(
    @Embedded val flock: Flock,
    @Relation(
        parentColumn = "uniqueId",
        entityColumn = "flockUniqueID"
    )
    val expenseList: List<Expense> = listOf()
)