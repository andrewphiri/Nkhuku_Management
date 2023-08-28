package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.AccountsWithExpense
import com.example.nkhukumanagement.data.FlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(val  savedStateHandle: SavedStateHandle,
                                           val flockRepository: FlockRepository) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }
    var expenseUiState by mutableStateOf(ExpensesUiState())
        private set
    private var expenseListState: SnapshotStateList<ExpensesUiState> = mutableStateListOf()

    val flockID = savedStateHandle[IncomeScreenDestination.flockIdArg] ?: 0

    val accountsWithExpense: StateFlow<AccountsWithExpense> =
        flockRepository.getAccountsWithExpense(flockID)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = AccountsWithExpense(accountsSummary = null)
            )
    fun setIncomeList(expenseList: SnapshotStateList<ExpensesUiState>) {
        expenseListState = expenseList
    }

    fun getIncomeList() : SnapshotStateList<ExpensesUiState> {
        return expenseListState
    }

    fun updateListState(index: Int, expensesUiState: ExpensesUiState) {
        expenseListState[index] = expensesUiState
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertExpense(expensesUiState: ExpensesUiState) {
        flockRepository.insertExpense(expensesUiState.toExpense())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateExpense(uiState: ExpensesUiState) {
        flockRepository.updateExpense(uiState.toExpense())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteExpense(uiState: ExpensesUiState) {
        flockRepository.deleteExpense(uiState.toExpense())
    }
}