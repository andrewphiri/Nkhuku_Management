package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.BaseFlockApplication
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.Income
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
 * ViewModel to insert, retrieve, update and delete an Income item from the [FlockRepository]'s data source.
 */
@HiltViewModel
class IncomeViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val flockRepository: FlockRepository,
    val application: BaseFlockApplication
) : ViewModel() {

    companion object {
        private const val MILLIS = 5_000L
    }

    val incomeTypeOptions = application.resources.getStringArray(R.array.income_type_options).toList()
    val incomeTypeOptionsLayers = application.resources.getStringArray(R.array.income_type_options_layers).toList()

    var incomeUiState by mutableStateOf(IncomeUiState())
        private set
    private var incomeListState: SnapshotStateList<IncomeUiState> = mutableStateListOf()

    val incomeID = savedStateHandle
        .getStateFlow(AddIncomeScreenDestination.incomeIdArg, initialValue = 0)


    @OptIn(ExperimentalCoroutinesApi::class)
    val getIncome: Flow<Income> = savedStateHandle
        .getStateFlow(AddIncomeScreenDestination.incomeIdArg, initialValue = 0)
        .flatMapLatest {
            flockRepository.getIncomeItem(it)
        }


    /**
     * Update Income Ui State
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateState(incomeState: IncomeUiState) {
        incomeUiState = incomeState.copy(enabled = incomeState.isValid())
    }

    /**
     * Insert an Income Item into the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertIncome(incomeUiState: IncomeUiState) {
        flockRepository.insertIncome(incomeUiState.toIncome())
    }

    /**
     * Update an Income Item from the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateIncome(uiState: IncomeUiState) {
        flockRepository.updateIncome(uiState.toIncome())
    }

    /**
     * Delete an Income Item from the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteIncome(income: Income) {
        flockRepository.deleteIncome(income)
    }

    /**
     * Delete an Income Item from the database
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteIncome(uniqueID: String) {
        flockRepository.deleteIncome(uniqueID)
    }

    fun setIncomeID(id: Int) {
        savedStateHandle[AddIncomeScreenDestination.incomeIdArg] = id
    }
}