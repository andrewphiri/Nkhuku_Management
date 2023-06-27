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
     * Retrieve all feed entries from data source
     */
    fun getAllFeedItems(): Flow<List<Feed>>

    /**
     * Retrieve all flock entries from data source
     */
    fun getAllWeightItems(): Flow<List<Weight>>

    /**
     * Retrieve all flocksWithVaccinations from data source
     */
    fun getAllFlocksWithVaccinations(id: Int): Flow<FlockWithVaccinations>

    /**
     * Retrieve all flocksWithFeed from data source
     */
    fun getAllFlocksWithFeed(id: Int): Flow<FlockWithFeed>

    /**
     * Retrieve all flocksWithWeight from data source
     */
    fun getAllFlocksWithWeight(id: Int): Flow<FlockWithWeight>

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
     * Insert feed in the database
     */
    suspend fun insertFeed(feed: Feed)

    /**
     * Insert weight in the database
     */
    suspend fun insertWeight(weight: Weight)

    /**
     * Delete flock from the database
     */
    suspend fun deleteFlock(flockUniqueID: String)

    /**
     * Delete vaccination from the database
     */
    suspend fun deleteVaccination(flockUniqueID: String)

    /**
     * Delete feed from the database
     */
    suspend fun deleteFeed(flockUniqueID: String)

    /**
     * Delete weight from the database
     */
    suspend fun deleteWeight(flockUniqueID: String)

    /**
     * Update flock in the database
     */
    suspend fun updateFlock(flock: Flock)

    /**
     * Update vaccination in the database
     */
    suspend fun updateVaccination(vaccination: Vaccination)

    /**
     * Update feed in the database
     */
    suspend fun updateFeed(feed: Feed)

    /**
     * Update weight in the database
     */
    suspend fun updateWeight(weight: Weight)
}