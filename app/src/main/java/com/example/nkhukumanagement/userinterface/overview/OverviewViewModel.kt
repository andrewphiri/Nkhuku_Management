package com.example.nkhukumanagement.userinterface.overview

import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nkhukumanagement.data.Account
import com.example.nkhukumanagement.data.AccountsSummary
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.ui.theme.GreenCardStartDividerColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(val flockRepository: FlockRepository) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }

    val accountsList: StateFlow<OverviewUiState> =
        flockRepository.getAllAccountsItems()
            .map { OverviewUiState(it.toMutableStateList()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = OverviewUiState()
            )

    fun accountsTotalsList(accountsSummary: List<AccountsSummary>): List<Account> {
        return listOf(
            Account(
                color = if (accountsSummary.isNotEmpty()) GreenCardStartDividerColor else Color.Gray,
                description = "Total Income",
                amount = accountsSummary.sumOf { it.totalIncome },
                net = accountsSummary.sumOf { it.variance }
            ),
            Account(
                color = if (accountsSummary.isNotEmpty()) Color.Red else Color.DarkGray,
                description = "Total Expenses",
                amount = accountsSummary.sumOf { it.totalExpenses },
                net = accountsSummary.sumOf { it.variance }
            )
        )
    }

    fun flockTotalsList(flock: List<Flock>): List<Account> {
        return listOf(
            Account(
                color = if (flock.isNotEmpty()) Color.Magenta else Color.Gray,
                description = "Healthy Birds",
                amount = (flock.sumOf { it.numberOfChicksPlaced + it.donorFlock } - flock.sumOf { it.mortality + it.culls })
                    .toDouble(),
                total = flock.sumOf { it.numberOfChicksPlaced + it.donorFlock }.toDouble(),
            ),
            Account(
                color = if (flock.isNotEmpty()) Color.Cyan else Color.DarkGray,
                description = "Mortality",
                amount = flock.sumOf { it.mortality }.toDouble()
            ),
            Account(
                color = if (flock.isNotEmpty()) Color.Blue else Color.DarkGray,
                description = "Culls",
                amount = flock.sumOf { it.culls }.toDouble(),
                net = (flock.sumOf { it.numberOfChicksPlaced + it.donorFlock } - flock.sumOf { it.mortality + it.culls }).toDouble()
            )
        )
    }
}