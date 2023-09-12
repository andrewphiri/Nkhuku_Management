package com.example.nkhukumanagement.userinterface.accounts

import com.example.nkhukumanagement.data.AccountsSummary

/**
 * UI State for [AccountsScreen].
 * Return a list of [AccountsSummary] records
 */
data class AccountsUiState(
    val accountsSummary: List<AccountsSummary> = listOf()
)
