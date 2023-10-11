package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.AccountsWithExpense
import and.drew.nkhukumanagement.data.AccountsWithIncome
import and.drew.nkhukumanagement.data.Expense
import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.Income
import and.drew.nkhukumanagement.userinterface.flock.FlockUiState
import and.drew.nkhukumanagement.utils.DateUtils
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel to insert, retrieve, update and delete Account data from the [FlockRepository]'s data source.
 */
@HiltViewModel
class AccountsViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository
) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }

    val id = savedStateHandle[TransactionsScreenDestination.accountIdArg] ?: 0

    val accountsWithExpense: StateFlow<AccountsWithExpense> =
        flockRepository.getAccountsWithExpense(id)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = AccountsWithExpense(
                    accountsSummary = AccountsSummary(
                        flockUniqueID = "",
                        batchName = "",
                        totalIncome = 0.0,
                        totalExpenses = 0.0,
                        variance = 0.0
                    )
                )
            )

    val accountsWithIncome: StateFlow<AccountsWithIncome> =
        flockRepository.getAccountsWithIncome(id)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = AccountsWithIncome(
                    accountsSummary = AccountsSummary(
                        flockUniqueID = "",
                        batchName = "",
                        totalIncome = 0.0,
                        totalExpenses = 0.0,
                        variance = 0.0
                    )
                )
            )
    val accountsList: StateFlow<AccountsUiState> =
        flockRepository.getAllAccountsItems()
            .map { AccountsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = AccountsUiState()
            )


    /**
     * Insert an Account Item into the database
     */
    suspend fun insertAccount(accountsSummary: AccountsSummary) {
        flockRepository.insertAccounts(accountsSummary)
    }

    /**
     * Update an Account item when inserting an expense item
     */
    suspend fun updateAccount(accountsSummary: AccountsSummary, expenseUiState: ExpensesUiState) {
        flockRepository.updateAccounts(
            AccountsSummary(
                id = accountsSummary.id,
                flockUniqueID = accountsSummary.flockUniqueID,
                batchName = accountsSummary.batchName,
                totalExpenses = calculateCumulativeExpenseUpdate(
                    initialExpense = accountsSummary.totalExpenses.toString(),
                    totalExpense = expenseUiState.totalExpense,
                    initialItemExpense = expenseUiState.initialItemExpense
                ),
                totalIncome = accountsSummary.totalIncome,
                variance = accountsSummary.totalIncome - calculateCumulativeExpenseUpdate(
                    initialExpense = accountsSummary.totalExpenses.toString(),
                    totalExpense = expenseUiState.totalExpense,
                    initialItemExpense = expenseUiState.initialItemExpense
                )
            )
        )
    }
    /**
     * Update an Account item when deleting an expense item
     */
    suspend fun updateAccountWhenDeletingExpense(accountsSummary: AccountsSummary, expense: Expense) {
        flockRepository.updateAccounts(
            AccountsSummary(
                id = accountsSummary.id,
                flockUniqueID = accountsSummary.flockUniqueID,
                batchName = accountsSummary.batchName,
                totalExpenses = accountsSummary.totalExpenses - expense.totalExpense,
                totalIncome = accountsSummary.totalIncome,
                variance = accountsSummary.variance + expense.totalExpense
            )
        )
    }

    /**
     * Update account when inserting a new Income Item
     */
    suspend fun updateAccount(accountsSummary: AccountsSummary, incomeUiState: IncomeUiState) {
        flockRepository.updateAccounts(
            AccountsSummary(
                id = accountsSummary.id,
                flockUniqueID = accountsSummary.flockUniqueID,
                batchName = accountsSummary.batchName,
                totalExpenses = accountsSummary.totalExpenses,
                totalIncome = calculateCumulativeIncomeUpdate(
                    initialIncome = accountsSummary.totalIncome.toString(),
                    totalIncome = incomeUiState.totalIncome,
                    initialItemIncome = incomeUiState.initialItemIncome
                ),
                variance = calculateCumulativeIncomeUpdate(
                    initialIncome = accountsSummary.totalIncome.toString(),
                    totalIncome = incomeUiState.totalIncome,
                    initialItemIncome = incomeUiState.initialItemIncome
                ) -
                        accountsSummary.totalExpenses
            )
        )
    }

    /**
     * Update Account when deleting an income item
     */
    suspend fun updateAccountWhenDeletingIncome(accountsSummary: AccountsSummary, income: Income) {
        flockRepository.updateAccounts(
            AccountsSummary(
                id = accountsSummary.id,
                flockUniqueID = accountsSummary.flockUniqueID,
                batchName = accountsSummary.batchName,
                totalExpenses = accountsSummary.totalExpenses,
                totalIncome = accountsSummary.totalIncome - income.totalIncome,
                variance = accountsSummary.variance - income.totalIncome
            )
        )
    }


    /**
     * Insert an expense into the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertExpense(expense: Expense) {
        flockRepository.insertExpense(expense)
    }

    /**
     * Retrieve expense data from flockUiState when a new flock item
     * is being inserted into the database. This is the cost of the chicks ordered
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun ExpenseToInsert(flockUiState: FlockUiState): Expense {
        return Expense(
            flockUniqueID = flockUiState.getUniqueId(),
            date = DateUtils().stringToLocalDateShortFormat(
                DateUtils().dateToStringShortFormat(DateUtils().stringToLocalDate(flockUiState.getDate()))
            ),
            expenseName = "Day Old Chicks",
            supplier = flockUiState.breed,
            costPerItem = flockUiState.cost.toDouble(),
            quantity = flockUiState.quantity.toInt(),
            totalExpense = flockUiState.totalCostOfBirds(),
            cumulativeTotalExpense = flockUiState.totalCostOfBirds(),
            notes = ""
        )
    }

    /**
     * Retrieve account data from flockUiState when a new flock item
     * is being inserted into the database. This is the initial account record inserted into the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun Accounts(flockUiState: FlockUiState): AccountsSummary {
        return AccountsSummary(
            flockUniqueID = flockUiState.getUniqueId(),
            batchName = flockUiState.batchName,
            totalIncome = 0.0,
            totalExpenses = flockUiState.totalCostOfBirds(),
            variance = flockUiState.variance()
        )
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun AccountsSummary(expenseUiState: ExpensesUiState) : AccountsSummary {
//        return AccountsSummary(
//            flockUniqueID = expenseUiState.flockUniqueID,
//            batchName = ,
//            totalIncome = 0.0,
//            totalExpenses = flockUiState.totalCostOfBirds(),
//            variance = flockUiState.variance()
//        )
//    }
}