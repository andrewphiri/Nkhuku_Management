package and.drew.nkhukumanagement.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * One to many relationship. Single Accounts summary with many instances of Expense
 */
data class AccountsWithExpense(
    @Embedded val accountsSummary: AccountsSummary,
    @Relation(
        parentColumn = "flockUniqueID",
        entityColumn = "flockUniqueID"
    )
    val expenseList: List<Expense> = listOf()
)
