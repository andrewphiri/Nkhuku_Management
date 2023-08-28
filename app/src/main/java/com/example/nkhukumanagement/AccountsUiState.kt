package com.example.nkhukumanagement

import com.example.nkhukumanagement.data.AccountsSummary

data class AccountsUiState(
    val accountsSummary: List<AccountsSummary> = listOf()
)
