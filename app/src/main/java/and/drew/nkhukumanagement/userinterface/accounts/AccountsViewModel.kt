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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

//    val accountsID: StateFlow<Int> = savedStateHandle
//        .getStateFlow(key = TransactionsScreenDestination.accountIdArg, initialValue = 0)

    @OptIn(ExperimentalCoroutinesApi::class)
//    val accountsWithExpense: Flow<AccountsWithExpense> =
//        savedStateHandle
//            .getStateFlow(key = TransactionsScreenDestination.accountIdArg, initialValue = 0)
//            .flatMapLatest {
//                flockRepository.getAccountsWithExpense(it)
//            }

    private val _accountsWithExpense = MutableStateFlow<AccountsWithExpense?>(null)
    val accountsWithExpense = _accountsWithExpense.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
//    val accountsWithIncome: Flow<AccountsWithIncome> =
//        savedStateHandle.getStateFlow(
//            key = TransactionsScreenDestination.accountIdArg,
//            initialValue = 0
//        )
//            .flatMapLatest {
//                flockRepository.getAccountsWithIncome(it)
//            }

    private val _accountsWithIncome = MutableStateFlow<AccountsWithIncome?>(null)
    val accountsWithIncome = _accountsWithIncome.asStateFlow()


    val accountsList: StateFlow<AccountsUiState> =
        flockRepository.getAllAccountsItems()
            .map { AccountsUiState(it ?: listOf()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = AccountsUiState()
            )

    fun getAccountsWithExpense(accountsID: Int) {
        viewModelScope.launch {
            flockRepository.getAccountsWithExpense(accountsID).collect {
                _accountsWithExpense.value = it
            }
        }
    }

    fun getAccountsWithIncome(accountsID: Int) {
        viewModelScope.launch {
            flockRepository.getAccountsWithIncome(accountsID).collect {
                _accountsWithIncome.value = it
            }
        }
    }

    fun setAccountsID(id: Int) {
        savedStateHandle[TransactionsScreenDestination.accountIdArg] = id
    }

    /**
     * Insert an Account Item into the database
     */
    suspend fun deleteAccountsSummary(uniqueID: String) {
        flockRepository.deleteAccounts(uniqueID)
    }

    /**
     * Delete an Account Item from the database
     */
    suspend fun insertAccount(accountsSummary: AccountsSummary) {
        flockRepository.insertAccounts(accountsSummary)
    }

    suspend fun updateAccountsSummary(accountsSummary: AccountsSummary) {
        flockRepository.updateAccounts(accountsSummary)
    }

    /**
     * Update an Account item when inserting an expense item
     */
    suspend fun updateAccount(accountsSummary: AccountsSummary?, expenseUiState: ExpensesUiState) {
        flockRepository.updateAccounts(
            if (accountsSummary != null) {
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
            } else {
                return
            }
        )
    }
    /**
     * Update an Account item when deleting an expense item
     */
    suspend fun updateAccountWhenDeletingExpense(accountsSummary: AccountsSummary?, expense: Expense) {
        flockRepository.updateAccounts(
            if (accountsSummary != null) {
                AccountsSummary(
                    id = accountsSummary.id,
                    flockUniqueID = accountsSummary.flockUniqueID,
                    batchName = accountsSummary.batchName,
                    totalExpenses = accountsSummary.totalExpenses - expense.totalExpense,
                    totalIncome = accountsSummary.totalIncome,
                    variance = accountsSummary.variance + expense.totalExpense
                )
            } else {
                return
            }
        )
    }

    /**
     * Update account when inserting a new Income Item
     */
    suspend fun updateAccount(accountsSummary: AccountsSummary?, incomeUiState: IncomeUiState) {
        flockRepository.updateAccounts(
            if (accountsSummary != null) {
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
            } else {
                return
            }
        )
    }

    /**
     * Update Account when deleting an income item
     */
    suspend fun updateAccountWhenDeletingIncome(accountsSummary: AccountsSummary?, income: Income) {
        flockRepository.updateAccounts(
           if (accountsSummary != null) {
               AccountsSummary(
                   id = accountsSummary.id,
                   flockUniqueID = accountsSummary.flockUniqueID,
                   batchName = accountsSummary.batchName,
                   totalExpenses = accountsSummary.totalExpenses,
                   totalIncome = accountsSummary.totalIncome - income.totalIncome,
                   variance = accountsSummary.variance - income.totalIncome
               )
           } else {
               return
           }
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