package com.example.nkhukumanagement.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nkhukumanagement.R
import java.time.LocalDate

@Entity(tableName = "flock")
data class Flock(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val uniqueId: String,
    val batchName: String,
    val breed: String,
    @ColumnInfo(name = "date_received")
    val datePlaced: LocalDate,
    @ColumnInfo(name = "quantity")
    val numberOfChicksPlaced: Int,
    @ColumnInfo(name = "donor")
    val donorFlock: Int,
    val mortality: Int,
    val imageResourceId: Int = R.drawable.chicken,
    @ColumnInfo(name = "deformities")
    val culls: Int
)
