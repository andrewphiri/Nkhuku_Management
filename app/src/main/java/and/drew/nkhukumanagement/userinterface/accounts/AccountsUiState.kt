package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.data.AccountsSummary

/**
 * UI State for [AccountsScreen].
 * Return a list of [AccountsSummary] records
 */
data class AccountsUiState(
    val accountsSummary: List<AccountsSummary> = listOf()
)
