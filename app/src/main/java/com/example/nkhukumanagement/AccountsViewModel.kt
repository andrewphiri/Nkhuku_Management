package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.AccountsSummary
import com.example.nkhukumanagement.data.AccountsWithExpense
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


    suspend fun insertAccounts(accountsSummary: AccountsSummary) {
        flockRepository.insertAccounts(accountsSummary)
    }

    suspend fun updateAccounts(accountsSummary: AccountsSummary) {
        flockRepository.updateAccounts(accountsSummary)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertExpense(expense: Expense) {
        flockRepository.insertExpense(expense)
    }

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