package com.example.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "health")
data class FlockHealth(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flockUniqueId: String,
    val mortality: Int,
    val culls: Int,
    val date: LocalDate
)
