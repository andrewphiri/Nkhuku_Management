package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * One to many relationship. Single Accounts summary with zero or more instances of Income
 */
data class AccountsWithIncome(
    @Embedded val accountsSummary: AccountsSummary,
    @Relation(
        parentColumn = "flockUniqueID",
        entityColumn = "flockUniqueID"
    )
    val incomeList: List<Income> = listOf()
)
