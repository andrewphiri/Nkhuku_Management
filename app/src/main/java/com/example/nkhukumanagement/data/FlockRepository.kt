package com.example.nkhukumanagement.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository with insert, update, delete, retrieve all and single items from data source
 */
interface FlockRepository {
    /**
     * Retrieve all flock entries from data source
     */
    fun getAllItems(): Flow<List<Flock>>

    /**
     * Retrieve an item from data source
     */
    fun getItem(id: Int): Flow<Flock>

    /**
     * Insert flock in the database
     */
    suspend fun insertFlock(flock: Flock)

    /**
     * Delete flock from the database
     */
    suspend fun deleteFlock(flock: Flock)

    /**
     * Update flock in the database
     */
    suspend fun updateFlock(flock: Flock)
}