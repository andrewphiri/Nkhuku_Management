package com.example.nkhukumanagement.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FlockRepositoryImplementation @Inject constructor(private val flockDao: FlockDao) : FlockRepository {
    override fun getAllFlockItems(): Flow<List<Flock>> = flockDao.getAllFlockItems()
    override fun getAllFlocksWithVaccinations():
            Flow<List<FlockWithVaccinations>> = flockDao.getFlocksWithVaccinations()

    override fun getAllVaccinationItems(): Flow<List<Vaccination>> = flockDao.getAllVaccinationItems()

    override fun getFlock(id: Int): Flow<Flock> = flockDao.retrieveFlock(id)
    override fun getVaccinationItem(id: Int): Flow<Vaccination> = flockDao.retrieveVaccination(id)

    override suspend fun insertFlock(flock: Flock) = flockDao.insertFlock(flock)
    override suspend fun insertVaccination(vaccination: Vaccination) = flockDao.insertVaccination(vaccination)

    override suspend fun deleteFlock(flockUniqueID: String) = flockDao.deleteFlock(flockUniqueID)
    override suspend fun deleteVaccination(flockUniqueID: String) = flockDao.deleteVaccination(flockUniqueID)

    override suspend fun updateFlock(flock: Flock) = flockDao.updateFlock(flock)
    override suspend fun updateVaccination(vaccination: Vaccination) = flockDao.updateVaccination(vaccination)
}