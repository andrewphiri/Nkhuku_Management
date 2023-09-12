package com.example.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Data class that represent the a table in the database.
 */
@Entity(tableName = "vaccinations")
data class Vaccination(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val flockUniqueId: String,
    val name: String,
    val date: LocalDate,
    val notes: String
)
