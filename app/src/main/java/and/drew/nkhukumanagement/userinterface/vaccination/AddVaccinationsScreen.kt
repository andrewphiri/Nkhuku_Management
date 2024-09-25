package and.drew.nkhukumanagement.userinterface.vaccination

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.data.EggsSummary
import and.drew.nkhukumanagement.data.FlockWithVaccinations
import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.accounts.AccountsViewModel
import and.drew.nkhukumanagement.userinterface.feed.FeedViewModel
import and.drew.nkhukumanagement.userinterface.flock.EggsInventoryViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockUiState
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.userinterface.weight.WeightViewModel
import and.drew.nkhukumanagement.utils.AddNewEntryDialog
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.DropDownMenuAutoCompleteDialog
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
    eggsInventoryViewModel: EggsInventoryViewModel = hiltViewModel(),
    userPrefsViewModel: UserPrefsViewModel,
    contentType: ContentType
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val flockTypeOptions = context.resources.getStringArray(R.array.types_of_flocks)
    val getAllVaccinationItems by vaccinationViewModel.getAllVaccinationItems.collectAsState()
    val flockWithVaccinations by detailsViewModel
        .flockWithVaccinationsStateFlow
        .collectAsState(
            initial = FlockWithVaccinations(flock = null, vaccinations = listOf())
        )
    val withVaccinationsToEdit by vaccinationViewModel.flockWithVaccinationsStateFlow.collectAsState(
        initial = FlockWithVaccinations(flock = null, vaccinations = listOf())
    )
    var vaccinesList: List<Vaccination> = listOf()
    val vaccinesStateList: MutableList<VaccinationUiState> = mutableListOf()
    val flockList by detailsViewModel.allFlocks.collectAsState()
    val userPreferences by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )
    var vaccineID by rememberSaveable { mutableStateOf(0) }
    var flockID by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(key1 = getAllVaccinationItems, key2 = flockList) {
        if (getAllVaccinationItems.isNotEmpty()) {
            vaccineID = getAllVaccinationItems.maxOf {
                it.id
            }
        }

        if (flockList.flockList.isNotEmpty()) {
            flockID = flockList.flockList.maxOf { it.id }
        }
    }

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
        modifier = Modifier
            .semantics { contentDescription = "Vaccination Screen" },
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            FlockManagementTopAppBar(
                title = title,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                isRemoveShowing = isRemoveShowing,
                isAddShowing = isAddShowing,
                isDoneShowing = isDoneButtonShowing,
                isDoneEnabled = isDoneEnabled,
                contentType = contentType,
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
                            weightViewModel.defaultWeights(
                                flockEntryViewModel.flockUiState
                            )
                        )
                        feedViewModel.setFeedList(
                            feedViewModel.defaultFeedInformationList(
                                flockEntryViewModel.flockUiState
                            )
                        )
                        if (userPreferences.receiveNotifications) {
                            for (uiState in vaccinationViewModel.getInitialVaccinationList()) {
                                vaccineID += 1
                                flockID += 1

                                vaccinationViewModel.schedule(
                                    uiState.copy(flockUniqueId = flockEntryViewModel.flockUiState.getUniqueId())
                                        .toVaccination(),
                                    flockEntryViewModel.flockUiState.copy(id = flockID),
                                    notificationID = vaccineID
                                )
//                                Log.i("UISTATE__", uiState.toString())
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

                            if (flockEntryViewModel.flockUiState.flockType == flockTypeOptions[1]) {
                                eggsInventoryViewModel.insertEggSummary(
                                    EggsSummary(
                                        flockUniqueID = flockEntryViewModel.flockUiState.getUniqueId(),
                                        totalGoodEggs = 0,
                                        totalBadEggs = 0,
                                        date = DateUtils().stringToLocalDate(flockEntryViewModel.flockUiState.getDate())
                                    )
                                )
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
                            vaccinationViewModel.cancelNotification(it)
                        }

                        if (userPreferences.receiveNotifications) {
                            for (uiState in vaccinationViewModel.getInitialVaccinationList()) {
                                vaccinationViewModel.schedule(
                                    vaccination = uiState.toVaccination(),
                                    flock = flockEntryViewModel.flockUiState
                                )
//                                Log.i("UISTATE", uiState.toString())
                            }
                        }

                        coroutineScope.launch {
                            vaccinationViewModel.deleteVaccination(flockUniqueID)

                            vaccinationViewModel.getInitialVaccinationList().forEach {
                                vaccinationViewModel.saveVaccination(it.copy(flockUniqueId = flockUniqueID))
                            }

                        }.invokeOnCompletion {
                            onNavigateUp()
                        }
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
                        contentDescription = context.resources.getString(R.string.edit_vaccinations),
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
            isEditingEnabled = isEditingEnabled,
            options = vaccinationViewModel.options,
            vaccinationList = vaccinationViewModel.getInitialVaccinationList()
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainAddVaccinationsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    flockId: Int,
    vaccinationList: MutableList<VaccinationUiState>,
    setInitialVaccinationDate: (SnapshotStateList<VaccinationUiState>) -> Unit,
    flockWithVaccinations: FlockWithVaccinations?,
    flockUiState: FlockUiState,
    vaccinationUiState: VaccinationUiState,
    vaccinesList: List<Vaccination>,
    vaccinesStateList: MutableList<VaccinationUiState>,
    defaultVaccinationDates: (FlockUiState, VaccinationUiState) -> SnapshotStateList<VaccinationUiState>,
    onSaveToDatabase: () -> Unit,
    onItemChange: (Int, VaccinationUiState) -> Unit,
    options: MutableList<String>,
    contentType: ContentType
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    title = stringResource(AddVaccinationsDestination.resourceId)
    var isEditingEnabled by rememberSaveable { mutableStateOf(false) }
    var isFABVisible by remember { mutableStateOf(false) }
    var isDoneButtonShowing by remember { mutableStateOf(true) }
    var isAddShowing by remember { mutableStateOf(true) }
    val vaccinationDates = vaccinationList
    var listSize by remember { mutableStateOf(vaccinationList.size) }
    var isRemoveShowing by remember { mutableStateOf(true) }
    var isDoneEnabled by rememberSaveable { mutableStateOf(true) }


    if (flockId == 0) {
        setInitialVaccinationDate(
            defaultVaccinationDates(
                flockUiState, vaccinationUiState
            )
        )
        isEditingEnabled = true
    } else {
        title = stringResource(R.string.vaccinations)
        isFABVisible = true
        isDoneButtonShowing = false
        isAddShowing = false
        isRemoveShowing = false
        //vaccinesList = flockWithVaccinations?.vaccinations ?: listOf()
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

        setInitialVaccinationDate(vaccinesStateList.toMutableStateList())
    }

    Scaffold(
        modifier = Modifier
            .semantics { contentDescription = "Vaccination Screen" },
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
                    vaccinationList.removeAt(
                        listSize
                    )

                },
                onClickAdd = {
                    listSize++
                    isRemoveShowing = true
                    //isDoneEnabled = vaccinationDates.all { it.isValid() }
                    val newDate = DateUtils().stringToLocalDate(
                        vaccinationList.last().getDate()
                    )
                    val vaccineDate =
                        DateUtils().dateToStringLongFormat(DateUtils().calculateDate(newDate, 7))
                    vaccinationList.add(
                        vaccinationDates.last().copy(
                            vaccinationNumber = listSize, name = "", date = vaccineDate
                        )
                    )
                },
                onSaveToDatabase = onSaveToDatabase,
                contentType = contentType
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

                        vaccinationList.onEach {
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
                        contentDescription = context.resources.getString(R.string.edit_vaccinations),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        listSize = vaccinationList.size
        isDoneEnabled = if (flockUiState.id > 0 &&
            vaccinationList.size == vaccinesStateList.size
        )
            vaccinationList.all { it.isValid() } &&
                    !vaccinesStateList.zip(vaccinationList)
                        .all { it.first == it.second } else
            vaccinationList.all { it.isValid() }

        VaccinationInputList(
            modifier = modifier.padding(innerPadding),
            onItemChange = onItemChange,
            vaccinationList = vaccinationList,
            isEditingEnabled = isEditingEnabled,
            options = options
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VaccinationInputList(
    modifier: Modifier,
    onItemChange: (Int, VaccinationUiState) -> Unit,
    vaccinationList: MutableList<VaccinationUiState>,
    isEditingEnabled: Boolean,
    options: MutableList<String>
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(vaccinationList) { index, vaccinationUiState ->
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
    var expanded by remember { mutableStateOf(false) }
    var selectedMethodOption by remember { mutableStateOf(vaccinationUiState.method) }
    OutlinedCard(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Checkbox(
                modifier = Modifier.align(Alignment.TopEnd),
                checked = vaccinationUiState.vaccineAdministered,
                onCheckedChange = {
                    onValueChanged(vaccinationUiState.copy(vaccineAdministered = it))
                },
                enabled = isEditable
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${stringResource(R.string.vaccination)} #${vaccinationUiState.vaccinationNumber}",
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

                DropDownMenuAutoCompleteDialog(
                    modifier = Modifier.semantics { contentDescription = "method" },
                    value = selectedMethodOption,
                    expanded = expanded,
                    onExpand = {
                        expanded = !expanded
                    },
                    onOptionSelected = {
                        selectedMethodOption = it
                        onValueChanged(vaccinationUiState.copy(method = it))
                    },
                    onDismissed = {
                        expanded = false
                    },
                    options = vaccinationUiState.methodsVaccineAdministration,
                    label = stringResource(R.string.method)
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
                            text = stringResource(R.string.notes),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    enabled = isEditable,
                    colors = TextFieldDefaults.colors(
                        disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha),
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(
                            LocalContentColor.current.alpha
                        )
                    )
                )
            }
        }
    }
}


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
        label = stringResource(R.string.vaccination_date),
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
                    date.toLocalDate()
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
        DropDownMenuAutoCompleteDialog(
            modifier = modifier,
            value = optionSelected,
            expanded = vaccinationUiState.isExpanded,
            onExpand = {
                expanded = if (editable) !expanded else false
                //Expand only this item's vaccination name dropdown menu
                onValueChanged(vaccinationUiState.copy(isExpanded = expanded))
            },
            onOptionSelected = {
                optionSelected = it
                onValueChanged(vaccinationUiState.copy(name = optionSelected))
            },
            onDismissed = {
                expanded = false
                onValueChanged(vaccinationUiState.copy(isExpanded = false))
            },
            isEditable = isEditable,
            options = options,
            label = stringResource(R.string.vaccination_name)
        )

//        IconButton(
//            modifier = Modifier.weight(0.2f),
//            enabled = isEditable,
//            onClick = { isVaccinationDialogShowing = true }
//        ) {
//            Icon(
//                imageVector = Icons.Default.Add,
//                contentDescription = "Add Vaccination Type",
//                tint = MaterialTheme.colorScheme.secondary
//            )
//        }
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
        label = stringResource(R.string.add_vaccination)
    )

}