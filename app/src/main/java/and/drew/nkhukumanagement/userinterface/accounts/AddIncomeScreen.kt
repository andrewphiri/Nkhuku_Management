package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.Income
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.PickerDateDialog
import and.drew.nkhukumanagement.utils.currencySymbol
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object AddIncomeScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "add income"
    override val resourceId: Int
        get() = R.string.add_income
    const val incomeIdArg = "id"
    const val accountIdArg = "accountId"
    val routeWithArgs = "$route/{$incomeIdArg}/{$accountIdArg}"
    val arguments = listOf(navArgument(incomeIdArg) {
        defaultValue = 0
        type = NavType.IntType
    },
        navArgument(AddExpenseScreenDestination.accountIdArg) {
            defaultValue = 0
            type = NavType.IntType
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddIncomeScreen(
    modifier: Modifier = Modifier,
    incomeViewModel: IncomeViewModel = hiltViewModel(),
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    userPrefsViewModel: UserPrefsViewModel,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit
) {
    val accountsWithIncome by accountsViewModel.accountsWithIncome.collectAsState()
    val income by incomeViewModel.getIncome.collectAsState(
        initial = incomeViewModel.incomeUiState.copy(
            date = DateUtils().dateToStringShortFormat(
                LocalDate.now()
            ), pricePerItem = "0", quantity = "0", totalIncome = "0",
            cumulativeTotalIncome = "0"
        ).toIncome()
    )
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val currency by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )

    //if id is 0, set initial date to today's date else get date from income
    val dateState = if (incomeViewModel.incomeUiState.id == 0) rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )
    else rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = income.date
            .atStartOfDay()
            .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli())


    var isUpdateButtonEnabled by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    val context = LocalContext.current
    title = stringResource(AddIncomeScreenDestination.resourceId)

    /**
     * if nav argument IncomeID is greater than zero, update state. LaunchedEffect used because this
     * only updates the state once. Recomposition does not reset the incomeUiState values.
     * This should only be called again when income changes(KEY)
     */
    LaunchedEffect(income) {
        if (incomeViewModel.incomeID > 0) {
            incomeViewModel.updateState(income.toIncomeUiState(enabled = true))
        }
    }

    MainAddIncomeScreen(
        modifier = modifier,
        incomeUiState = incomeViewModel.incomeUiState,
        income = income,
        accountsSummary = accountsWithIncome.accountsSummary,
        updateState = incomeViewModel::updateState,
        insertIncome = {
            coroutineScope.launch {
                incomeViewModel.insertIncome(it)
            }
        },
        updateIncome = {
            coroutineScope.launch {
                incomeViewModel.updateIncome(it)
            }
        },
        updateAccountSummary = { accountSummary, incomeUiState ->
            coroutineScope.launch {
                accountsViewModel.updateAccount(
                    accountsSummary = accountSummary,
                    incomeUiState = incomeUiState
                )
            }
        },
        incomeIDArg = incomeViewModel.incomeID,
        canNavigateBack = canNavigateBack,
        onNavigateUp = onNavigateUp,
        currencySymbol = currency.symbol
    )
//    Scaffold(
//        topBar = {
//            FlockManagementTopAppBar(
//                title = if (incomeViewModel.incomeUiState.id > 0) context.resources.getString(R.string.edit_income)
//                else title,
//                canNavigateBack = canNavigateBack,
//                navigateUp = onNavigateUp
//            )
//        },
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { innerPadding ->
////        Log.i("INCOME", income.toIncomeUiState(true).toString())
////        Log.i("INCOMEUISTATE", incomeViewModel.incomeUiState.toString())
//        isUpdateButtonEnabled = if (incomeViewModel.incomeID > 0) incomeViewModel.incomeUiState !=
//                income.toIncomeUiState(enabled = true) else incomeViewModel.incomeUiState.enabled
//
//        Column(
//            modifier = modifier.verticalScroll(
//                state = scrollState
//            ).padding(innerPadding)
//        ) {
//            AddIncomeCard(
//                incomeUiState = incomeViewModel.incomeUiState,
//                onValueChanged = incomeViewModel::updateState,
//                onNavigateUp = onNavigateUp,
//                isUpdateButtonEnabled = isUpdateButtonEnabled,
//                onSaveIncome = {
//                    if (incomeViewModel.incomeID > 0) {
//                        if (handleNumberExceptions(incomeViewModel.incomeUiState)) {
//                            coroutineScope.launch {
//                                incomeViewModel.updateIncome(
//                                    incomeViewModel.incomeUiState.copy(
//                                        cumulativeTotalIncome = calculateCumulativeIncomeUpdate(
//                                            incomeViewModel.incomeUiState.cumulativeTotalIncome,
//                                            totalIncome = incomeViewModel.incomeUiState.totalIncome,
//                                            initialItemIncome = incomeViewModel.incomeUiState.initialItemIncome
//                                        ).toString()
//                                    )
//                                )
//                                accountsViewModel.updateAccount(
//                                accountsSummary = accountsWithIncome.accountsSummary,
//                                    incomeUiState = incomeViewModel.incomeUiState
//                                )
//                            }.invokeOnCompletion { onNavigateUp() }
//                        } else {
//                            coroutineScope.launch {
//                                snackbarHostState.showSnackbar(message = "Please enter a valid number.")
//                            }
//                        }
//                    } else {
//                        if (handleNumberExceptions(incomeViewModel.incomeUiState)) {
//                            coroutineScope.launch {
//                                incomeViewModel.insertIncome(
//                                    incomeViewModel.incomeUiState.copy(
//                                        flockUniqueID = accountsWithIncome.accountsSummary.flockUniqueID,
//                                        cumulativeTotalIncome = calculateCumulativeIncome(
//                                            initialIncome = accountsWithIncome.accountsSummary.totalIncome.toString(),
//                                            totalIncome = incomeViewModel.incomeUiState.totalIncome
//                                        ).toString()
//                                    )
//                                )
//                                accountsViewModel.updateAccount(
//                                        accountsSummary = accountsWithIncome.accountsSummary,
//                                        incomeUiState = incomeViewModel.incomeUiState
//                                )
//                            }.invokeOnCompletion { onNavigateUp() }
//                        } else {
//                            coroutineScope.launch {
//                                snackbarHostState.showSnackbar(message = "Please enter a valid number.")
//                            }
//                        }
//                    }
//                },
//                showDialog = showDialog,
//                onDismissed = { showDialog = false },
//                updateShowDialogOnClick = { showDialog = true },
//                label = "Date",
//                state = dateState,
//                saveDateSelected = { dateState ->
//                    val millisToLocalDate = dateState.selectedDateMillis?.let { millis ->
//                        DateUtils().convertMillisToLocalDate(
//                            millis
//                        )
//                    }
//                    val localDateToString = millisToLocalDate?.let { date ->
//                        DateUtils().dateToStringShortFormat(
//                            date
//                        )
//                    }
//                    localDateToString
//                },
//                currencySymbol = currency.symbol
//            )
//        }
//
//    }

}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainAddIncomeScreen(
    modifier: Modifier = Modifier,
    incomeUiState: IncomeUiState,
    income: Income?,
    accountsSummary: AccountsSummary,
    updateState: (IncomeUiState) -> Unit,
    insertIncome: (IncomeUiState) -> Unit,
    updateIncome: (IncomeUiState) -> Unit,
    updateAccountSummary: (AccountsSummary, IncomeUiState) -> Unit,
    incomeIDArg: Int,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    currencySymbol: String,
) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    //if id is 0, set initial date to today's date else get date from income
    val dateState = if (incomeUiState.id == 0) rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )
    else rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = income?.date
            ?.atStartOfDay()
            ?.atZone(ZoneId.of("UTC"))?.toInstant()?.toEpochMilli()
    )


    var isUpdateButtonEnabled by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    val context = LocalContext.current
    title = stringResource(AddIncomeScreenDestination.resourceId)

    /**
     * if nav argument IncomeID is greater than zero, update state. LaunchedEffect used because this
     * only updates the state once. Recomposition does not reset the incomeUiState values.
     * This should only be called again when income changes(KEY)
     */
    LaunchedEffect(income) {
        if (incomeIDArg > 0) {
            income?.toIncomeUiState(enabled = true)?.let { updateState(it) }
        }
    }

    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = if (incomeUiState.id > 0) context.resources.getString(R.string.edit_income)
                else title,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
//        Log.i("INCOME", income.toIncomeUiState(true).toString())
//        Log.i("INCOMEUISTATE", incomeViewModel.incomeUiState.toString())
        isUpdateButtonEnabled = if (incomeIDArg > 0) incomeUiState !=
                income?.toIncomeUiState(enabled = true) else incomeUiState.enabled

        Column(
            modifier = modifier.verticalScroll(
                state = scrollState
            ).padding(innerPadding)
        ) {
            AddIncomeCard(
                incomeUiState = incomeUiState,
                onValueChanged = updateState,
                onNavigateUp = onNavigateUp,
                isUpdateButtonEnabled = isUpdateButtonEnabled,
                onSaveIncome = {
                    if (incomeIDArg > 0) {
                        if (handleNumberExceptions(incomeUiState)) {
                            updateIncome(
                                incomeUiState.copy(
                                    cumulativeTotalIncome = calculateCumulativeIncomeUpdate(
                                        incomeUiState.cumulativeTotalIncome,
                                        totalIncome = incomeUiState.totalIncome,
                                        initialItemIncome = incomeUiState.initialItemIncome
                                    ).toString()
                                )
                            )
                            updateAccountSummary(
                                accountsSummary,
                                incomeUiState
                            )
                            onNavigateUp()
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = "Please enter a valid number.")
                            }
                        }
                    } else {
                        if (handleNumberExceptions(incomeUiState)) {

                            insertIncome(
                                incomeUiState.copy(
                                    flockUniqueID = accountsSummary.flockUniqueID,
                                    cumulativeTotalIncome = calculateCumulativeIncome(
                                        initialIncome = accountsSummary.totalIncome.toString(),
                                        totalIncome = incomeUiState.totalIncome
                                    ).toString()
                                )
                            )
                            updateAccountSummary(
                                accountsSummary,
                                incomeUiState
                            )
                            onNavigateUp()
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = "Please enter a valid number.")
                            }
                        }
                    }
                },
                showDialog = showDialog,
                onDismissed = { showDialog = false },
                updateShowDialogOnClick = { showDialog = true },
                label = "Date",
                state = dateState,
                saveDateSelected = { dateState ->
                    val millisToLocalDate = dateState.selectedDateMillis?.let { millis ->
                        DateUtils().convertMillisToLocalDate(
                            millis
                        )
                    }
                    val localDateToString = millisToLocalDate?.let { date ->
                        DateUtils().dateToStringShortFormat(
                            date
                        )
                    }
                    localDateToString
                },
                currencySymbol = currencySymbol
            )
        }

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIncomeCard(
    modifier: Modifier = Modifier,
    incomeUiState: IncomeUiState,
    onValueChanged: (IncomeUiState) -> Unit,
    onNavigateUp: () -> Unit,
    onSaveIncome: () -> Unit,
    isUpdateButtonEnabled: Boolean,
    showDialog: Boolean,
    label: String,
    onDismissed: () -> Unit,
    updateShowDialogOnClick: (Boolean) -> Unit,
    saveDateSelected: (DatePickerState) -> String?,
    state: DatePickerState,
    currencySymbol: String
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        PickerDateDialog(
            showDialog = showDialog,
            label = label,
            onDismissed = onDismissed,
            updateShowDialogOnClick = updateShowDialogOnClick,
            date = incomeUiState.getDate(),
            saveDateSelected = saveDateSelected,
            datePickerState = state,
            onValueChanged = { onValueChanged(incomeUiState.copy(date = it)) },
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = incomeUiState.incomeName,
            onValueChange = { onValueChanged(incomeUiState.copy(incomeName = it)) },
            label = { Text("Description") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = incomeUiState.customer,
            onValueChange = { onValueChanged(incomeUiState.copy(customer = it)) },
            label = { Text("Customer") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = incomeUiState.pricePerItem,
            onValueChange = {
                onValueChanged(
                    incomeUiState.copy(
                        pricePerItem = it,
                        totalIncome = calculateTotalIncome(
                            quantity = incomeUiState.quantity,
                            pricePerItem = it
                        ).toString()
                    )
                )
            },
            label = { Text("Unit Price") },
            prefix = {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = currencySymbol
                    )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = incomeUiState.quantity,
            onValueChange = {
                onValueChanged(
                    incomeUiState.copy(
                        quantity = it,
                        totalIncome = calculateTotalIncome(
                            quantity = it,
                            pricePerItem = incomeUiState.pricePerItem
                        ).toString()
                    )
                )
            },
            label = { Text("Quantity") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
//       onValueChanged(expensesUiState.copy(totalExpense = calculateTotalExpense(
//           expensesUiState.quantity, expensesUiState.costPerItem).toString() ))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = incomeUiState.totalIncome,
            onValueChange = { },
            prefix = {
                currencySymbol()?.let {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = it
                    )
                }
            },
            label = { Text("Total Income") },
            readOnly = true,
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = incomeUiState.notes,
            onValueChange = { onValueChanged(incomeUiState.copy(notes = it)) },
            label = { Text("Notes") },
            minLines = 2,
            maxLines = 2
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f, true),
                onClick = onNavigateUp,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Cancel",
                    textAlign = TextAlign.Center
                )
            }

            Button(
                modifier = Modifier
                    .semantics { contentDescription = "save button" }
                    .weight(1f, true),
                onClick = onSaveIncome,
                enabled = isUpdateButtonEnabled
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (incomeUiState.id > 0) "Update" else "Save",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerDialog(
    showDialog: Boolean,
    label: String,
    onDismissed: () -> Unit,
    updateShowDialogOnClick: (Boolean) -> Unit,
    incomeUiState: IncomeUiState,
    saveDateSelected: (DatePickerState) -> String,
    onValueChanged: (IncomeUiState) -> Unit
) {
    val state = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )

    incomeUiState.setDate(saveDateSelected(state))

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = incomeUiState.getDate(),
        onValueChange = { onValueChanged(incomeUiState.copy(date = it)) },
        label = { Text(text = label) },
        singleLine = true,
        readOnly = true,
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Unspecified,
            errorCursorColor = Color.Unspecified
        ),
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            updateShowDialogOnClick(showDialog)
                        }
                    }
                }
            }
    )
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = onDismissed,
            confirmButton = {
                Button(
                    onClick = onDismissed
                ) { Text("OK") }
            },
            dismissButton = {
                Button(onClick = onDismissed) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = state,
                modifier = Modifier,
                showModeToggle = false,
            )
        }
    }
}