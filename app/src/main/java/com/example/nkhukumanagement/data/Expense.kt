package com.example.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Data class that represent the a table in the database.
 */
@Entity(tableName = "expense")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flockUniqueID: String,
    val date: LocalDate,
    val expenseName: String,
    val supplier: String,
    val costPerItem: Double,
    val quantity: Int,
    val totalExpense: Double,
    val cumulativeTotalExpense: Double,
    val notes: String
)
