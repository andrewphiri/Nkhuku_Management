package com.example.nkhukumanagement.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data access object to query the database.
 */
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

    @Insert
    suspend fun insertAccounts(accountsSummary: AccountsSummary)

    @Insert
    suspend fun insertIncome(income: Income)

    @Insert
    suspend fun insertExpense(expense: Expense)

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

    @Update
    suspend fun updateAccounts(accountsSummary: AccountsSummary)

    @Update
    suspend fun updateIncome(income: Income)

    @Update
    suspend fun updateExpense(expense: Expense)

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

    @Query("DELETE FROM accounts_summary WHERE flockUniqueID = :flockUniqueID")
    suspend fun deleteAccounts(flockUniqueID: String)

    @Delete
    suspend fun deleteIncome(income: Income)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM flock WHERE id = :id")
    fun retrieveFlock(id: Int): Flow<Flock>

    @Query("SELECT * FROM income WHERE id = :id")
    fun retrieveIncome(id: Int): Flow<Income>

    @Query("SELECT * FROM expense WHERE id = :id")
    fun retrieveExpense(id: Int): Flow<Expense>

    @Query("SELECT * FROM vaccinations WHERE id = :id")
    fun retrieveVaccination(id: Int): Flow<Vaccination>

    @Query("SELECT * FROM flock")
    fun getAllFlockItems(): Flow<List<Flock>>

    @Query("SELECT * FROM vaccinations")
    fun getAllVaccinationItems(): Flow<List<Vaccination>>

    @Query("SELECT * FROM feed")
    fun getAllFeedItems(): Flow<List<Feed>>

    @Query("SELECT * FROM weight")
    fun getAllWeightItems(): Flow<List<Weight>>

    @Query("SELECT * FROM accounts_summary")
    fun getAllAccountItems(): Flow<List<AccountsSummary>>


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

    @Transaction
    @Query("SELECT * FROM flock WHERE id = :id")
    fun getFlocksAndAccountSummary(id: Int): Flow<FlockAndAccountSummary>

    @Transaction
    @Query("SELECT * FROM flock WHERE id = :id")
    fun getFlocksWithIncome(id: Int): Flow<FlockWithIncome>

    @Transaction
    @Query("SELECT * FROM flock WHERE id = :id")
    fun getFlocksWithExpense(id: Int): Flow<FlockWithExpenses>

    @Transaction
    @Query("SELECT * FROM accounts_summary WHERE id = :id")
    fun getAccountsWithIncome(id: Int): Flow<AccountsWithIncome>

    @Transaction
    @Query("SELECT * FROM accounts_summary WHERE id = :id")
    fun getAccountsWithExpense(id: Int): Flow<AccountsWithExpense>
}