package and.drew.nkhukumanagement.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

/**
 * Repository with insert, update, delete, retrieve all and single items from data source
 */
interface FlockRepository {
    /**
     * Retrieve all flock entries from data source
     */
    fun getAllFlockItems(): Flow<List<Flock>?>

    /**
     * Retrieve all feed entries from data source
     */
    fun getAllFeedItems(): Flow<List<Feed>?>

    /**
     * Retrieve all weight entries from data source
     */
    fun getAllWeightItems(): Flow<List<Weight>?>

    /**
     * Retrieve all egg entries from data source
     */
    fun getAllEggItems(): Flow<List<Eggs>?>

    /**
     * Retrieve all flock entries from data source
     */
    fun getAllAccountsItems(): Flow<List<AccountsSummary>?>

    /**
     * Retrieve all flocksWithVaccinations from data source
     */
    fun getAllFlocksWithVaccinations(id: Int): Flow<FlockWithVaccinations?>

    /**
     * Retrieve all flocksWithEggs from data source
     */
    fun getAllFlocksWithEggs(id: Int): Flow<FlockWithEggs?>

    /**
     * Retrieve all flocksWithFeed from data source
     */
    fun getAllFlocksWithFeed(id: Int): Flow<FlockWithFeed?>

    /**
     * Retrieve all flocksWithWeight from data source
     */
    fun getAllFlocksWithWeight(id: Int): Flow<FlockWithWeight?>

    /**
     * Retrieve all flocksWithHealth from data source
     */
    fun getFlocksWithHealth(id: Int): Flow<FlockWithHealth?>

    /**
     * Retrieve all vaccination entries from data source
     */
    fun getAllVaccinationItems(): Flow<List<Vaccination>?>

    /**
     * Retrieve flock from data source
     */
    fun getFlock(id: Int): Flow<Flock>?

    /**
     * Retrieve flock from data source
     */
    fun getFlock(uniqueID: String?): Flow<Flock>?

    /**
     * Retrieve flock health from data source
     */
    fun getFlockHealthItem(id: Int): Flow<FlockHealth?>

    /**
     * Retrieve vaccination item from data source
     */
    fun getVaccinationItem(id: Int): Flow<Vaccination?>

    /**
     * Retrieve income item from data source
     */
    fun getIncomeItem(id: Int): Flow<Income?>

    /**
     * Retrieve weight item from data source
     */
    fun getWeightItem(id: Int): Flow<Weight?>

    /**
     * Retrieve feed item from data source
     */
    fun getFeedItem(id: Int): Flow<Feed>

    /**
     * Retrieve egg item from data source
     */
    fun getEggItem(id: Int): Flow<Eggs>

    /**
     * Retrieve egg summary item from data source
     */
    fun getAllEggsSummaryItems(): Flow<List<EggsSummary>?>

    /**
     * Retrieve expense item from data source
     */
    fun getExpenseItem(id: Int): Flow<Expense>

    fun getFlockAndAccountSummary(id: Int): LiveData<FlockAndAccountSummary?>

    fun getFlockAndEggsSummary(id: Int): Flow<FlockAndEggsSummary?>

    fun getFlockWithIncome(id: Int): Flow<FlockWithIncome?>

    fun getFlockWithExpenses(id: Int): Flow<FlockWithExpenses?>

    fun getAccountsWithIncome(id: Int): Flow<AccountsWithIncome?>

    fun getAccountsWithExpense(id: Int): Flow<AccountsWithExpense?>

    fun getAllVaccinationsForExport(flockUniqueID: String): Flow<List<Vaccination>>
    fun getAllFeedsForExport(flockUniqueID: String): Flow<List<Feed>?>
    fun getAllWeightsForExport(flockUniqueID: String): Flow<List<Weight>?>
    fun getAllEggsForExport(flockUniqueID: String): Flow<List<Eggs>>
    fun getAllHealthForExport(flockUniqueID: String): Flow<List<FlockHealth>?>
    fun getAllIncomeForExport(flockUniqueID: String): Flow<List<Income>?>
    fun getAllExpensesForExport(flockUniqueID: String): Flow<List<Expense>?>
    fun getAllAccountsForExport(flockUniqueID: String): Flow<AccountsSummary?>
    fun getAllEggsSummaryForExport(flockUniqueID: String): Flow<EggsSummary?>

    /**
     * Insert flock in the database
     */
    suspend fun insertFlock(flock: Flock)

    /**
     * Insert accounts in the database
     */
    suspend fun insertAccounts(accountsSummary: AccountsSummary)

    /**
     * Insert income in the database
     */
    suspend fun insertIncome(income: Income)

    /**
     * Insert expense in the database
     */
    suspend fun insertExpense(expense: Expense)

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
     * Insert flockHealth in the database
     */
    suspend fun insertFlockHealth(flockHealth: FlockHealth)

    /**
     * Insert eggs in the database
     */
    suspend fun insertEgg(eggs: Eggs)

    /**
     * Insert eggsSummary in the database
     */
    suspend fun insertEggsSummary(eggsSummary: EggsSummary)

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
     * Delete accountsSummary from the database
     */
    suspend fun deleteAccounts(flockUniqueID: String)

    /**
     * Delete eggsSummary from the database
     */
    suspend fun deleteEggsSummary(flockUniqueID: String)

    /**
     * Delete eggs from the database
     */
    suspend fun deleteEggs(flockUniqueID: String)

    /**
     * Delete eggs from the database
     */
    suspend fun deleteEggs(eggs: Eggs)

    /**
     * Delete weight from the database
     */
    suspend fun deleteWeight(flockUniqueID: String)

    /**
     * Delete flockHealth from the database
     */
    suspend fun deleteFlockHealth(flockUniqueID: String)

    /**
     * Delete income from the database
     */
    suspend fun deleteIncome(income: Income)


    /**
     * Delete income from the database
     */
    suspend fun deleteIncome(uniqueID: String)

    /**
     * Delete expense from the database
     */
    suspend fun deleteExpense(expense: Expense)

    /**
     * Delete expense from the database
     */
    suspend fun deleteExpense(uniqueID: String)

    /**
     * Update flock in the database
     */
    suspend fun updateFlock(flock: Flock)

    /**
     * Update eggs in the database
     */
    suspend fun updateEggs(eggs: Eggs)

    /**
     * Update eggsSummary in the database
     */
    suspend fun updateEggsSummary(eggsSummary: EggsSummary)

    /**
     * Update vaccination in the database
     */
    suspend fun updateVaccination(vaccination: Vaccination)

    /**
     * Update feed in the database
     */
    suspend fun updateFeed(feed: Feed)

    /**
     * Update accounts in the database
     */
    suspend fun updateAccounts(accountsSummary: AccountsSummary)

    /**
     * Update weight in the database
     */
    suspend fun updateWeight(weight: Weight)

    /**
     * Update flockHealth in the database
     */
    suspend fun updateFlockHealth(flockHealth: FlockHealth)

    /**
     * Update income in the database
     */
    suspend fun updateIncome(income: Income)

    /**
     * Update income in the database
     */
    suspend fun updateExpense(expense: Expense)
}