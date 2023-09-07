package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.AccountsSummary
import com.example.nkhukumanagement.data.AccountsWithExpense
import com.example.nkhukumanagement.data.Expense
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.userinterface.flock.FlockUiState
import com.example.nkhukumanagement.userinterface.flock.toFlock
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository
) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }

    var expenseUiState by mutableStateOf(ExpensesUiState())
        private set
    private var expenseListState: SnapshotStateList<ExpensesUiState> = mutableStateListOf()

    val flockID = savedStateHandle[ExpenseScreenDestination.flockIdArg] ?: 0
    val id = savedStateHandle[TransactionsScreenDestination.flockIdArg] ?: 1

    @RequiresApi(Build.VERSION_CODES.O)
    val getExpense: Flow<Expense> =
        flockRepository
            .getExpenseItem(flockID)

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

    fun setExpenseList(expenseList: SnapshotStateList<ExpensesUiState>) {
        expenseListState = expenseList
    }

    fun getExpenseList(): SnapshotStateList<ExpensesUiState> {
        return expenseListState
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateState(expenseState: ExpensesUiState) {
        expenseUiState = expenseState.copy(isEnabled = expenseState.isValid())
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