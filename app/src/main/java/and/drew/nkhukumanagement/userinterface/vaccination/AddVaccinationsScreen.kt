package and.drew.nkhukumanagement.userinterface.vaccination

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.accounts.AccountsViewModel
import and.drew.nkhukumanagement.userinterface.feed.FeedViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import and.drew.nkhukumanagement.userinterface.flock.toFlock
import and.drew.nkhukumanagement.userinterface.flock.toFlockUiState
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.userinterface.weight.WeightViewModel
import and.drew.nkhukumanagement.utils.AddNewEntryDialog
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.DropDownMenuDialog
import and.drew.nkhukumanagement.utils.PickerDateDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.UUID


object AddVaccinationsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.MedicalServices
    override val route: String
        get() = "Add vaccinations"
    override val resourceId: Int
        get() = R.string.add_vaccinations
    const val flockIdArg = "id_arg"
    val routeWithArgs = "$route/{$flockIdArg}"
    val argument = listOf(
        navArgument(flockIdArg) {
            defaultValue = 0
            type = NavType.IntType
        },
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddVaccinationsScreen(
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    vaccinationViewModel: VaccinationViewModel = hiltViewModel(),
    detailsViewModel: FlockDetailsViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel,
    weightViewModel: WeightViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel(),
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    userPrefsViewModel: UserPrefsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val flockWithVaccinations by detailsViewModel.flockWithVaccinationsStateFlow.collectAsState()
    val withVaccinationsToEdit by vaccinationViewModel.flockWithVaccinationsStateFlow.collectAsState()
    var vaccinesList: List<Vaccination> = listOf()
    val vaccinesStateList: MutableList<VaccinationUiState> = mutableListOf()
    val flockList by detailsViewModel.allFlocks.collectAsState()
    val userPreferences by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )

    var title by remember { mutableStateOf("") }
    title = stringResource(AddVaccinationsDestination.resourceId)
    var isEditingEnabled by rememberSaveable { mutableStateOf(false) }
    var isFABVisible by remember { mutableStateOf(false) }
    var isDoneButtonShowing by remember { mutableStateOf(true) }
    var isAddShowing by remember { mutableStateOf(true) }
    val vaccinationDates = vaccinationViewModel.getInitialVaccinationList()
    var listSize by remember { mutableStateOf(vaccinationViewModel.getInitialVaccinationList().size) }
    var isRemoveShowing by remember { mutableStateOf(true) }
    var isDoneEnabled by rememberSaveable { mutableStateOf(true) }


    if (flockEntryViewModel.flockUiState.id == 0) {
        vaccinationViewModel.setInitialVaccinationDates(
            vaccinationViewModel.defaultVaccinationDates(
                flockEntryViewModel.flockUiState, vaccinationViewModel.vaccinationUiState
            )
        )
        isEditingEnabled = true
    } else {
        title = stringResource(R.string.vaccinations)
        isFABVisible = true
        isDoneButtonShowing = false
        isAddShowing = false
        isRemoveShowing = false
        vaccinesList = withVaccinationsToEdit?.vaccinations ?: listOf()
        flockWithVaccinations?.vaccinations?.let {

        }
        for ((index, vaccination) in vaccinesList.withIndex()) {
            vaccinesStateList.add(
                vaccination.toVaccinationUiState(
                    enabled = true,
                    vaccinationNumber = index + 1
                )
            )
        }

        vaccinationViewModel.setInitialVaccinationDates(vaccinesStateList.toMutableStateList())
    }

    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = title,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                isRemoveShowing = isRemoveShowing,
                isAddShowing = isAddShowing,
                isDoneShowing = isDoneButtonShowing,
                isDoneEnabled = isDoneEnabled,
                onClickRemove = {
                    listSize--
                    if (listSize == 1) {
                        isRemoveShowing = false
                    }
                    //isDoneEnabled = vaccinationDates.all { it.isValid() }
                    vaccinationViewModel.getInitialVaccinationList().removeAt(
                        listSize
                    )

                },
                onClickAdd = {
                    listSize++
                    isRemoveShowing = true
                    //isDoneEnabled = vaccinationDates.all { it.isValid() }
                    val newDate = DateUtils().stringToLocalDate(
                        vaccinationViewModel.getInitialVaccinationList().last().getDate()
                    )
                    val vaccineDate =
                        DateUtils().dateToStringLongFormat(DateUtils().calculateDate(newDate, 7))
                    vaccinationViewModel.getInitialVaccinationList().add(
                        vaccinationDates.last().copy(
                            vaccinationNumber = listSize, name = "", date = vaccineDate
                        )
                    )
                },
                onSaveToDatabase = {
                    if (flockEntryViewModel.flockUiState.id == 0) {
                        val uniqueId = UUID.randomUUID().toString()
                        flockEntryViewModel.flockUiState.setUniqueId(uniqueID = uniqueId)
                        vaccinationViewModel.vaccinationUiState.setUniqueId(uniqueID = uniqueId)

                        weightViewModel.setWeightList(
                            weightViewModel.defaultWeight(
                                flockEntryViewModel.flockUiState
                            )
                        )
                        feedViewModel.setFeedList(
                            feedViewModel.defaultFeedInformationList(
                                flockEntryViewModel.flockUiState
                            )
                        )
                        val flock =
                            if (!flockList.flockList.isNullOrEmpty()) flockList.flockList.last() else
                                flockEntryViewModel.flockUiState.toFlock()
                        if (userPreferences.receiveNotifications) {
                            vaccinationViewModel.getInitialVaccinationList().forEach {
                                flock.toFlockUiState().let { flock ->
                                    vaccinationViewModel.schedule(
                                        it.toVaccination(),
                                        flock.copy(id = flock.id + 1)
                                    )
                                    Log.i(
                                        "VACCINATION_HASHCODE",
                                        it.toVaccination().hashCode().toString()
                                    )
                                }
                            }
                        }

                        coroutineScope.launch {
                            flockEntryViewModel.saveItem()
                            accountsViewModel.insertAccount(
                                accountsViewModel
                                    .Accounts(flockEntryViewModel.flockUiState)
                            )
                            accountsViewModel.insertExpense(
                                accountsViewModel
                                    .ExpenseToInsert(flockEntryViewModel.flockUiState)
                            )
                            weightViewModel.getWeightList().forEach {
                                weightViewModel.saveInitialWeight(it)
                            }
                            feedViewModel.getFeedList().forEach {
                                feedViewModel.saveFeed(it)
                            }
                            vaccinationViewModel.getInitialVaccinationList().forEach {
                                vaccinationViewModel.saveVaccination(
                                    it.copy(
                                        flockUniqueId = flockEntryViewModel.flockUiState.getUniqueId()
                                    )
                                )

                            }
                        }.invokeOnCompletion {

                            navigateBack()
                        }
                    } else {
                        val flockUniqueID = flockEntryViewModel.flockUiState.getUniqueId()
                        withVaccinationsToEdit?.vaccinations?.forEach {
                            vaccinationViewModel.cancelAlarm(it)
                        }

                        if (userPreferences.receiveNotifications) {
                            vaccinationViewModel.getInitialVaccinationList().forEach {
                                vaccinationViewModel.schedule(
                                    vaccination = it.toVaccination(),
                                    flock = flockEntryViewModel.flockUiState
                                )
                            }
                        }

                        coroutineScope.launch {
                            vaccinationViewModel.deleteVaccination(flockUniqueID)

                            vaccinationViewModel.getInitialVaccinationList().forEach {
                                vaccinationViewModel.saveVaccination(it.copy(flockUniqueId = flockUniqueID))
                            }

                        }.invokeOnCompletion { onNavigateUp() }
                    }

                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(visible = isFABVisible,
                enter = slideIn(tween(200, easing = LinearOutSlowInEasing),
                    initialOffset = {
                        IntOffset(180, 90)
                    }),
                exit = slideOut(tween(200, easing = FastOutSlowInEasing)) {
                    IntOffset(180, 90)
                }) {
                FloatingActionButton(
                    onClick = {
                        title = context.resources.getString(R.string.edit_vaccinations)
                        isEditingEnabled = true
                        isFABVisible = false
                        isDoneButtonShowing = true
                        isAddShowing = true
                        isRemoveShowing = true

                        vaccinationViewModel.getInitialVaccinationList().onEach {
                            if (it.isExpanded) {
                                it.isExpanded = false
                            }
                        }
                    },
                    shape = ShapeDefaults.Small,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    elevation = FloatingActionButtonDefaults.elevation()
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Vaccinations",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        listSize = vaccinationViewModel.getInitialVaccinationList().size
        isDoneEnabled = if (flockEntryViewModel.flockUiState.id > 0 &&
            vaccinationViewModel.getInitialVaccinationList().size == vaccinesStateList.size
        )
            vaccinationViewModel.getInitialVaccinationList().all { it.isValid() } &&
                    !vaccinesStateList.zip(vaccinationViewModel.getInitialVaccinationList())
                        .all { it.first == it.second } else
            vaccinationViewModel.getInitialVaccinationList().all { it.isValid() }

        VaccinationInputList(
            modifier = modifier.padding(innerPadding),
            onItemChange = vaccinationViewModel::updateUiState,
            vaccinationViewModel = vaccinationViewModel,
            flockEntryViewModel = flockEntryViewModel,
            isEditingEnabled = isEditingEnabled,
            options = vaccinationViewModel.options
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VaccinationInputList(
    modifier: Modifier,
    onItemChange: (Int, VaccinationUiState) -> Unit,
    vaccinationViewModel: VaccinationViewModel,
    flockEntryViewModel: FlockEntryViewModel,
    isEditingEnabled: Boolean,
    options: MutableList<String>
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(vaccinationViewModel.getInitialVaccinationList()) { index, vaccinationUiState ->
            VaccinationCardEntry(
                modifier = Modifier,
                vaccinationUiState = vaccinationUiState,
                onValueChanged = { uiState ->
                    onItemChange(index, uiState)
//                    isListValid(
//                        vaccinationViewModel.getInitialVaccinationList().all { it.isValid() }
//                    )
                },
                isEditable = isEditingEnabled,
                options = options
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VaccinationCardEntry(
    modifier: Modifier = Modifier,
    vaccinationUiState: VaccinationUiState,
    onValueChanged: (VaccinationUiState) -> Unit,
    isEditable: Boolean,
    options: MutableList<String>
) {
    //var expand by remember { mutableStateOf(vaccinationUiState.isExpanded) }
    OutlinedCard(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation()
    ) {

        Column(
            modifier = modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vaccination #${vaccinationUiState.vaccinationNumber}",
                modifier = modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.headlineSmall
            )
            StatefulDropDownMenu(
                modifier = modifier,
                vaccinationUiState = vaccinationUiState,
                onValueChanged = onValueChanged,
                isEditable = isEditable,
                options = options
            )
            StatefulPickDateDialog(
                modifier = modifier,
                vaccinationUiState = vaccinationUiState,
                onValueChanged = onValueChanged,
                isEditable = isEditable
            )

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = vaccinationUiState.notes,
                textStyle = MaterialTheme.typography.bodySmall,
                onValueChange = { onValueChanged(vaccinationUiState.copy(notes = it)) },
                minLines = 2,
                label = {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                enabled = isEditable,
                colors = TextFieldDefaults.colors(
                    disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha),
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(LocalContentColor.current.alpha)
                )
            )
        }
    }
}

//@RequiresApi(Build.VERSION_CODES.O)
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PickDateDialog(
//    modifier: Modifier = Modifier,
//    showDialog: Boolean,
//    onDismissed: () -> Unit,
//    updateShowDialogOnClick: (Boolean) -> Unit,
//    vaccinationUiState: VaccinationUiState,
//    saveDateSelected: (DatePickerState) -> String,
//    state: DatePickerState,
//    isEditable: Boolean
//) {
//    vaccinationUiState.setDate(saveDateSelected(state))
//    OutlinedTextField(
//        modifier = modifier.fillMaxWidth(),
//        textStyle = MaterialTheme.typography.bodySmall,
//        value = vaccinationUiState.getDate(),
//        onValueChange = { vaccinationUiState.setDate(saveDateSelected(state)) },
//        label = { Text(text = "Vaccination date", style = MaterialTheme.typography.bodySmall) },
//        singleLine = true,
//        readOnly = true,
//        enabled = isEditable,
//        colors = TextFieldDefaults.colors(
//            cursorColor = Color.Unspecified,
//            errorCursorColor = Color.Unspecified,
//            disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha),
//            disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(LocalContentColor.current.alpha)
//        ),
//        isError = vaccinationUiState.isSingleEntryValid(vaccinationUiState.getDate()),
//        interactionSource = remember { MutableInteractionSource() }
//            .also { interactionSource ->
//                LaunchedEffect(interactionSource) {
//                    interactionSource.interactions.collect {
//                        if (it is PressInteraction.Release) {
//                            updateShowDialogOnClick(showDialog)
//                        }
//                    }
//                }
//            }
//    )
//    if (showDialog) {
//        DatePickerDialog(
//            onDismissRequest = onDismissed,
//            confirmButton = {
//                Button(
//                    onClick = onDismissed
//                ) { Text("OK") }
//            },
//            dismissButton = {
//                Button(onClick = onDismissed) {
//                    Text("Cancel")
//                }
//            }
//        ) {
//            DatePicker(
//                state = state,
//                modifier = Modifier,
//                showModeToggle = false,
//            )
//        }
//    }
//}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatefulPickDateDialog(
    modifier: Modifier,
    vaccinationUiState: VaccinationUiState,
    onValueChanged: (VaccinationUiState) -> Unit,
    isEditable: Boolean = true
) {
    val state = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = DateUtils()
            .stringToLocalDate(vaccinationUiState.getDate())
            .atStartOfDay()
            .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )

    var showDialog by remember { mutableStateOf(false) }

    PickerDateDialog(
        label = "Vaccination Date",
        showDialog = showDialog,
        onDismissed = { showDialog = false },
        updateShowDialogOnClick = { showDialog = true },
        date = vaccinationUiState.getDate(),
        datePickerState = state,
        isEditable = isEditable,
        onValueChanged = { onValueChanged(vaccinationUiState.copy(date = it)) },
        saveDateSelected = { dateState ->
            val millisToLocalDate = dateState.selectedDateMillis?.let { millis ->
                DateUtils().convertMillisToLocalDate(
                    millis
                )
            }
            val localDateToString = millisToLocalDate?.let { date ->
                DateUtils().dateToStringLongFormat(
                    date
                )
            }
            localDateToString
        })
}

@Composable
fun StatefulDropDownMenu(
    modifier: Modifier,
    vaccinationUiState: VaccinationUiState,
    onValueChanged: (VaccinationUiState) -> Unit,
    isEditable: Boolean,
    options: MutableList<String>
) {
    var expanded by remember { mutableStateOf(false) }
    val editable by rememberUpdatedState(isEditable)
    var isVaccinationDialogShowing by rememberSaveable { mutableStateOf(false) }
    var newVaccinationEntry by rememberSaveable { mutableStateOf(vaccinationUiState.getName()) }

    var optionSelected by remember { mutableStateOf(vaccinationUiState.getName()) }

    vaccinationUiState.setName(optionSelected)
    Row {
        DropDownMenuDialog(
            modifier = modifier.weight(0.8f),
            value = vaccinationUiState.getName(),
            expanded = vaccinationUiState.isExpanded,
            onExpand = {
                expanded = if (editable) !expanded else false
                //Expand only this item's vaccination name dropdown menu
                onValueChanged(vaccinationUiState.copy(isExpanded = expanded))
            },
            onOptionSelected = {
                optionSelected = it
            },
            onDismissed = {
                expanded = false
                onValueChanged(vaccinationUiState.copy(isExpanded = false))
            },
            isEditable = isEditable,
            options = options,
            label = "Vaccination Name"
        )

        IconButton(
            modifier = Modifier.weight(0.2f),
            enabled = isEditable,
            onClick = { isVaccinationDialogShowing = true }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Vaccination Type",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
    AddNewEntryDialog(
        entry = newVaccinationEntry,
        showDialog = isVaccinationDialogShowing,
        onValueChanged = { newVaccinationEntry = it },
        onDismissed = { isVaccinationDialogShowing = false },
        onSaveEntry = {
            options.add(newVaccinationEntry)
            optionSelected = newVaccinationEntry
            onValueChanged(vaccinationUiState.copy(name = newVaccinationEntry))
            isVaccinationDialogShowing = false
        },
        isEnabled = newVaccinationEntry.isNotBlank(),
        label = "Add Vaccination"
    )

}