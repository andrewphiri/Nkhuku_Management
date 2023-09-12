package com.example.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Data class that represent the a table in the database.
 */
@Entity(tableName = "health")
data class FlockHealth(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flockUniqueId: String,
    val mortality: Int,
    val culls: Int,
    val date: LocalDate
)
