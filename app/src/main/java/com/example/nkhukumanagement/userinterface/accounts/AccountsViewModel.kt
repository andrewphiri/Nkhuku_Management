package com.example.nkhukumanagement.userinterface.accounts

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.AccountsSummary
import com.example.nkhukumanagement.data.Expense
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.userinterface.flock.FlockUiState
import com.example.nkhukumanagement.utils.DateUtils
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
    suspend fun insertAccounts(accountsSummary: AccountsSummary) {
        flockRepository.insertAccounts(accountsSummary)
    }

    /**
     * Update an Account item
     */
    suspend fun updateAccounts(accountsSummary: AccountsSummary) {
        flockRepository.updateAccounts(accountsSummary)
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