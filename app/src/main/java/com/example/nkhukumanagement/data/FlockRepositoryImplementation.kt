package com.example.nkhukumanagement.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FlockRepositoryImplementation @Inject constructor(private val flockDao: FlockDao) :
    FlockRepository {
    override fun getAllFlockItems(): Flow<List<Flock>> = flockDao.getAllFlockItems()
    override fun getAllFeedItems(): Flow<List<Feed>> = flockDao.getAllFeedItems()

    override fun getAllWeightItems(): Flow<List<Weight>> = flockDao.getAllWeightItems()
    override fun getAllAccountsItems(): Flow<List<AccountsSummary>> = flockDao.getAllAccountItems()

    override fun getAllFlocksWithVaccinations(id: Int):
            Flow<FlockWithVaccinations> = flockDao.getFlocksWithVaccinations(id)

    override fun getAllFlocksWithFeed(id: Int): Flow<FlockWithFeed> = flockDao.getFlocksWithFeed(id)

    override fun getAllFlocksWithWeight(id: Int): Flow<FlockWithWeight> =
        flockDao.getFlocksWithWeight(id)

    override fun getFlocksWithHealth(id: Int): Flow<FlockWithHealth> =
        flockDao.getFlocksWithHealth(id)

    override fun getAllVaccinationItems(): Flow<List<Vaccination>> =
        flockDao.getAllVaccinationItems()

    override fun getFlock(id: Int): Flow<Flock> = flockDao.retrieveFlock(id)
    override fun getFlockHealthItem(id: Int): Flow<FlockHealth> = flockDao.retrieveHealth(id)

    override fun getVaccinationItem(id: Int): Flow<Vaccination> = flockDao.retrieveVaccination(id)
    override fun getIncomeItem(id: Int) = flockDao.retrieveIncome(id)

    override fun getExpenseItem(id: Int) = flockDao.retrieveExpense(id)
    override fun getFlockAndAccountSummary(id: Int) = flockDao.getFlocksAndAccountSummary(id)

    override fun getFlockWithIncome(id: Int): Flow<FlockWithIncome> =
        flockDao.getFlocksWithIncome(id)

    override fun getFlockWithExpenses(id: Int): Flow<FlockWithExpenses> =
        flockDao.getFlocksWithExpense(id)

    override fun getAccountsWithIncome(id: Int): Flow<AccountsWithIncome> =
        flockDao.getAccountsWithIncome(id)

    override fun getAccountsWithExpense(id: Int): Flow<AccountsWithExpense> =
        flockDao.getAccountsWithExpense(id)

    override suspend fun insertFlock(flock: Flock) = flockDao.insertFlock(flock)
    override suspend fun insertAccounts(accountsSummary: AccountsSummary) =
        flockDao.insertAccounts(accountsSummary)

    override suspend fun insertIncome(income: Income) = flockDao.insertIncome(income)

    override suspend fun insertExpense(expense: Expense) = flockDao.insertExpense(expense)

    override suspend fun insertVaccination(vaccination: Vaccination) =
        flockDao.insertVaccination(vaccination)

    override suspend fun insertFeed(feed: Feed) = flockDao.insertFeed(feed)

    override suspend fun insertWeight(weight: Weight) = flockDao.insertWeight(weight)
    override suspend fun insertFlockHealth(flockHealth: FlockHealth) =
        flockDao.insertFlockHealth(flockHealth)

    override suspend fun deleteFlock(flockUniqueID: String) = flockDao.deleteFlock(flockUniqueID)
    override suspend fun deleteVaccination(flockUniqueID: String) =
        flockDao.deleteVaccination(flockUniqueID)

    override suspend fun deleteFeed(flockUniqueID: String) = flockDao.deleteFeed(flockUniqueID)
    override suspend fun deleteAccounts(flockUniqueID: String) =
        flockDao.deleteAccounts(flockUniqueID)

    override suspend fun deleteWeight(flockUniqueID: String) = flockDao.deleteWeight(flockUniqueID)
    override suspend fun deleteFlockHealth(flockUniqueID: String) =
        flockDao.deleteFlockHealth(flockUniqueID)

    override suspend fun deleteIncome(income: Income) = flockDao.deleteIncome(income)

    override suspend fun deleteExpense(expense: Expense) = flockDao.deleteExpense(expense)

    override suspend fun updateFlock(flock: Flock) = flockDao.updateFlock(flock)
    override suspend fun updateVaccination(vaccination: Vaccination) =
        flockDao.updateVaccination(vaccination)

    override suspend fun updateFeed(feed: Feed) = flockDao.updateFeed(feed)
    override suspend fun updateAccounts(accountsSummary: AccountsSummary) =
        flockDao.updateAccounts(accountsSummary)

    override suspend fun updateWeight(weight: List<Weight>) = flockDao.updateWeight(weight)
    override suspend fun updateFlockHealth(flockHealth: FlockHealth) =
        flockDao.updateFlockHealth(flockHealth)

    override suspend fun updateIncome(income: Income) = flockDao.updateIncome(income)

    override suspend fun updateExpense(expense: Expense) = flockDao.updateExpense(expense)
}