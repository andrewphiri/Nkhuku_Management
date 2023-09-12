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
import com.example.nkhukumanagement.data.AccountsWithIncome
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.data.Income
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel to insert, retrieve, update and delete an Income item from the [FlockRepository]'s data source.
 */
@HiltViewModel
class IncomeViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository
) : ViewModel() {

    companion object {
        private const val MILLIS = 5_000L
    }

    var incomeUiState by mutableStateOf(IncomeUiState())
        private set
    private var incomeListState: SnapshotStateList<IncomeUiState> = mutableStateListOf()

    val flockID = savedStateHandle[IncomeScreenDestination.flockIdArg] ?: 0
    val id = savedStateHandle[TransactionsScreenDestination.flockIdArg] ?: 1

    val getIncome: Flow<Income> = flockRepository.getIncomeItem(flockID)

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

    /**
     * Update Income Ui State
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateState(incomeState: IncomeUiState) {
        incomeUiState = incomeState.copy(enabled = incomeState.isValid())
    }

    /**
     * Insert an Income Item into the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertIncome(incomeUiState: IncomeUiState) {
        flockRepository.insertIncome(incomeUiState.toIncome())
    }

    /**
     * Update an Income Item from the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateIncome(uiState: IncomeUiState) {
        flockRepository.updateIncome(uiState.toIncome())
    }

    /**
     * Delete an Expense Item from the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteIncome(uiState: IncomeUiState) {
        flockRepository.deleteIncome(uiState.toIncome())
    }
}