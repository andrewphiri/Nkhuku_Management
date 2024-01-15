package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.data.Expense
import and.drew.nkhukumanagement.data.FlockRepository
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


/**
 * ViewModel to insert, retrieve, update and delete an Expense item from the [FlockRepository]'s data source.
 */
@HiltViewModel
class ExpenseViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository
) : ViewModel() {

    /**
     * Holds the current Ui State
     */
    var expenseUiState by mutableStateOf(ExpensesUiState())
        private set
    private var expenseListState: SnapshotStateList<ExpensesUiState> = mutableStateListOf()

    val expenseID = savedStateHandle
        .getStateFlow(key = AddExpenseScreenDestination.expenseIdArg, initialValue = 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    val getExpense: Flow<Expense> =
        savedStateHandle.getStateFlow(
            key = AddExpenseScreenDestination.expenseIdArg,
            initialValue = 0
        )
            .flatMapLatest {
                flockRepository
                    .getExpenseItem(it)
            }
//        flockRepository
//            .getExpenseItem(expenseID)


    /**
     * Update the expense ui state
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateState(expenseState: ExpensesUiState) {
        expenseUiState = expenseState.copy(isEnabled = expenseState.isValid())
    }

    /**
     * Insert an Expense Item into the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertExpense(expensesUiState: ExpensesUiState) {
        flockRepository.insertExpense(expensesUiState.toExpense())
    }

    /**
     * Update an Expense Item from the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateExpense(uiState: ExpensesUiState) {
        flockRepository.updateExpense(uiState.toExpense())
    }

    /**
     * Delete an Expense Item from the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteExpense(expense: Expense) {
        flockRepository.deleteExpense(expense)
    }

    fun setExpenseID(id: Int) {
        savedStateHandle[AddExpenseScreenDestination.expenseIdArg] = id
    }
}