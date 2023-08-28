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
import com.example.nkhukumanagement.data.AccountsWithIncome
import com.example.nkhukumanagement.data.FlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor( val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository) : ViewModel() {

    companion object {
        private const val MILLIS = 5_000L
    }

    var incomeUiState by mutableStateOf(IncomeUiState())
        private set
    private var incomeListState: SnapshotStateList<IncomeUiState> = mutableStateListOf()

    val flockID = savedStateHandle[IncomeScreenDestination.flockIdArg] ?: 0

    val accountsWithIncome: StateFlow<AccountsWithIncome> =
        flockRepository.getAccountsWithIncome(flockID)
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = AccountsWithIncome(accountsSummary = null)
            )
    fun setIncomeList(incomeList: SnapshotStateList<IncomeUiState>) {
        incomeListState = incomeList
    }

    fun getIncomeList() : SnapshotStateList<IncomeUiState> {
        return incomeListState
    }

    fun updateListState(index: Int, incomeUiState: IncomeUiState) {
        incomeListState[index] = incomeUiState
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertIncome(incomeUiState: IncomeUiState) {
        flockRepository.insertIncome(incomeUiState.toIncome())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateIncome(uiState: IncomeUiState) {
        flockRepository.updateIncome(uiState.toIncome())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteIncome(uiState: IncomeUiState) {
        flockRepository.deleteIncome(uiState.toIncome())
    }
}