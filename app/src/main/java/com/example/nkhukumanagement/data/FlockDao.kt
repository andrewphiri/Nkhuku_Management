package com.example.nkhukumanagement.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FlockDao {
    @Insert
    suspend fun insertFlock(flock: Flock)

    @Insert
    suspend fun insertVaccination(vaccination: Vaccination)

    @Update
    suspend fun updateFlock(flock: Flock)

    @Update
    suspend fun updateVaccination(vaccination: Vaccination)

    @Delete
    suspend fun deleteFlock(flock: Flock)

    @Delete
    suspend fun deleteVaccination(vaccination: Vaccination)

    @Query("SELECT * FROM flock WHERE id = :id")
    fun retrieveFlock(id: Int) : Flow<Flock>

    @Query("SELECT * FROM vaccinations WHERE id = :id")
    fun retrieveVaccination(id: Int) : Flow<Vaccination>

    @Query("SELECT * FROM flock")
    fun getAllFlockItems() : Flow<List<Flock>>

    @Query("SELECT * FROM vaccinations")
    fun getAllVaccinationItems() : Flow<List<Vaccination>>

    @Transaction
    @Query("SELECT * FROM flock")
    fun getFlocksWithVaccinations(): Flow<List<FlockWithVaccinations>>
}