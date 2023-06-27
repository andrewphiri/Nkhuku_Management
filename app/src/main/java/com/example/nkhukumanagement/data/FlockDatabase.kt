package com.example.nkhukumanagement.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nkhukumanagement.utils.DateConverter

/**
 * Database class with a singleton INSTANCE Object
 */
@Database(entities = [Flock::class, Vaccination::class, Feed::class, Weight::class], version = 4, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class FlockDatabase : RoomDatabase() {

    abstract fun flockDao(): FlockDao

}