package com.example.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
@Entity(tableName = "weight")
data class Weight(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val flockUniqueId: String,
    val week: String,
    val expectedWeight: Double,
    val weight: Double,
    val measuredDate: LocalDate
)
