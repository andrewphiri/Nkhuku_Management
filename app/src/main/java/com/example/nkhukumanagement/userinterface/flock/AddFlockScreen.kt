package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.utils.DateUtils
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
        Column( modifier = modifier.padding(innerPadding).verticalScroll(scrollState)) {
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
    var selectedOption by rememberSaveable { mutableStateOf(flockUiState.breed) }
    var isBreedDialogShowing by remember { mutableStateOf(false) }
    var newBreedEntry by remember { mutableStateOf("") }

    var showDialog by rememberSaveable { mutableStateOf(false) }

//    flockUiState.setBreed(selectedOption)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp).also { Arrangement.Center }
    ) {
        Row {
            Box(
                modifier = Modifier.weight(0.8f),
                contentAlignment = Alignment.TopCenter
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        readOnly = true,
                        value = flockUiState.breed,
                        onValueChange = {
                            onValueChanged(flockUiState.copy(breed = it))
                            Log.i("Flock Changed", flockUiState.toString())
                        },
                        label = { Text("Breed") },
                        isError = flockUiState.isSingleEntryValid(flockUiState.breed),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors()
                    )

                    ExposedDropdownMenu(
                        modifier = Modifier.exposedDropdownSize(true),
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option) },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                    onValueChanged(flockUiState.copy(breed = selectedOption))
                                }
                            )
                        }
                    }
                }
            }
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
            onSaveBreed = {
                flockUiState.options.add(newBreedEntry)
                onValueChanged(flockUiState.copy(breed = newBreedEntry))
                isBreedDialogShowing = false
            },
            isEnabled = newBreedEntry.isNotBlank()
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

        PickerDialog(
            showDialog = showDialog,
            onDismissed = { showDialog = false },
            label = "Date Received",
            flockUiState = flockUiState,
            updateShowDialogOnClick = { showDialog = true },
            onValueChanged = onValueChanged,
            saveDateSelected = { dateState ->
                val millisToLocalDate = dateState.selectedDateMillis?.let { millis ->
                    DateUtils().convertMillisToLocalDate(
                        millis
                    )
                }
                val localDateToString = millisToLocalDate?.let { date ->
                    DateUtils().convertLocalDateToString(
                        date
                    )
                }
                localDateToString!!
            }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = flockUiState.quantity,
            onValueChange = { onValueChanged(flockUiState.copy(quantity = it)) },
            label = { Text("Number of chicks placed") },
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = flockUiState.isSingleEntryValid(flockUiState.quantity)
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = flockUiState.cost,
            onValueChange = { onValueChanged(flockUiState.copy(cost = it)) },
            label = { Text("Price Per Bird") },
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


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerDialog(
    showDialog: Boolean,
    label: String,
    onDismissed: () -> Unit,
    updateShowDialogOnClick: (Boolean) -> Unit,
    flockUiState: FlockUiState,
    saveDateSelected: (DatePickerState) -> String,
    onValueChanged: (FlockUiState) -> Unit
) {
    val state = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()
            .toEpochMilli()
    )

    flockUiState.setDate(saveDateSelected(state))

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = flockUiState.getDate(),
        onValueChange = { onValueChanged(flockUiState.copy(datePlaced = it)) },
        label = { Text(text = label) },
        singleLine = true,
        readOnly = true,
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Unspecified,
            errorCursorColor = Color.Unspecified
        ),
        isError = flockUiState.isSingleEntryValid(flockUiState.getDate()),
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

@Composable
fun AddNewEntryDialog(
    modifier: Modifier = Modifier,
    entry: String,
    showDialog: Boolean,
    onValueChanged: (String) -> Unit,
    onDismissed: () -> Unit,
    onSaveBreed: () -> Unit,
    isEnabled: Boolean = false,
    label: String = "Add Breed"
) {

    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissed
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    modifier = modifier,
                    value = entry,
                    onValueChange = onValueChanged,
                    label = { Text(label) }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(2.dp, color = MaterialTheme.colorScheme.primary),
                        onClick = onDismissed
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            text = "Cancel",
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = isEnabled,
                        onClick = onSaveBreed
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(8.dp),
                            text = "Save",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddFlockScreenPreview() {
    NkhukuManagementTheme {
//        AddFlockBody()
    }
}