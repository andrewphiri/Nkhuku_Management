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
@Database(entities = [Flock::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class FlockDatabase : RoomDatabase() {

    abstract fun flockDao(): FlockDao

    companion object {
        private var Instance: FlockDatabase? = null

        fun getDatabase(context: Context) : FlockDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context = context, FlockDatabase::class.java, "flock_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}