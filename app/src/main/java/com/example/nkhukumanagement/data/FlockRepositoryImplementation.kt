package com.example.nkhukumanagement.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FlockRepositoryImplementation @Inject constructor(private val flockDao: FlockDao) : FlockRepository {
    override fun getAllFlockItems(): Flow<List<Flock>> = flockDao.getAllFlockItems()
    override fun getAllFeedItems(): Flow<List<Feed>> = flockDao.getAllFeedItems()

    override fun getAllWeightItems(): Flow<List<Weight>> = flockDao.getAllWeightItems()

    override fun getAllFlocksWithVaccinations(id: Int):
            Flow<FlockWithVaccinations> = flockDao.getFlocksWithVaccinations(id)

    override fun getAllFlocksWithFeed(id: Int): Flow<FlockWithFeed> = flockDao.getFlocksWithFeed(id)

    override fun getAllFlocksWithWeight(id: Int): Flow<FlockWithWeight> = flockDao.getFlocksWithWeight(id)

    override fun getAllVaccinationItems(): Flow<List<Vaccination>> = flockDao.getAllVaccinationItems()

    override fun getFlock(id: Int): Flow<Flock> = flockDao.retrieveFlock(id)
    override fun getVaccinationItem(id: Int): Flow<Vaccination> = flockDao.retrieveVaccination(id)

    override suspend fun insertFlock(flock: Flock) = flockDao.insertFlock(flock)
    override suspend fun insertVaccination(vaccination: Vaccination) = flockDao.insertVaccination(vaccination)
    override suspend fun insertFeed(feed: Feed) = flockDao.insertFeed(feed)

    override suspend fun insertWeight(weight: Weight) = flockDao.insertWeight(weight)

    override suspend fun deleteFlock(flockUniqueID: String) = flockDao.deleteFlock(flockUniqueID)
    override suspend fun deleteVaccination(flockUniqueID: String) = flockDao.deleteVaccination(flockUniqueID)
    override suspend fun deleteFeed(flockUniqueID: String) = flockDao.deleteFeed(flockUniqueID)

    override suspend fun deleteWeight(flockUniqueID: String) = flockDao.deleteWeight(flockUniqueID)

    override suspend fun updateFlock(flock: Flock) = flockDao.updateFlock(flock)
    override suspend fun updateVaccination(vaccination: Vaccination) = flockDao.updateVaccination(vaccination)
    override suspend fun updateFeed(feed: Feed) = flockDao.updateFeed(feed)

    override suspend fun updateWeight(weight: Weight) = flockDao.updateWeight(weight)
}