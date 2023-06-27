package com.example.nkhukumanagement.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
@Entity(tableName = "feed")
data class Feed(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val flockUniqueId:String,
    val name: String,
    val type: String,
    val consumed: Double,
    val feedingDate: LocalDate
)
