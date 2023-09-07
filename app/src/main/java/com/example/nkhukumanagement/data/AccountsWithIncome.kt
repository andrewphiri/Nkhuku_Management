package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

data class AccountsWithIncome(
    @Embedded val accountsSummary: AccountsSummary,
    @Relation(
        parentColumn = "flockUniqueID",
        entityColumn = "flockUniqueID"
    )
    val incomeList: List<Income> = listOf()
)
