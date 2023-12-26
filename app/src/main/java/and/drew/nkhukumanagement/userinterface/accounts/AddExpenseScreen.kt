package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.Expense
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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

object AddExpenseScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "add expense"
    override val resourceId: Int
        get() = R.string.add_expense
    const val expenseIdArg = "id"
    const val accountIdArg = "accountId"
    val routeWithArgs = "$route/{$expenseIdArg}/{$accountIdArg}"
    val arguments = listOf(navArgument(expenseIdArg) {
        defaultValue = 0
        type = NavType.IntType
    },
        navArgument(accountIdArg) {
            defaultValue = 0
            type = NavType.IntType
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddExpenseScreen(
    modifier: Modifier = Modifier,
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    userPrefsViewModel: UserPrefsViewModel,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val accountsWithExpense by accountsViewModel.accountsWithExpense.collectAsState()
    val currency by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )

    val expense by expenseViewModel.getExpense.collectAsState(
        initial = expenseViewModel.expenseUiState.copy(
            date = DateUtils().dateToStringShortFormat(
                LocalDate.now()
            ), costPerItem = "0", quantity = "0", totalExpense = "0",
            cumulativeTotalExpense = "0"
        ).toExpense()
    )

    //if id is 0, set initial date to today's date else get date from expense
    val dateState = if (expenseViewModel.expenseUiState.id == 0) rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis =   LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli())
    else rememberDatePickerState(
                initialDisplayMode = DisplayMode.Picker,
                initialSelectedDateMillis = expense.date
                    .atStartOfDay()
                    .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli())

    var isUpdateButtonEnabled by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    title = stringResource(AddExpenseScreenDestination.resourceId)

    /**
     * if nav argument expenseID is greater than zero, update state. LaunchedEffect used because this
     * only updates the state once. Recomposition does not reset the expenseUiState values.
     * This should only be called again when expense changes(KEY)
     */
    LaunchedEffect(expense) {
        if (expenseViewModel.expenseID > 0) {
            expenseViewModel.updateState(expense.toExpenseUiState(enabled = true))
        }
    }

    MainAddExpenseScreen(
        modifier = modifier,
        expenseUiState = expenseViewModel.expenseUiState,
        expense = expense,
        accountsSummary = accountsWithExpense.accountsSummary,
        updateState = expenseViewModel::updateState,
        insertExpense = {
            coroutineScope.launch {
                expenseViewModel.insertExpense(it)
            }
        },
        updateExpense = {
            coroutineScope.launch {
                expenseViewModel.updateExpense(it)
            }
        },
        updateAccountSummary = { accountSummary, expenseUiState ->
            coroutineScope.launch {
                accountsViewModel.updateAccount(
                    accountsSummary = accountSummary,
                    expenseUiState = expenseUiState
                )
            }
        },
        expenseIDArg = expenseViewModel.expenseID,
        canNavigateBack = canNavigateBack,
        onNavigateUp = onNavigateUp,
        currencySymbol = currency.symbol
    )

//    Scaffold(
//        topBar = {
//            FlockManagementTopAppBar(
//                title = if (expenseViewModel.expenseUiState.id > 0) context.resources.getString(R.string.edit_expense)
//                else title,
//                canNavigateBack = canNavigateBack,
//                navigateUp = onNavigateUp
//            )
//        },
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { innerPadding ->
//
//        isUpdateButtonEnabled = if (expenseViewModel.expenseID > 0) expenseViewModel.expenseUiState !=
//                expense.toExpenseUiState(enabled = true) else expenseViewModel.expenseUiState.isEnabled
//
//        Column(
//            modifier = modifier.verticalScroll(
//                state = scrollState
//            ).padding(innerPadding)
//        ) {
//            AddExpenseCard(
//                expensesUiState = expenseViewModel.expenseUiState,
//                onValueChanged = expenseViewModel::updateState,
//                onNavigateUp = onNavigateUp,
//                isUpdateButtonEnabled = isUpdateButtonEnabled,
//                onSaveExpense = {
//                    if (expenseViewModel.expenseID > 0) {
//                        if (handleNumberExceptions(
//                                expenseViewModel.expenseUiState.copy(
//                                    cumulativeTotalExpense = calculateCumulativeExpenseUpdate(
//                                        expenseViewModel.expenseUiState.cumulativeTotalExpense,
//                                        totalExpense = expenseViewModel.expenseUiState.totalExpense,
//                                        initialItemExpense = expenseViewModel.expenseUiState.initialItemExpense
//                                    ).toString()
//                                )
//                            )
//                        ) {
//                            coroutineScope.launch {
//                                expenseViewModel.updateExpense(expenseViewModel.expenseUiState)
//                                    accountsViewModel.updateAccount(
//                                        accountsSummary = accountsWithExpense.accountsSummary,
//                                        expenseUiState = expenseViewModel.expenseUiState
//                                    )
//                            }.invokeOnCompletion { onNavigateUp() }
//                        } else {
//                            coroutineScope.launch {
//                                snackbarHostState.showSnackbar(message = "Please enter a valid number.")
//                            }
//                        }
//                    } else {
//                        if (handleNumberExceptions(expenseViewModel.expenseUiState)) {
//                            coroutineScope.launch {
//                                expenseViewModel.insertExpense(
//                                    expenseViewModel.expenseUiState.copy(
//                                        flockUniqueID = accountsWithExpense.accountsSummary.flockUniqueID,
//                                        cumulativeTotalExpense = calculateCumulativeExpense(
//                                            accountsWithExpense.accountsSummary.totalExpenses.toString(),
//                                            expenseViewModel.expenseUiState.totalExpense
//                                        ).toString()
//                                    )
//                                )
//                                accountsViewModel.updateAccount(
//                                    accountsSummary = accountsWithExpense.accountsSummary,
//                                    expenseUiState = expenseViewModel.expenseUiState
//                                )
//                            }.invokeOnCompletion { onNavigateUp() }
//                        } else {
//                            coroutineScope.launch {
//                                snackbarHostState.showSnackbar(message = "Please enter a valid number.")
//                            }
//                            Log.i("EXPENSEUISTATE", accountsWithExpense.accountsSummary.flockUniqueID)
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
fun MainAddExpenseScreen(
    modifier: Modifier = Modifier,
    expenseUiState: ExpensesUiState,
    expense: Expense,
    accountsSummary: AccountsSummary,
    updateState: (ExpensesUiState) -> Unit,
    insertExpense: (ExpensesUiState) -> Unit,
    updateExpense: (ExpensesUiState) -> Unit,
    updateAccountSummary: (AccountsSummary, ExpensesUiState) -> Unit,
    expenseIDArg: Int,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    currencySymbol: String,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()


//    val expense by expenseViewModel.getExpense.collectAsState(
//        initial = expenseViewModel.expenseUiState.copy(
//            date = DateUtils().dateToStringShortFormat(
//                LocalDate.now()
//            ), costPerItem = "0", quantity = "0", totalExpense = "0",
//            cumulativeTotalExpense = "0"
//        ).toExpense()
//    )

    //if id is 0, set initial date to today's date else get date from expense
    val dateState = if (expenseUiState.id == 0) rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )
    else rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = expense.date
            .atStartOfDay()
            .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )

    var isUpdateButtonEnabled by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    title = stringResource(AddExpenseScreenDestination.resourceId)

    /**
     * if nav argument expenseID is greater than zero, update state. LaunchedEffect used because this
     * only updates the state once. Recomposition does not reset the expenseUiState values.
     * This should only be called again when expense changes(KEY)
     */
    LaunchedEffect(expense) {
        if (expenseIDArg > 0) {
            updateState(expense.toExpenseUiState(enabled = true))
        }
    }

    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = if (expenseUiState.id > 0) context.resources.getString(R.string.edit_expense)
                else title,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        isUpdateButtonEnabled = if (expenseIDArg > 0) expenseUiState !=
                expense.toExpenseUiState(enabled = true) else expenseUiState.isEnabled

        Column(
            modifier = modifier.verticalScroll(
                state = scrollState
            ).padding(innerPadding)
        ) {
            AddExpenseCard(
                expensesUiState = expenseUiState,
                onValueChanged = updateState,
                onNavigateUp = onNavigateUp,
                isUpdateButtonEnabled = isUpdateButtonEnabled,
                onSaveExpense = {
                    if (expenseIDArg > 0) {
                        if (handleNumberExceptions(
                                expenseUiState.copy(
                                    cumulativeTotalExpense = calculateCumulativeExpenseUpdate(
                                        expenseUiState.cumulativeTotalExpense,
                                        totalExpense = expenseUiState.totalExpense,
                                        initialItemExpense = expenseUiState.initialItemExpense
                                    ).toString()
                                )
                            )
                        ) {
                            updateExpense(expenseUiState)
                            updateAccountSummary(
                                accountsSummary,
                                expenseUiState
                            )
                            onNavigateUp()
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = "Please enter a valid number.")
                            }
                        }
                    } else {
                        if (handleNumberExceptions(expenseUiState)) {
                            insertExpense(
                                expenseUiState.copy(
                                    flockUniqueID = accountsSummary.flockUniqueID,
                                    cumulativeTotalExpense = calculateCumulativeExpense(
                                        accountsSummary.totalExpenses.toString(),
                                        expenseUiState.totalExpense
                                    ).toString()
                                )
                            )
                            updateAccountSummary(
                                accountsSummary,
                                expenseUiState
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
fun AddExpenseCard(
    modifier: Modifier = Modifier,
    expensesUiState: ExpensesUiState,
    onValueChanged: (ExpensesUiState) -> Unit,
    onSaveExpense: () -> Unit,
    onNavigateUp: () -> Unit,
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
            date = expensesUiState.getDate(),
            saveDateSelected = saveDateSelected,
            datePickerState = state,
            onValueChanged = { onValueChanged(expensesUiState.copy(date = it)) },
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = expensesUiState.expenseName,
            onValueChange = { onValueChanged(expensesUiState.copy(expenseName = it)) },
            label = { Text("Description") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = expensesUiState.supplier,
            onValueChange = { onValueChanged(expensesUiState.copy(supplier = it)) },
            label = { Text("Supplier") },
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = expensesUiState.costPerItem,
            onValueChange = {
                onValueChanged(
                    expensesUiState.copy(
                        costPerItem = it,
                        totalExpense = calculateTotalExpense(
                            quantity = expensesUiState.quantity,
                            pricePerItem = it
                        ).toString()
                    )
                )
            },
            prefix = {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = currencySymbol
                    )
            },
            label = { Text("Unit Price") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = expensesUiState.quantity,
            onValueChange = {
                onValueChanged(
                    expensesUiState.copy(
                        quantity = it,
                        totalExpense = calculateTotalExpense(
                            quantity = it,
                            pricePerItem = expensesUiState.costPerItem
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
            value = expensesUiState.totalExpense,
            onValueChange = { },
            prefix = {
                currencySymbol()?.let {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = it
                    )
                }
            },
            label = { Text("Total Expense") },
            readOnly = true,
            singleLine = true,
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = expensesUiState.notes,
            onValueChange = { onValueChanged(expensesUiState.copy(notes = it)) },
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
                modifier = Modifier.weight(1f, true),
                onClick = onSaveExpense,
                enabled = isUpdateButtonEnabled,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (expensesUiState.id > 0) "Update" else "Save",
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
    expensesUiState: ExpensesUiState,
    saveDateSelected: (DatePickerState) -> String,
    onValueChanged: (ExpensesUiState) -> Unit
) {
    val state = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )

    expensesUiState.setDate(saveDateSelected(state))

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = expensesUiState.getDate(),
        onValueChange = { onValueChanged(expensesUiState.copy(date = it)) },
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
