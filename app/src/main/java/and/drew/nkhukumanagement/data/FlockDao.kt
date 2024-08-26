package and.drew.nkhukumanagement.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data access object to query the database.
 */
@Dao
interface FlockDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFlock(flock: Flock)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVaccination(vaccination: Vaccination)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFeed(feed: Feed)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeight(weight: Weight)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFlockHealth(flockHealth: FlockHealth)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccounts(accountsSummary: AccountsSummary)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncome(income: Income)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpense(expense: Expense)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEggs(eggs: Eggs)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEggsSummary(eggs: EggsSummary)

    @Update
    suspend fun updateFlock(flock: Flock)

    @Update
    suspend fun updateVaccination(vaccination: Vaccination)

    @Update
    suspend fun updateFeed(feed: Feed)

    @Update
    suspend fun updateWeight(weight: Weight)

    @Update
    suspend fun updateFlockHealth(flockHealth: FlockHealth)

    @Update
    suspend fun updateAccounts(accountsSummary: AccountsSummary)

    @Update
    suspend fun updateIncome(income: Income)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Update
    suspend fun updateEggs(eggs: Eggs)

    @Update
    suspend fun updateEggsSummary(eggsSummary: EggsSummary)

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

    @Query("DELETE FROM income WHERE flockUniqueID = :flockUniqueID")
    suspend fun deleteIncome(flockUniqueID: String)

    @Query("DELETE FROM expense WHERE flockUniqueID = :flockUniqueID")
    suspend fun deleteExpense(flockUniqueID: String)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM eggs WHERE flockUniqueId = :flockUniqueID")
    suspend fun deleteEggs(flockUniqueID: String)

    @Delete
    suspend fun deleteEggs(eggs: Eggs)

    @Delete
    suspend fun deleteEggsSummary(eggsSummary: EggsSummary)

    @Query("DELETE FROM eggs_summary WHERE flockUniqueID = :flockUniqueID")
    suspend fun deleteEggsSummary(flockUniqueID: String)

    @Query("SELECT * FROM flock WHERE id = :id")
    fun retrieveFlock(id: Int): Flow<Flock>

    @Query("SELECT * FROM flock WHERE uniqueId = :flockUniqueID")
    fun retrieveFlock(flockUniqueID: String): Flow<Flock>?

    @Query("SELECT * FROM income WHERE id = :id")
    fun retrieveIncome(id: Int): Flow<Income>

    @Query("SELECT * FROM expense WHERE id = :id")
    fun retrieveExpense(id: Int): Flow<Expense>

    @Query("SELECT * FROM vaccinations WHERE id = :id")
    fun retrieveVaccination(id: Int): Flow<Vaccination>

    @Query("SELECT * FROM health WHERE id = :id")
    fun retrieveHealth(id: Int): Flow<FlockHealth>

    @Query("SELECT * FROM weight WHERE id = :id")
    fun retrieveWeight(id: Int): Flow<Weight>

    @Query("SELECT * FROM feed WHERE id = :id")
    fun retrieveFeed(id: Int): Flow<Feed>

    @Query("SELECT * FROM eggs WHERE id = :id")
    fun retrieveEggs(id: Int): Flow<Eggs>

    @Query("SELECT * FROM flock")
    fun getAllFlockItems(): Flow<List<Flock>>

    @Query("SELECT * FROM vaccinations")
    fun getAllVaccinationItems(): Flow<List<Vaccination>>

    @Query("SELECT * FROM feed")
    fun getAllFeedItems(): Flow<List<Feed>>

    @Query("SELECT * FROM weight")
    fun getAllWeightItems(): Flow<List<Weight>>

    @Query("SELECT * FROM eggs")
    fun getAllEggItems(): Flow<List<Eggs>>

    @Query("SELECT * FROM accounts_summary")
    fun getAllAccountItems(): Flow<List<AccountsSummary>>

    @Query("SELECT * FROM eggs_summary")
    fun getAllEggsSummaryItems(): Flow<List<EggsSummary>>


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
    fun getFlocksWithEggs(id: Int): Flow<FlockWithEggs>
    @Transaction
    @Query("SELECT * FROM flock WHERE id = :id")
    fun getFlocksAndAccountSummary(id: Int): Flow<FlockAndAccountSummary>

    @Transaction
    @Query("SELECT * FROM flock WHERE id = :id")
    fun getFlocksAndEggsSummary(id: Int): Flow<FlockAndEggsSummary>

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

    @Query("SELECT * FROM vaccinations WHERE flockUniqueId = :flockUniqueID")
    fun getAllVaccinationItemsForExport(flockUniqueID: String): Flow<List<Vaccination>>

    @Query("SELECT * FROM feed WHERE flockUniqueId = :flockUniqueID")
    fun getAllFeedItemsForExport(flockUniqueID: String): Flow<List<Feed>>

    @Query("SELECT * FROM weight WHERE flockUniqueId = :flockUniqueID")
    fun getAllWeightItemsForExport(flockUniqueID: String): Flow<List<Weight>>

    @Query("SELECT * FROM health WHERE flockUniqueId = :flockUniqueID")
    fun getAllHealthItemsForExport(flockUniqueID: String): Flow<List<FlockHealth>>

    @Query("SELECT * FROM eggs WHERE flockUniqueId = :flockUniqueID")
    fun getAllEggsItemsForExport(flockUniqueID: String): Flow<List<Eggs>>

    @Query("SELECT * FROM income WHERE flockUniqueID = :flockUniqueID")
    fun getAllIncomeItemsForExport(flockUniqueID: String): Flow<List<Income>>

    @Query("SELECT * FROM expense WHERE flockUniqueID = :flockUniqueID")
    fun getAllExpenseItemsForExport(flockUniqueID: String): Flow<List<Expense>>

    @Query("SELECT * FROM accounts_summary WHERE flockUniqueID = :flockUniqueID")
    fun getAllAccountItemsForExport(flockUniqueID: String): Flow<AccountsSummary>

    @Query("SELECT * FROM eggs_summary WHERE flockUniqueID = :flockUniqueID")
    fun getAllEggsSummaryItemsForExport(flockUniqueID: String): Flow<EggsSummary>
}