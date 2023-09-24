package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import com.example.nkhukumanagement.utils.AddNewEntryDialog
import com.example.nkhukumanagement.utils.DateUtils
import com.example.nkhukumanagement.utils.DropDownMenuDialog
import com.example.nkhukumanagement.utils.PickerDateDialog
import com.example.nkhukumanagement.utils.currencySymbol
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

object AddFlockDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Add
    override val route: String
        get() = "Add Flock"
    override val resourceId: Int
        get() = R.string.add_flock
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddFlockScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    navigateToVaccinationsScreen: (FlockUiState) -> Unit,
    viewModel: FlockEntryViewModel
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(AddFlockDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = { onNavigateUp() }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding).verticalScroll(scrollState)) {
            AddFlockBody(
                flockUiState = viewModel.flockUiState,
                onItemValueChange = viewModel::updateUiState,
                onVaccinationsScreen = {
                    if (checkNumberExceptions(viewModel.flockUiState)) {
                        viewModel.flockUiState.setStock(it.quantity, it.donorFlock)
                        navigateToVaccinationsScreen(viewModel.flockUiState)
                    } else {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(message = "Please enter a valid number.")
                        }
                    }
                })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddFlockBody(
    flockUiState: FlockUiState,
    onItemValueChange: (FlockUiState) -> Unit,
    modifier: Modifier = Modifier, onVaccinationsScreen: (FlockUiState) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
    ) {
        AddFlockInputForm(
            flockUiState = flockUiState, modifier = modifier,
            onValueChanged = onItemValueChange
        )
        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            onClick = { onVaccinationsScreen(flockUiState) },
            enabled = flockUiState.enabled
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                textAlign = TextAlign.Center,
                text = "Set vaccination days",
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AddFlockInputForm(
    flockUiState: FlockUiState,
    modifier: Modifier = Modifier,
    onValueChanged: (FlockUiState) -> Unit = {}
) {

    val options = flockUiState.options
    var expanded by rememberSaveable { mutableStateOf(false) }
    var isBreedDialogShowing by remember { mutableStateOf(false) }
    var newBreedEntry by remember { mutableStateOf("") }

    val dateState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )

    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp).also { Arrangement.Center }
    ) {
        Row {
            DropDownMenuDialog(
                modifier = modifier.weight(0.8f),
                value = flockUiState.breed,
                expanded = expanded,
                onExpand = {
                    expanded = !expanded
                },
                onOptionSelected = {
                    onValueChanged(flockUiState.copy(breed = it))
                },
                onDismissed = {
                    expanded = false
                },
                options = options,
                label = "Breed"
            )
            IconButton(
                modifier = Modifier.weight(0.2f),
                onClick = { isBreedDialogShowing = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add breed",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }

        AddNewEntryDialog(
            entry = newBreedEntry,
            showDialog = isBreedDialogShowing,
            onValueChanged = { newBreedEntry = it },
            onDismissed = { isBreedDialogShowing = false },
            onSaveEntry = {
                flockUiState.options.add(newBreedEntry)
                onValueChanged(flockUiState.copy(breed = newBreedEntry))
                isBreedDialogShowing = false
            },
            isEnabled = newBreedEntry.isNotBlank(),
            label = "Add Breed"
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = flockUiState.batchName,
            onValueChange = { onValueChanged(flockUiState.copy(batchName = it)) },
            label = { Text("Batch name") },
            enabled = true,
            singleLine = true,
            isError = flockUiState.isSingleEntryValid(flockUiState.batchName)
        )

        PickerDateDialog(
            showDialog = showDialog,
            onDismissed = { showDialog = false },
            label = "Date Received",
            date = flockUiState.getDate(),
            updateShowDialogOnClick = { showDialog = true },
            onValueChanged = { onValueChanged(flockUiState.copy(datePlaced = it)) },
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
            value = flockUiState.quantity,
            onValueChange = { onValueChanged(flockUiState.copy(quantity = it)) },
            label = { Text("Number of chicks placed") },
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            isError = flockUiState.isSingleEntryValid(flockUiState.quantity)
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = flockUiState.cost,
            onValueChange = { onValueChanged(flockUiState.copy(cost = it)) },
            label = { Text("Price Per Bird") },
            prefix = {
                currencySymbol()?.let {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = it
                    )
                }
            },
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = flockUiState.isSingleEntryValid(flockUiState.cost)
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = flockUiState.donorFlock,
            onValueChange = { onValueChanged(flockUiState.copy(donorFlock = it)) },
            label = { Text("Donor flock") },
            isError = flockUiState.isSingleEntryValid(flockUiState.donorFlock),
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

    }

}


//@RequiresApi(Build.VERSION_CODES.O)
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PickerDialog(
//    showDialog: Boolean,
//    label: String,
//    onDismissed: () -> Unit,
//    updateShowDialogOnClick: (Boolean) -> Unit,
//    flockUiState: FlockUiState,
//    saveDateSelected: (DatePickerState) -> String,
//    onValueChanged: (FlockUiState) -> Unit
//) {
//    val state = rememberDatePickerState(
//        initialDisplayMode = DisplayMode.Picker,
//        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
//            .toEpochMilli()
//    )
//
//    flockUiState.setDate(saveDateSelected(state))
//
//    OutlinedTextField(
//        modifier = Modifier
//            .fillMaxWidth(),
//        value = flockUiState.getDate(),
//        onValueChange = { onValueChanged(flockUiState.copy(datePlaced = it)) },
//        label = { Text(text = label) },
//        singleLine = true,
//        readOnly = true,
//        colors = TextFieldDefaults.colors(
//            cursorColor = Color.Unspecified,
//            errorCursorColor = Color.Unspecified
//        ),
//        isError = flockUiState.isSingleEntryValid(flockUiState.getDate()),
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


@Preview(showBackground = true)
@Composable
private fun AddFlockScreenPreview() {
    NkhukuManagementTheme {
//        AddFlockBody()
    }
}