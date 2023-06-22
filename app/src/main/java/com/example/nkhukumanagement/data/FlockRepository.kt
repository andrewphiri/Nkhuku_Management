package com.example.nkhukumanagement.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository with insert, update, delete, retrieve all and single items from data source
 */
interface FlockRepository {
    /**
     * Retrieve all flock entries from data source
     */
    fun getAllFlockItems(): Flow<List<Flock>>

    /**
     * Retrieve all flocksWithVaccinations from data source
     */
    fun getAllFlocksWithVaccinations(): Flow<List<FlockWithVaccinations>>

    /**
     * Retrieve all vaccination entries from data source
     */
    fun getAllVaccinationItems(): Flow<List<Vaccination>>

    /**
     * Retrieve flock from data source
     */
    fun getFlock(id: Int): Flow<Flock>

    /**
     * Retrieve vaccination item from data source
     */
    fun getVaccinationItem(id: Int): Flow<Vaccination>

    /**
     * Insert flock in the database
     */
    suspend fun insertFlock(flock: Flock)

    /**
     * Insert vaccination in the database
     */
    suspend fun insertVaccination(vaccination: Vaccination)

    /**
     * Delete flock from the database
     */
    suspend fun deleteFlock(flockUniqueID: String)

    /**
     * Delete vaccination from the database
     */
    suspend fun deleteVaccination(flockUniqueID: String)

    /**
     * Update flock in the database
     */
    suspend fun updateFlock(flock: Flock)

    /**
     * Update vaccination in the database
     */
    suspend fun updateVaccination(vaccination: Vaccination)
}