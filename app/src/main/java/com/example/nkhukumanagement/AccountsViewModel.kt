package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.AccountsSummary
import com.example.nkhukumanagement.data.Expense
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.userinterface.flock.FlockUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(val flockRepository: FlockRepository): ViewModel() {
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertExpense(expenseUiState: ExpensesUiState) {
        flockRepository.insertExpense(expenseUiState.toExpense())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun ExpenseToInsert(flockUiState: FlockUiState) : ExpensesUiState {
        return ExpensesUiState(
            flockUniqueID = flockUiState.getUniqueId(),
            date = flockUiState.getDate(),
            expenseName = "Day Old Chicks",
            costPerItem = flockUiState.cost,
            quantity = flockUiState.quantity,
            totalExpense = flockUiState.totalCostOfBirds().toString(),
            cumulativeTotalExpense = flockUiState.totalCostOfBirds().toString()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Accounts(flockUiState: FlockUiState) : AccountsSummary {
        return AccountsSummary(
            flockUniqueID = flockUiState.getUniqueId(),
            batchName = flockUiState.batchName,
            totalIncome = 0.0,
            totalExpenses = flockUiState.totalCostOfBirds(),
            variance = flockUiState.variance()
        )
    }
}