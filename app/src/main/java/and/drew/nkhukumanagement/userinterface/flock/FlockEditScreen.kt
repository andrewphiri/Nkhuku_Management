package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockHealth
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.PickerDateDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

object EditFlockDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Edit
    override val route: String
        get() = "edit flock"
    override val resourceId: Int
        get() = R.string.edit_flock
    const val flockIdArg = "id"
    const val healthIdArg = "health"
    val routeWithArgs = "$route/{$flockIdArg}/{$healthIdArg}"
    val arguments = listOf(
        navArgument(flockIdArg) {
            defaultValue = 0
            type = NavType.IntType
        }, navArgument(healthIdArg) {
            defaultValue = 0
            type = NavType.IntType
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockEditScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    editFlockViewModel: EditFlockViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel
) {

    val coroutineScope = rememberCoroutineScope()
    var date by remember { mutableStateOf(DateUtils().dateToStringLongFormat(LocalDate.now())) }
//    val flockWithHealth by editFlockViewModel.flockWithHealth.collectAsState()
    val flockHealth by editFlockViewModel.flockHealth.collectAsState(
        initial = FlockHealth(
            flockUniqueId = "",
            mortality = 0,
            culls = 0,
            date = LocalDate.now()
        )
    )

    val flock by editFlockViewModel.flock.collectAsState(
        initial = flockEntryViewModel.flockUiState.copy(
            datePlaced = DateUtils().dateToStringLongFormat(LocalDate.now()),
            quantity = "0",
            donorFlock = "0"
        ).toFlock()
    )
    val flockUiState: FlockUiState = flock.toFlockUiState()


//    val dateState = if (editFlockViewModel.healthId == 0) rememberDatePickerState(
//        initialDisplayMode = DisplayMode.Picker,
//        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
//            .toEpochMilli()
//    )
//    else rememberDatePickerState(
//        initialDisplayMode = DisplayMode.Picker,
//        initialSelectedDateMillis = flockHealth.date
//            .atStartOfDay()
//            .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
//    )
    flockEntryViewModel.updateUiState(flockUiState.copy(enabled = true))
//    var mortality by rememberSaveable { mutableStateOf(0) }
//    var culls by rememberSaveable { mutableStateOf(0) }

//    var isCullsRemoveButtonEnabled by rememberSaveable { mutableStateOf(false) }
//    var isMortalityRemoveButtonEnabled by rememberSaveable { mutableStateOf(false) }
//    var isCullsAddButtonEnabled by rememberSaveable { mutableStateOf(true) }
//    var isMortalityAddButtonEnabled by rememberSaveable { mutableStateOf(true) }
//    var showDialog by rememberSaveable { mutableStateOf(false) }

//    LaunchedEffect(flockHealth) {
//        if (editFlockViewModel.healthId > 0) {
//            mortality = flockHealth?.mortality ?: 0
//            culls = flockHealth?.culls ?: 0
//            date = flockHealth?.date?.let { DateUtils().dateToStringLongFormat(it) }.toString()
//        }
//    }
//    Log.i("Health_ID", editFlockViewModel.id.toString())
    var quantityRemaining by rememberSaveable {
        mutableStateOf(
            flockEntryViewModel.flockUiState.getStock().toInt()
        )
    }
    //quantityRemaining = flockEntryViewModel.flockUiState.getStock().toInt() - mortality
//    var isUpdateButtonEnabled by rememberSaveable { mutableStateOf(culls > 0 || mortality > 0) }

//    isMortalityAddButtonEnabled = mortality < quantityRemaining
////    mortality?.let { isMortalityAddButtonEnabled = it < quantityRemaining }
//    isMortalityRemoveButtonEnabled = mortality > 0
////    mortality?.let { isMortalityRemoveButtonEnabled = it > 0 }
//    isCullsAddButtonEnabled = culls < quantityRemaining
////    culls?.let { isCullsAddButtonEnabled = it < quantityRemaining }
//    isCullsRemoveButtonEnabled = culls > 0
////    culls?.let { isCullsRemoveButtonEnabled = it > 0 }
//    isUpdateButtonEnabled = flock != flockEntryViewModel.flockUiState.toFlock()

    MainFlockEditScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        flock = flock,
        flockHealth = if (flockHealth != null) flockHealth!! else FlockHealth(
            flockUniqueId = "",
            mortality = 0,
            culls = 0,
            date = LocalDate.now()
        ),
        flockUiState = flockUiState,
        insertHealth = {
            coroutineScope.launch {
                editFlockViewModel.insertHealth(it)
            }
        },
        healthId = editFlockViewModel.healthId,
        onNavigateUp = onNavigateUp,
        quantityRemaining = quantityRemaining,
        onQuantityChanged = {
            quantityRemaining = it
        },
        updateFlock = {
            coroutineScope.launch {
                flockEntryViewModel.updateItem(it)
            }
        },
        updateFlockUiState = flockEntryViewModel::updateUiState,
        updateHealth = {
            coroutineScope.launch {
                editFlockViewModel.updateHealth(it)
            }
        }
    )

//    Scaffold(
//        modifier = modifier,
//        topBar = {
//            FlockManagementTopAppBar(
//                title = stringResource(EditFlockDestination.resourceId),
//                canNavigateBack = canNavigateBack,
//                navigateUp = onNavigateUp
//            )
//        }
//    ) { innerPadding ->
//        isUpdateButtonEnabled =
//            if (editFlockViewModel.healthId == 0) culls > 0 || mortality > 0 else
//                (culls > 0 || mortality > 0) && (mortality != flockHealth.mortality || culls != flockHealth.culls)
//        Column(
//            modifier = Modifier.padding(innerPadding),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Card {
//                Column(
//                    modifier = Modifier.padding(16.dp),
//                    verticalArrangement = Arrangement.spacedBy(16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    PickerDateDialog(
//                        showDialog = showDialog,
//                        onDismissed = { showDialog = false },
//                        label = "Date",
//                        date = date,
//                        updateShowDialogOnClick = { showDialog = true },
//                        onValueChanged = {},
//                        datePickerState = dateState,
//                        saveDateSelected = { dateState ->
//                            val millisToLocalDate = dateState.selectedDateMillis?.let { millis ->
//                                DateUtils().convertMillisToLocalDate(
//                                    millis
//                                )
//                            }
//                            val localDateToString = millisToLocalDate?.let { date ->
//                                DateUtils().dateToStringLongFormat(
//                                    date
//                                )
//                            }
//                            localDateToString
//                        }
//                    )
//                    OutlinedTextField(
//                        modifier = Modifier.fillMaxWidth(),
//                        value = quantityRemaining.toString(),
//                        readOnly = true,
//                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
//                        onValueChange = {
//                            flockEntryViewModel.flockUiState.setStock(
//                                quantityRemaining.toString(),
//                                flockEntryViewModel.flockUiState.donorFlock
//                            )
//                        },
//                        label = {
//                            Text(
//                                modifier = Modifier.fillMaxWidth(),
//                                textAlign = TextAlign.Center,
//                                text = "Stock"
//                            )
//                        },
//                        enabled = true,
//                        singleLine = true,
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                        isError = flockEntryViewModel.flockUiState.isSingleEntryValid(
//                            flockEntryViewModel.flockUiState.quantity
//                        )
//                    )
//
//                    EditCard(
//                        value = mortality.toString(),
//                        label = "Mortality",
//                        onChangedValue = {
//                        },
//                        onReduce = {
//                            mortality -= 1
//                            quantityRemaining =
//                                flockEntryViewModel.flockUiState.getStock().toInt() - mortality
//                            isMortalityRemoveButtonEnabled = mortality > 0
//                            flockEntryViewModel.flockUiState.setStock(
//                                quantityRemaining.toString(),
//                                flockEntryViewModel.flockUiState.donorFlock
//                            )
//                        },
//                        onIncrease = {
//                            mortality += 1
//                            quantityRemaining =
//                                flockEntryViewModel.flockUiState.getStock().toInt() - mortality
//                            flockEntryViewModel.flockUiState.setStock(
//                                quantityRemaining.toString(),
//                                flockEntryViewModel.flockUiState.donorFlock
//                            )
//                            isMortalityAddButtonEnabled = mortality < quantityRemaining
//                        },
//                        isAddButtonEnabled = isMortalityAddButtonEnabled,
//                        isRemoveButtonEnabled = isMortalityRemoveButtonEnabled,
//                        color = if (isMortalityRemoveButtonEnabled) MaterialTheme.colorScheme.primary else
//                            Color.Unspecified
//                    )
//
//                    EditCard(
//                        value = culls.toString(),
//                        label = "Culls",
//                        onChangedValue = {
//                            flockEntryViewModel.flockUiState.setCulls(it)
//                        },
//                        onReduce = {
//                            culls -= 1
//                            isCullsRemoveButtonEnabled = culls > 0
//                        },
//                        onIncrease = {
//                            culls += 1
//                            isCullsAddButtonEnabled = culls < quantityRemaining
//                        },
//                        isAddButtonEnabled = isCullsAddButtonEnabled,
//                        isRemoveButtonEnabled = isCullsRemoveButtonEnabled,
//                        color = if (isCullsRemoveButtonEnabled) MaterialTheme.colorScheme.primary else Color.Unspecified
//                    )
//                }
//            }
//            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                OutlinedButton(
//                    modifier = Modifier.weight(1f),
//                    onClick = onNavigateUp
//                ) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = "Cancel",
//                        textAlign = TextAlign.Center
//                    )
//                }
//                Button(
//                    modifier = Modifier.weight(1f),
//                    enabled = isUpdateButtonEnabled,
//                    onClick = {
//                        if (editFlockViewModel.healthId == 0) {
//                            coroutineScope.launch {
//                                flockEntryViewModel.updateItem(
//                                    flockEntryViewModel.flockUiState.copy(
//                                        mortality =
//                                        (flockUiState.getMortality()
//                                            .toInt() + mortality).toString(),
//                                        culls = (flockUiState.getCulls()
//                                            .toInt() + culls).toString(),
//                                        stock = quantityRemaining.toString()
//                                    )
//                                )
//
//                                editFlockViewModel.insertHealth(
//                                    FlockHealth(
//                                        flockUniqueId = flockUiState.getUniqueId(),
//                                        mortality = mortality,
//                                        culls = culls,
//                                        date = DateUtils().stringToLocalDate(date)
//                                    )
//                                )
//                            }.invokeOnCompletion { onNavigateUp() }
//                        } else {
//                            val mort =
//                                if (flockHealth.mortality < mortality) mortality - flockHealth.mortality else
//                                    flockHealth.mortality - mortality
//                            val cull = if (flockHealth.culls < culls) culls - flockHealth.culls else
//                                flockHealth.culls - culls
//                            coroutineScope.launch {
//
//                                flockEntryViewModel.updateItem(
//                                    flockEntryViewModel.flockUiState.copy(
//                                        mortality =
//                                        (flockUiState.getMortality()
//                                            .toInt() + mort).toString(),
//                                        culls = (flockUiState.getCulls()
//                                            .toInt() + cull).toString(),
//                                        stock = quantityRemaining.toString()
//                                    )
//                                )
//
//                                editFlockViewModel.updateHealth(
//                                    FlockHealth(
//                                        id = flockHealth.id,
//                                        flockUniqueId = flockHealth.flockUniqueId,
//                                        mortality = mortality,
//                                        culls = culls,
//                                        date = DateUtils().stringToLocalDate(date)
//                                    )
//                                )
//                            }.invokeOnCompletion { onNavigateUp() }
//                        }
//                    }
//                ) {
//                    Text(
//                        modifier = Modifier.fillMaxWidth(),
//                        text = "Update",
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//        }
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainFlockEditScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    flockHealth: FlockHealth,
    healthId: Int = 0,
    flock: Flock,
    flockUiState: FlockUiState,
    insertHealth: (FlockHealth) -> Unit,
    updateHealth: (FlockHealth) -> Unit,
    updateFlock: (FlockUiState) -> Unit,
    updateFlockUiState: (FlockUiState) -> Unit,
    quantityRemaining: Int,
    onQuantityChanged: (Int) -> Unit,
) {

    val coroutineScope = rememberCoroutineScope()
    var date by remember { mutableStateOf(DateUtils().dateToStringLongFormat(LocalDate.now())) }
    val dateState = if (healthId == 0) rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )
    else rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = flockHealth.date
            .atStartOfDay()
            .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )
    updateFlockUiState(flockUiState.copy(enabled = true))
    var mortality by rememberSaveable { mutableStateOf(0) }
    var culls by rememberSaveable { mutableStateOf(0) }

    var isCullsRemoveButtonEnabled by rememberSaveable { mutableStateOf(false) }
    var isMortalityRemoveButtonEnabled by rememberSaveable { mutableStateOf(false) }
    var isCullsAddButtonEnabled by rememberSaveable { mutableStateOf(true) }
    var isMortalityAddButtonEnabled by rememberSaveable { mutableStateOf(true) }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(flockHealth) {
        if (healthId > 0) {
            mortality = flockHealth.mortality
            culls = flockHealth.culls
            date = DateUtils().dateToStringLongFormat(flockHealth.date)
        }
    }
    var isUpdateButtonEnabled by rememberSaveable { mutableStateOf(culls > 0 || mortality > 0) }

    isMortalityAddButtonEnabled = mortality < quantityRemaining
    isMortalityRemoveButtonEnabled = mortality > 0
    isCullsAddButtonEnabled = culls < quantityRemaining
    isCullsRemoveButtonEnabled = culls > 0
    isUpdateButtonEnabled = flock != flockUiState.toFlock()

    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(EditFlockDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        isUpdateButtonEnabled =
            if (healthId == 0) culls > 0 || mortality > 0 else
                (culls > 0 || mortality > 0) && (mortality != flockHealth.mortality || culls != flockHealth.culls)
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PickerDateDialog(
                        showDialog = showDialog,
                        onDismissed = { showDialog = false },
                        label = "Date",
                        date = date,
                        updateShowDialogOnClick = { showDialog = true },
                        onValueChanged = {},
                        datePickerState = dateState,
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
                        }
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = quantityRemaining.toString(),
                        readOnly = true,
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        onValueChange = {
                            flockUiState.setStock(
                                quantityRemaining.toString(),
                                flockUiState.donorFlock
                            )
                        },
                        label = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                text = "Stock"
                            )
                        },
                        enabled = true,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = flockUiState.isSingleEntryValid(
                            flockUiState.quantity
                        )
                    )

                    EditCard(
                        value = mortality.toString(),
                        label = "Mortality",
                        onChangedValue = {
                        },
                        onReduce = {
                            mortality -= 1

                            onQuantityChanged(flockUiState.getStock().toInt() - mortality)
                            isMortalityRemoveButtonEnabled = mortality > 0
                            flockUiState.setStock(
                                quantityRemaining.toString(),
                                flockUiState.donorFlock
                            )
                        },
                        onIncrease = {
                            mortality += 1
                            onQuantityChanged(flockUiState.getStock().toInt() - mortality)
                            flockUiState.setStock(
                                quantityRemaining.toString(),
                                flockUiState.donorFlock
                            )
                            isMortalityAddButtonEnabled = mortality < quantityRemaining
                        },
                        isAddButtonEnabled = isMortalityAddButtonEnabled,
                        isRemoveButtonEnabled = isMortalityRemoveButtonEnabled,
                        color = if (isMortalityRemoveButtonEnabled) MaterialTheme.colorScheme.primary else Color.Unspecified
                    )

                    EditCard(
                        value = culls.toString(),
                        label = "Culls",
                        onChangedValue = {
                            flockUiState.setCulls(it)
                        },
                        onReduce = {
                            culls -= 1
                            onQuantityChanged(flockUiState.getStock().toInt() - culls)
                            flockUiState.setStock(
                                quantityRemaining.toString(),
                                flockUiState.donorFlock
                            )
                            isCullsRemoveButtonEnabled = culls > 0
                        },
                        onIncrease = {
                            culls += 1
                            onQuantityChanged(flockUiState.getStock().toInt() - culls)
                            flockUiState.setStock(
                                quantityRemaining.toString(),
                                flockUiState.donorFlock
                            )
                            isCullsAddButtonEnabled = culls < quantityRemaining
                        },
                        isAddButtonEnabled = isCullsAddButtonEnabled,
                        isRemoveButtonEnabled = isCullsRemoveButtonEnabled,
                        color = if (isCullsRemoveButtonEnabled) MaterialTheme.colorScheme.primary else Color.Unspecified
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateUp
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Cancel",
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = isUpdateButtonEnabled,
                    onClick = {
                        if (healthId == 0) {
                            coroutineScope.launch {
                                updateFlock(
                                    flockUiState.copy(
                                        mortality =
                                        (flockUiState.getMortality()
                                            .toInt() + mortality).toString(),
                                        culls = (flockUiState.getCulls()
                                            .toInt() + culls).toString(),
                                        stock = quantityRemaining.toString()
                                    )
                                )

                                insertHealth(
                                    FlockHealth(
                                        flockUniqueId = flockUiState.getUniqueId(),
                                        mortality = mortality,
                                        culls = culls,
                                        date = DateUtils().stringToLocalDate(date)
                                    )
                                )
                            }.invokeOnCompletion { onNavigateUp() }
                        } else {
                            val mort =
                                if (flockHealth.mortality < mortality) mortality - flockHealth.mortality else
                                    flockHealth.mortality - mortality
                            val cull = if (flockHealth.culls < culls) culls - flockHealth.culls else
                                flockHealth.culls - culls
                            coroutineScope.launch {

                                updateFlock(
                                    flockUiState.copy(
                                        mortality =
                                        (flockUiState.getMortality()
                                            .toInt() + mort).toString(),
                                        culls = (flockUiState.getCulls()
                                            .toInt() + cull).toString(),
                                        stock = quantityRemaining.toString()
                                    )
                                )

                                updateHealth(
                                    FlockHealth(
                                        id = flockHealth.id,
                                        flockUniqueId = flockHealth.flockUniqueId,
                                        mortality = mortality,
                                        culls = culls,
                                        date = DateUtils().stringToLocalDate(date)
                                    )
                                )
                            }.invokeOnCompletion { onNavigateUp() }
                        }
                    }
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Update",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun EditCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    onChangedValue: (String) -> Unit,
    onReduce: () -> Unit,
    onIncrease: () -> Unit,
    color: Color,
    isRemoveButtonEnabled: Boolean = true,
    isAddButtonEnabled: Boolean = true,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier.weight(0.5f),
                shape = CircleShape,
                onClick = onReduce,
                border = BorderStroke(1.dp, color = color),
                enabled = isRemoveButtonEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Reduce"
                )
            }
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = value,
                readOnly = true,
                onValueChange = onChangedValue,
                label = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = label
                    )
                },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )

            FilledIconButton(
                modifier = Modifier.weight(0.5f),
                onClick = onIncrease,
                enabled = isAddButtonEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase"
                )
            }
        }

    }
}