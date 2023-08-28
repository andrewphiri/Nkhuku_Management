package com.example.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

data class AccountsWithExpense(
    @Embedded val accountsSummary: AccountsSummary?,
    @Relation(
        parentColumn = "flockUniqueID",
        entityColumn = "flockUniqueID"
    )
    val expenseList: List<Expense> = listOf()
)
