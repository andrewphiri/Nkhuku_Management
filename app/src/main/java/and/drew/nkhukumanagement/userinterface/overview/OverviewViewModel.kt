package and.drew.nkhukumanagement.userinterface.overview

import and.drew.nkhukumanagement.BaseFlockApplication
import and.drew.nkhukumanagement.FlockApplication
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.Account
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.EggsSummary
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.ui.theme.GreenColor
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    val application: BaseFlockApplication,
    val flockRepository: FlockRepository
) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }

    val accountsList: StateFlow<OverviewUiState> =
        flockRepository.getAllAccountsItems()
            .map { OverviewUiState(it?.toMutableStateList() ?: mutableStateListOf()) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = OverviewUiState()
            )

    val eggSummaryList: StateFlow<List<EggsSummary>?> =
        flockRepository.getAllEggsSummaryItems()
            .map { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(MILLIS),
                initialValue = listOf()
            )

    fun accountsTotalsList(accountsSummary: List<AccountsSummary>): List<Account> {
        return listOf(
            Account(
                color = if (accountsSummary.isNotEmpty()) GreenColor else Color.Gray,
                description = application.applicationContext.getString(
                    R.string.total_income),
                amount = accountsSummary.sumOf { it.totalIncome },
                net = accountsSummary.sumOf { it.variance }
            ),
            Account(
                color = if (accountsSummary.isNotEmpty()) Color.Red else Color.DarkGray,
                description = application.applicationContext.getString(
                    R.string.total_expenses),
                amount = accountsSummary.sumOf { it.totalExpenses },
                net = accountsSummary.sumOf { it.variance }
            )
        )
    }

    fun flockTotalsList(flock: List<Flock>): List<Account> {
        return listOf(
            Account(
                color = if (flock.isNotEmpty()) Color.Magenta else Color.Gray,
                description = application.applicationContext.getString(R.string.healthy_birds),
                amount = (flock.sumOf { it.numberOfChicksPlaced + it.donorFlock } - flock.sumOf { it.mortality + it.culls })
                    .toDouble(),
                total = flock.sumOf { it.numberOfChicksPlaced + it.donorFlock }.toDouble(),
            ),
            Account(
                color = if (flock.isNotEmpty()) Color.Cyan else Color.DarkGray,
                description = application.applicationContext.getString(
                    R.string.mortality),
                amount = flock.sumOf { it.mortality }.toDouble()
            ),
            Account(
                color = if (flock.isNotEmpty()) Color.Blue else Color.DarkGray,
                description = application.applicationContext.getString(
                    R.string.culls),
                amount = flock.sumOf { it.culls }.toDouble(),
                net = (flock.sumOf { it.numberOfChicksPlaced + it.donorFlock } - flock.sumOf { it.mortality + it.culls }).toDouble()
            )
        )
    }
}