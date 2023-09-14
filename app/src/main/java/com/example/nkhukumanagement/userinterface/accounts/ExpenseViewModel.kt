package com.example.nkhukumanagement.userinterface.accounts

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
import com.example.nkhukumanagement.data.AccountsSummary
import com.example.nkhukumanagement.data.AccountsWithExpense
import com.example.nkhukumanagement.data.Expense
import com.example.nkhukumanagement.data.FlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


/**
 * ViewModel to insert, retrieve, update and delete an Expense item from the [FlockRepository]'s data source.
 */
@HiltViewModel
class ExpenseViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository
) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }

    /**
     * Holds the current Ui State
     */
    var expenseUiState by mutableStateOf(ExpensesUiState())
        private set
    private var expenseListState: SnapshotStateList<ExpensesUiState> = mutableStateListOf()

    val expenseID = savedStateHandle[AddExpenseScreenDestination.expenseIdArg] ?: 0

    @RequiresApi(Build.VERSION_CODES.O)
    val getExpense: Flow<Expense> =
        flockRepository
            .getExpenseItem(expenseID)


    /**
     * Update the expense ui state
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateState(expenseState: ExpensesUiState) {
        expenseUiState = expenseState.copy(isEnabled = expenseState.isValid())
    }

    /**
     * Insert an Expense Item into the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertExpense(expensesUiState: ExpensesUiState) {
        flockRepository.insertExpense(expensesUiState.toExpense())
    }

    /**
     * Update an Expense Item from the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateExpense(uiState: ExpensesUiState) {
        flockRepository.updateExpense(uiState.toExpense())
    }

    /**
     * Delete an Expense Item from the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteExpense(expense: Expense) {
        flockRepository.deleteExpense(expense)
    }
}