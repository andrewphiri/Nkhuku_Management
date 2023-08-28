package com.example.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "income")
data class Income(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flockUniqueID: String,
    val date: LocalDate,
    val incomeName: String,
    val pricePerItem: Double,
    val quantity: Double,
    val totalIncome: Double,
    val cumulativeTotalIncome: Double
)
