package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.Eggs
import and.drew.nkhukumanagement.data.EggsSummary
import and.drew.nkhukumanagement.data.FlockAndEggsSummary
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.PickerDateDialog
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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

object EditEggsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Edit
    override val route: String
        get() = "edit eggs"
    override val resourceId: Int
        get() = R.string.edit_flock_eggs
    const val flockArg = "arg_flockId"
    const val eggsIdArg = "eggsIdArg"
    val routeWithArgs = "$route/{$flockArg}/{$eggsIdArg}"
    val arguments = listOf(
        navArgument(flockArg) {
            defaultValue = 0
            type = NavType.IntType
        }, navArgument(eggsIdArg) {
            defaultValue = 0
            type = NavType.IntType
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EggsEditScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    eggsInventoryViewModel: EggsInventoryViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel,
    contentType: ContentType
) {
    val coroutineScope = rememberCoroutineScope()
    val eggItem by eggsInventoryViewModel.egg.collectAsState(
        initial = Eggs(
            flockUniqueId = "",
            goodEggs = 0,
            badEggs = 0,
            date = LocalDate.now()
        )
    )
    val flock by eggsInventoryViewModel.flock.collectAsState(
        FlockUiState(
            datePlaced = DateUtils().dateToStringLongFormat(LocalDate.now()),
            quantity = "0",
            donorFlock = "0",
            cost = "0"
        ).toFlock()
    )


    val flockAndEggsSummary by eggsInventoryViewModel
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

    val flockUiState: FlockUiState? = flock?.toFlockUiState()
    val eggId by eggsInventoryViewModel.eggsID.collectAsState(initial = 0)

    LaunchedEffect(key1 = flock) {
        flockUiState?.copy(enabled = true)?.let { flockEntryViewModel.updateUiState(it) }
    }


    MainEditEggsScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        eggs = if (eggItem != null) eggItem!! else Eggs(
            flockUniqueId = "",
            goodEggs = 0,
            badEggs = 0,
            date = LocalDate.now()
        ),
        flockUiState = flockUiState ?: FlockUiState(),
        insertEggs = { eggs ->
            coroutineScope.launch {
                eggsInventoryViewModel.insertEgg(eggs)
            }
        },
        updateEggsSummary = { eggSummary ->
            coroutineScope.launch {
                eggsInventoryViewModel.updateEggsSummary(eggSummary)
            }
        },
        eggId = eggId,
        onNavigateUp = onNavigateUp,
        updateEggs = {
            coroutineScope.launch {
                eggsInventoryViewModel.updateEggs(it)
            }
        },
        contentType = contentType,
        eggsSummary = flockAndEggsSummary?.eggsSummary ?: EggsSummary(
            flockUniqueID = "",
            totalGoodEggs = 0,
            totalBadEggs = 0,
            date = LocalDate.now()
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainEditEggsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    eggs: Eggs,
    eggId: Int = 0,
    eggsSummary: EggsSummary,
    flockUiState: FlockUiState,
    updateEggsSummary: (EggsSummary) -> Unit,
    insertEggs: (Eggs) -> Unit,
    updateEggs: (Eggs) -> Unit,
    contentType: ContentType
) {
    BackHandler {
        onNavigateUp()
    }
    var date by remember { mutableStateOf(DateUtils().dateToStringLongFormat(LocalDate.now())) }
    val dateState = if (eggId == 0) rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )
    else rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = eggs.date
            .atStartOfDay()
            .atZone(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )
    var goodEggs by rememberSaveable { mutableStateOf(0) }
    var badEggs by rememberSaveable { mutableStateOf(0) }

    var totalGoodEggs by rememberSaveable { mutableStateOf(eggsSummary.totalGoodEggs) }
    var totalBadEggs by rememberSaveable { mutableStateOf(eggsSummary.totalBadEggs ) }

    var isBadEggsRemoveButtonEnabled by rememberSaveable { mutableStateOf(false) }
    var isGoodEggsRemoveButtonEnabled by rememberSaveable { mutableStateOf(false) }
    var isBadEggsAddButtonEnabled by rememberSaveable { mutableStateOf(true) }
    var isGoodEggsAddButtonEnabled by rememberSaveable { mutableStateOf(true) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isUpdateButtonEnabled by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(eggs) {
        if (eggId > 0) {
            goodEggs = eggs.goodEggs
            badEggs = eggs.badEggs
            date = DateUtils().dateToStringLongFormat(eggs.date)
        }
    }

    isGoodEggsAddButtonEnabled = goodEggs <= totalGoodEggs
    isGoodEggsRemoveButtonEnabled = goodEggs > 0
    isBadEggsAddButtonEnabled = badEggs <= totalBadEggs
    isBadEggsRemoveButtonEnabled = badEggs > 0
    //isUpdateButtonEnabled = flock != flockUiState.toFlock()
    isUpdateButtonEnabled =
        (badEggs > 0 || goodEggs > 0) && (goodEggs != eggs.goodEggs || badEggs != eggs.badEggs)
    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = flockUiState.batchName,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                contentType = contentType
            )
        }
    ) { innerPadding ->

        LazyColumn(
            userScrollEnabled = true
        ) {
            item {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp),
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
                                label = stringResource(R.string.date),
                                date = date,
                                updateShowDialogOnClick = { showDialog = true },
                                onValueChanged = {
                                    date = it
                                },
                                datePickerState = dateState,
                                saveDateSelected = { dateState ->
                                    val millisToLocalDate =
                                        dateState.selectedDateMillis?.let { millis ->
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
                                value = totalGoodEggs.toString(),
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                onValueChange = {

                                },
                                label = {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.total_good_eggs)
                                    )
                                },
                                enabled = true,
                                readOnly = true,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            )

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = totalBadEggs.toString(),
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                                onValueChange = {

                                },
                                label = {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = stringResource(R.string.total_bad_eggs)
                                    )
                                },
                                readOnly = true,
                                enabled = true,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            )

                            EditEggCard(
                                descriptionForIncrease = "Increase good eggs",
                                descriptionForDecrease = "Decrease good eggs",
                                value = if (goodEggs == 0) "" else goodEggs.toString(),
                                label = stringResource(R.string.good_eggs),
                                onChangedValue = {
                                    // updateFlockUiState(flockUiState.copy(mortality = it))
                                    try {
                                        goodEggs = it.toIntOrNull() ?: 0
                                        totalGoodEggs = eggsSummary.totalGoodEggs + goodEggs
                                    } catch (e: NumberFormatException) {
                                        e.printStackTrace()
                                    }

                                },
                                onReduce = {
                                    goodEggs -= 1
                                    totalGoodEggs -=1
                                    isGoodEggsRemoveButtonEnabled = goodEggs > 0

                                },
                                onIncrease = {
                                    goodEggs += 1
                                    totalGoodEggs +=1
                                    isGoodEggsAddButtonEnabled = goodEggs < totalGoodEggs
                                },
                                isAddButtonEnabled = isGoodEggsAddButtonEnabled,
                                isRemoveButtonEnabled = isGoodEggsRemoveButtonEnabled,
                                color = if (isGoodEggsRemoveButtonEnabled) MaterialTheme.colorScheme.primary else Color.Unspecified
                            )

                            EditEggCard(
                                descriptionForDecrease = "Decrease bad eggs",
                                descriptionForIncrease = "Increase bad eggs",
                                value = if (badEggs == 0) "" else badEggs.toString(),
                                label = stringResource(R.string.bad_eggs),
                                onChangedValue = {
                                    try {
                                        badEggs = it.toIntOrNull() ?: 0
                                        totalBadEggs = eggsSummary.totalBadEggs + badEggs
                                    } catch (e: NumberFormatException) {
                                        e.printStackTrace()
                                    }
                                },
                                onReduce = {
                                    badEggs -= 1
                                    totalBadEggs -= 1
                                    isBadEggsRemoveButtonEnabled = badEggs > 0
                                },
                                onIncrease = {
                                    badEggs += 1
                                    totalBadEggs += 1
                                    isBadEggsAddButtonEnabled = badEggs < totalBadEggs
                                },
                                isAddButtonEnabled = isBadEggsAddButtonEnabled,
                                isRemoveButtonEnabled = isBadEggsRemoveButtonEnabled,
                                color = if (isBadEggsRemoveButtonEnabled) MaterialTheme.colorScheme.primary else Color.Unspecified
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f, true),
                            onClick = onNavigateUp
                        ) {
                            Text(
                                text = stringResource(R.string.cancel),
                                textAlign = TextAlign.Center
                            )
                        }
                        Button(
                            modifier = Modifier
                                .semantics { contentDescription = "Update Button" }
                                .weight(1f, true),
                            enabled = isUpdateButtonEnabled,
                            onClick = {
                                if (eggId == 0) {

                                    updateEggsSummary(
                                        EggsSummary(
                                            flockUniqueID = flockUiState.getUniqueId(),
                                            totalGoodEggs = totalGoodEggs,
                                            totalBadEggs = totalBadEggs,
                                            date = DateUtils().stringToLocalDate(date)
                                        )
                                    )
                                    insertEggs(
                                        Eggs(
                                            flockUniqueId = flockUiState.getUniqueId(),
                                            goodEggs = goodEggs,
                                            badEggs = badEggs,
                                            date = DateUtils().stringToLocalDate(date)
                                        )
                                    )
                                    onNavigateUp()
                                } else {
                                    val gdEggs =
                                        if (eggs.goodEggs < goodEggs) goodEggs - eggs.goodEggs else
                                            eggs.goodEggs - goodEggs
                                    val bdEggs =
                                        if (eggs.badEggs < badEggs) badEggs - eggs.badEggs else
                                            eggs.badEggs - badEggs

                                    updateEggsSummary(
                                        EggsSummary(
                                            flockUniqueID = flockUiState.getUniqueId(),
                                            totalGoodEggs = eggsSummary.totalGoodEggs + gdEggs,
                                            totalBadEggs = eggsSummary.totalBadEggs + bdEggs,
                                            date = DateUtils().stringToLocalDate(date)
                                        )
                                    )

                                    updateEggs(
                                        Eggs(
                                            id = eggs.id,
                                            flockUniqueId = eggs.flockUniqueId,
                                            goodEggs = goodEggs,
                                            badEggs = badEggs,
                                            date = DateUtils().stringToLocalDate(date)
                                        )
                                    )

                                }
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.update),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun EditEggCard(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    onChangedValue: (String) -> Unit,
    onReduce: () -> Unit,
    onIncrease: () -> Unit,
    color: Color,
    isRemoveButtonEnabled: Boolean = true,
    isAddButtonEnabled: Boolean = true,
    descriptionForIncrease: String,
    descriptionForDecrease: String
) {
    Column(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier
                    .semantics { contentDescription = descriptionForDecrease }
                    .weight(0.5f),
                shape = CircleShape,
                onClick = onReduce,
                border = BorderStroke(1.dp, color = color),
                enabled = isRemoveButtonEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Reduction"
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f),
                value = value,
                onValueChange = onChangedValue,
                label = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = label
                    )
                },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            FilledIconButton(
                modifier = Modifier
                    .semantics { contentDescription = descriptionForIncrease }
                    .weight(0.5f),
                onClick = onIncrease,
                enabled = isAddButtonEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Addition"
                )
            }
        }

    }
}