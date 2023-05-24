package com.example.nkhukumanagement.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlockDao {
    @Insert
    suspend fun insert(flock: Flock)

    @Update
    suspend fun update(flock: Flock)

    @Delete
    suspend fun delete(flock: Flock)

    @Query("SELECT * FROM flock WHERE id = :id")
    fun retrieveFlock(id: Int) : Flow<Flock>

    @Query("SELECT * FROM flock")
    fun getAllItems() : Flow<List<Flock>>
}