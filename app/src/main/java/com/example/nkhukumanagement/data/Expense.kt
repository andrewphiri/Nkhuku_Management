package com.example.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "expense")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flockUniqueID: String,
    val date: LocalDate,
    val expenseName: String,
    val costPerItem: Double,
    val quantity: Double,
    val totalExpense: Double,
    val cumulativeTotalExpense: Double
)
