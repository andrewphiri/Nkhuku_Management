package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.AccountsWithIncome
import and.drew.nkhukumanagement.data.EggsSummary
import and.drew.nkhukumanagement.data.FlockAndEggsSummary
import and.drew.nkhukumanagement.data.Income
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.flock.EditFlockViewModel
import and.drew.nkhukumanagement.userinterface.flock.EggsInventoryViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockUiState
import and.drew.nkhukumanagement.userinterface.flock.toFlock
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.DropDownMenuDialog
import and.drew.nkhukumanagement.utils.PickerDateDialog
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
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

@Serializable
data class AddIncomeScreenNav(
    val incomeId: Int,
    val accountId: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddIncomeScreen(
    modifier: Modifier = Modifier,
    incomeViewModel: IncomeViewModel = hiltViewModel(),
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    userPrefsViewModel: UserPrefsViewModel,
    editFlockViewModel: EditFlockViewModel = hiltViewModel(),
    eggsInventoryViewModel: EggsInventoryViewModel = hiltViewModel(),
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    contentType: ContentType,
    incomeID: Int,
    accountID: Int
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val flockTypeOptions = context.resources.getStringArray(R.array.types_of_flocks).toList()
    val accountsWithIncome by accountsViewModel.accountsWithIncome.collectAsState(
        AccountsWithIncome(
            accountsSummary = AccountsSummary(
                flockUniqueID = "",
                batchName = "",
                totalIncome = 0.0,
                totalExpenses = 0.0,
                variance = 0.0
            )
        )
    )
    val flock by editFlockViewModel.flock.collectAsState(
        initial =  FlockUiState(
            datePlaced = DateUtils().dateToStringLongFormat(LocalDate.now()),
            quantity = "0",
            donorFlock = "0",
            cost = "0"
        ).toFlock()
    )
    val income by incomeViewModel.income.collectAsState(
        initial = incomeViewModel.incomeUiState.copy(
            date = DateUtils().dateToStringShortFormat(
                LocalDate.now()
            ), pricePerItem = "0", quantity = "0", totalIncome = "0",
            cumulativeTotalIncome = "0"
        ).toIncome()
    )

    val flockAndEggsSummary by editFlockViewModel
        .flockAndEggsSummaryStateFlow
        .collectAsState(
            initial = FlockAndEggsSummary(flock = null,
                eggsSummary = EggsSummary(
                    flockUniqueID = "",
                    totalGoodEggs = 0,
                    totalBadEggs = 0,
                    date = LocalDate.now()
                )
            )
        )

    val userPreferences by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )

    var title by remember { mutableStateOf("") }
    title = stringResource(AddIncomeScreenDestination.resourceId)
    //val incomeID by incomeViewModel.incomeID.collectAsState(initial = 0)
    var expanded by remember { mutableStateOf(false) }

        FlockUiState(
            datePlaced = DateUtils().dateToStringLongFormat(LocalDate.now()),
            quantity = "0",
            donorFlock = "0",
            cost = "0"
        ).toFlock()


    LaunchedEffect(Unit) {
        if (incomeID > 0) {

        }
        async { accountsViewModel.getAccountsWithIncome(accountID) }.await()
        async { editFlockViewModel.getFlock(accountsWithIncome?.accountsSummary?.flockUniqueID) }.await()
    }

    LaunchedEffect(flock) {
       if (flock != null) {
           flock?.id?.let { editFlockViewModel.getFlockAndEggsSummary(it) }
       }
    }

    /**
     * if nav argument IncomeID is greater than zero, update state. LaunchedEffect used because this
     * only updates the state once. Recomposition does not reset the incomeUiState values.
     * This should only be called again when income changes(KEY)
     */
    LaunchedEffect(income) {
        if (incomeID > 0) {
            async { incomeViewModel.getIncome(incomeID) }.await()
            income?.let { incomeViewModel.updateState(it.toIncomeUiState(enabled = true)) }
        }
    }

    MainAddIncomeScreen(
        modifier = modifier,
        incomeUiState = incomeViewModel.incomeUiState,
        income = income,
        accountsSummary = accountsWithIncome?.accountsSummary  ,
        updateState = incomeViewModel::updateState,
        insertIncome = {
            coroutineScope.launch {
                incomeViewModel.insertIncome(it)
                if (incomeViewModel.incomeUiState.incomeType == context.getString(R.string.chicken_sale)) {
                    flock?.let { editFlockViewModel.updateFlock(it.copy(stock = (it.stock - incomeViewModel.incomeUiState.quantity.toInt()))) }
                }

                flock?.let {
                    if(incomeViewModel.incomeUiState.quantity.toInt() > it.stock) {
                        Toast.makeText(context, context.getString(R.string.not_enough_stock), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
        updateIncome = {
            coroutineScope.launch {
                incomeViewModel.updateIncome(it)
                if (incomeViewModel.incomeUiState.incomeType == context.getString(R.string.chicken_sale)) {
                    flock?.let { editFlockViewModel.updateFlock(it.copy(stock = ((it.stock + (income?.quantity ?: 0)) - incomeViewModel.incomeUiState.quantity.toInt()))) }
                }

                flock?.let {
                    if(incomeViewModel.incomeUiState.quantity.toInt() > it.stock) {
                        Toast.makeText(context, context.getString(R.string.not_enough_stock), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
        updateAccountSummary = { accountSummary, incomeUiState ->
            coroutineScope.launch {
                accountsViewModel.updateAccount(
                    accountsSummary = accountSummary,
                    incomeUiState = incomeUiState
                )

                flockAndEggsSummary?.eggsSummary?.let {
                    EggsSummary(
                        flockUniqueID = it.flockUniqueID,
                        totalGoodEggs = it.totalGoodEggs - (incomeUiState.quantity.toInt() * userPreferences.traySize.toInt()),
                        totalBadEggs = it.totalBadEggs,
                        date = it.date
                    )
                }?.let {
                    eggsInventoryViewModel.updateEggsSummary(
                        it
                    )
                }
            }
        },
        incomeIDArg = incomeID,
        canNavigateBack = canNavigateBack,
        onNavigateUp = onNavigateUp,
        currencySymbol = userPreferences.symbol,
        contentType = contentType,
        onDismissIncomeTypeDropDownMenu = {
                                          expanded = false
        } ,
        incomeTypeOptions = if (flock?.flockType == flockTypeOptions[1]) incomeViewModel.incomeTypeOptionsLayers else incomeViewModel.incomeTypeOptions,
        onExpand = {
                   expanded = !expanded
        },
        expanded = expanded,
        traySize = userPreferences.traySize
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAddIncomeScreen(
    modifier: Modifier = Modifier,
    incomeUiState: IncomeUiState,
    income: Income?,
    accountsSummary: AccountsSummary?,
    updateState: (IncomeUiState) -> Unit,
    insertIncome: (IncomeUiState) -> Unit,
    updateIncome: (IncomeUiState) -> Unit,
    updateAccountSummary: (AccountsSummary?, IncomeUiState) -> Unit,
    incomeIDArg: Int,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    currencySymbol: String,
    contentType: ContentType,
    onDismissIncomeTypeDropDownMenu: () -> Unit,
    onExpand: (Boolean) -> Unit,
    expanded: Boolean,
    incomeTypeOptions: List<String>,
    traySize: String
) {
    BackHandler {
        onNavigateUp()
    }
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
                navigateUp = onNavigateUp,
                contentType = contentType
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

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
                                snackbarHostState.showSnackbar(message = context.getString(R.string.please_enter_a_valid_number))
                            }
                        }
                    } else {
                        if (handleNumberExceptions(incomeUiState)) {

                            insertIncome(
                                incomeUiState.copy(
                                    flockUniqueID = accountsSummary?.flockUniqueID ?: "",
                                    cumulativeTotalIncome = calculateCumulativeIncome(
                                        initialIncome = accountsSummary?.totalIncome.toString(),
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
                                snackbarHostState.showSnackbar(message = context.getString(R.string.please_enter_a_valid_number))
                            }
                        }
                    }
                },
                showDialog = showDialog,
                onDismissDateDialog = { showDialog = false },
                updateShowDialogOnClick = { showDialog = true },
                label = stringResource(R.string.date),
                state = dateState,
                saveDateSelected = { dateState ->
                    val millisToLocalDate = dateState.selectedDateMillis?.let { millis ->
                        DateUtils().convertMillisToLocalDate(
                            millis
                        )
                    }
                    val localDateToString = millisToLocalDate?.let { date ->
                        DateUtils().dateToStringShortFormat(
                            date.toLocalDate()
                        )
                    }
                    localDateToString
                },
                currencySymbol = currencySymbol,
                onDismissIncomeTypeDropDownMenu = onDismissIncomeTypeDropDownMenu,
                incomeTypeOptions = incomeTypeOptions,
                onExpand = onExpand,
                expanded = expanded,
                traySize = traySize
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
    onDismissDateDialog: () -> Unit,
    expanded: Boolean,
    onExpand: (Boolean) -> Unit,
    onDismissIncomeTypeDropDownMenu: () -> Unit,
    updateShowDialogOnClick: (Boolean) -> Unit,
    saveDateSelected: (DatePickerState) -> String?,
    state: DatePickerState,
    currencySymbol: String,
    incomeTypeOptions: List<String>,
    traySize:String
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PickerDateDialog(
            showDialog = showDialog,
            label = label,
            onDismissed = onDismissDateDialog,
            updateShowDialogOnClick = updateShowDialogOnClick,
            date = incomeUiState.getDate(),
            saveDateSelected = saveDateSelected,
            datePickerState = state,
            onValueChanged = { onValueChanged(incomeUiState.copy(date = it)) },
        )

        DropDownMenuDialog(
            value = incomeUiState.incomeType,
            onDismissed = onDismissIncomeTypeDropDownMenu,
            options = incomeTypeOptions,
            onOptionSelected = {
                               onValueChanged(incomeUiState.copy(incomeType = it, incomeName = it))
            },
            onExpand = onExpand,
            label = stringResource(R.string.income_type),
            expanded = expanded,
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = if(incomeUiState.incomeType == stringResource(R.string.chicken_sale)) incomeUiState.incomeType else incomeUiState.incomeName,
            onValueChange = { onValueChanged(incomeUiState.copy(incomeName = it)) },
            enabled = incomeUiState.incomeType != stringResource(R.string.chicken_sale),
            readOnly = incomeUiState.incomeType == stringResource(R.string.chicken_sale),
            label = { Text(text = stringResource(R.string.description)) },
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = incomeUiState.customer,
            onValueChange = { onValueChanged(incomeUiState.copy(customer = it)) },
            label = { Text(stringResource(R.string.customer)) },
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
            label = { Text(text = stringResource(R.string.unit_price)) },
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
            label = { Text(text = stringResource(R.string.quantity)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            suffix = {
                if (incomeUiState.incomeType == "Eggs") {
                    OutlinedTextField(
                        modifier = Modifier.padding(start = 4.dp),
                        value = traySize,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = stringResource(R.string.tray_size)) }
                    )
                }
            }
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = incomeUiState.totalIncome,
            onValueChange = { },
            prefix = {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = currencySymbol
                    )

            },
            label = { Text(stringResource(R.string.total_income)) },
            readOnly = true,
            singleLine = true
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = incomeUiState.notes,
            onValueChange = { onValueChanged(incomeUiState.copy(notes = it)) },
            label = { Text(text = stringResource(R.string.notes)) },
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
                    text = stringResource(R.string.cancel),
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
                    text = if (incomeUiState.id > 0) stringResource(R.string.update) else stringResource(
                        R.string.save
                    ),
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