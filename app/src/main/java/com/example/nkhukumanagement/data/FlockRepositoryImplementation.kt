package com.example.nkhukumanagement.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FlockRepositoryImplementation @Inject constructor(private val flockDao: FlockDao) : FlockRepository {
    override fun getAllItems(): Flow<List<Flock>> = flockDao.getAllItems()

    override fun getItem(id: Int): Flow<Flock> = flockDao.retrieveFlock(id)

    override suspend fun insertFlock(flock: Flock) = flockDao.insert(flock)

    override suspend fun deleteFlock(flock: Flock) = flockDao.delete(flock)

    override suspend fun updateFlock(flock: Flock) = flockDao.update(flock)
}