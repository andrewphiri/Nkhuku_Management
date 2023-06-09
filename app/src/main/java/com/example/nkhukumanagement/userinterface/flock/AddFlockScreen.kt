package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

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

    Scaffold(
        topBar = {
                 FlockManagementTopAppBar(
                     title = stringResource(AddFlockDestination.resourceId),
                     canNavigateBack = canNavigateBack,
                     navigateUp = onNavigateUp
                 )
        } ,
    ) { innerPadding ->
        AddFlockBody(
            flockUiState = viewModel.flockUiState,
            modifier = modifier.padding(innerPadding),
            onItemValueChange = viewModel::updateUiState,
            onVaccinationsScreen = {
                navigateToVaccinationsScreen(viewModel.flockUiState)} )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddFlockBody(
    flockUiState: FlockUiState,
    onItemValueChange: (FlockUiState) -> Unit,
    modifier: Modifier = Modifier, onVaccinationsScreen: (FlockUiState) -> Unit
) {
    Column (
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
            ) {
        AddFlockInputForm(flockUiState = flockUiState, modifier = modifier,
            onValueChanged = onItemValueChange)
        Button(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            onClick = {onVaccinationsScreen(flockUiState)},
            enabled = flockUiState.enabled
        ) {
            Text("Set vaccination days")
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

    var showDialog by rememberSaveable { mutableStateOf(false) }

//    flockUiState.setBreed(selectedOption)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp).also { Arrangement.Center }
    ){
        Box(contentAlignment = Alignment.TopCenter) {
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

        PickerDialog(
            showDialog = showDialog,
            onDismissed = {showDialog = false},
            flockUiState = flockUiState,
            updateShowDialogOnClick = {showDialog = true},
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
            label = { Text("Number of chicks placed")},
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = flockUiState.isSingleEntryValid(flockUiState.quantity)
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = flockUiState.donorFlock,
            onValueChange = { onValueChanged(flockUiState.copy(donorFlock = it)) },
            label = { Text("Donor flock")},
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
    onDismissed: () -> Unit,
    updateShowDialogOnClick: (Boolean) -> Unit,
    flockUiState: FlockUiState,
    saveDateSelected: (DatePickerState) -> String,
    onValueChanged: (FlockUiState) -> Unit
    ) {
    val state = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker,
    initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())

    flockUiState.setDate(saveDateSelected(state))

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = flockUiState.getDate(),
        onValueChange = {onValueChanged(flockUiState.copy(datePlaced = it))},
        label = { Text("Date Received")},
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
                ){ Text("OK") }
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatefulPickerDialog(modifier: Modifier = Modifier) {
    val state = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())



}
@Preview(showBackground = true)
@Composable
private fun AddFlockScreenPreview() {
    NkhukuManagementTheme {
//        AddFlockBody()
    }
}