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

    @Insert
    suspend fun insertFeed(feed: Feed)

    @Insert
    suspend fun insertWeight(weight: Weight)

    @Insert
    suspend fun insertFlockHealth(flockHealth: FlockHealth)

    @Update
    suspend fun updateFlock(flock: Flock)

    @Update
    suspend fun updateVaccination(vaccination: Vaccination)

    @Update
    suspend fun updateFeed(feed: Feed)

    @Update
    suspend fun updateWeight(weight: List<Weight>)

    @Update
    suspend fun updateFlockHealth(flockHealth: FlockHealth)


    @Query("DELETE FROM flock WHERE uniqueId = :flockUniqueID")
    suspend fun deleteFlock(flockUniqueID: String)

    @Query("DELETE FROM vaccinations WHERE flockUniqueId = :flockUniqueID")
    suspend fun deleteVaccination(flockUniqueID: String)

    @Query("DELETE FROM feed WHERE flockUniqueId = :flockUniqueID")
    suspend fun deleteFeed(flockUniqueID: String)

    @Query("DELETE FROM weight WHERE flockUniqueId = :flockUniqueID")
    suspend fun deleteWeight(flockUniqueID: String)

    @Query("DELETE FROM health WHERE flockUniqueId = :flockUniqueID")
    suspend fun deleteFlockHealth(flockUniqueID: String)

    @Query("SELECT * FROM flock WHERE id = :id")
    fun retrieveFlock(id: Int) : Flow<Flock>

    @Query("SELECT * FROM vaccinations WHERE id = :id")
    fun retrieveVaccination(id: Int) : Flow<Vaccination>

    @Query("SELECT * FROM flock")
    fun getAllFlockItems() : Flow<List<Flock>>

    @Query("SELECT * FROM vaccinations")
    fun getAllVaccinationItems() : Flow<List<Vaccination>>

    @Query("SELECT * FROM feed")
    fun getAllFeedItems() : Flow<List<Feed>>

    @Query("SELECT * FROM weight")
    fun getAllWeightItems() : Flow<List<Weight>>

    @Transaction
    @Query("SELECT * FROM flock WHERE id = :id")
    fun getFlocksWithVaccinations(id: Int): Flow<FlockWithVaccinations>

    @Transaction
    @Query("SELECT * FROM flock WHERE id = :id")
    fun getFlocksWithFeed(id: Int): Flow<FlockWithFeed>

    @Transaction
    @Query("SELECT * FROM flock WHERE id = :id")
    fun getFlocksWithWeight(id: Int): Flow<FlockWithWeight>

    @Transaction
    @Query("SELECT * FROM flock WHERE id = :id")
    fun getFlocksWithHealth(id: Int): Flow<FlockWithHealth>
}