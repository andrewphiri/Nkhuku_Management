package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nkhukumanagement.AppViewModelProviders
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import com.example.nkhukumanagement.utils.DateUtils
import java.time.LocalDateTime
import java.time.ZoneId

object AddVaccinationsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.MedicalServices
    override val route: String
        get() = "Add vaccinations"
    override val resourceId: Int
        get() = R.string.add_vaccinations

}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddVaccinationsScreen(  modifier: Modifier = Modifier,
                            canNavigateBack: Boolean = true,
                           onNavigateUp: () -> Unit,
                            viewModel: VaccinationViewModel = viewModel(factory = AppViewModelProviders.Factory)
){
    Scaffold (
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(AddVaccinationsDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ){ innerPadding ->
        VaccinationInputForm(modifier = modifier.padding(innerPadding), viewModel.vaccinationUiState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VaccinationInputForm(modifier: Modifier, vaccinationUiState: VaccinationUiState) {
    Column(modifier = modifier.fillMaxSize()
    ) {
        SingleVaccinationEntry(vaccinationUiState = vaccinationUiState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SingleVaccinationEntry(modifier: Modifier = Modifier, vaccinationUiState: VaccinationUiState) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatefulPickDateDialog(modifier = modifier.weight(1f), vaccinationUiState = vaccinationUiState)
        StatefulDropDownMenu(modifier = modifier.weight(1f), vaccinationUiState = vaccinationUiState)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickDateDialog(
    showDialog: Boolean,
    onDismissed: () -> Unit,
    updateShowDialogOnClick: (Boolean) -> Unit,
    vaccinationUiState: VaccinationUiState,
    saveDateSelected: (DatePickerState) -> String,
    state: DatePickerState
) {

    OutlinedTextField(
        value = vaccinationUiState.getDate(),
        onValueChange = {vaccinationUiState.setDate(saveDateSelected(state))},
        label = { Text("Vaccination date")},
        singleLine = true,
        readOnly = true,
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Unspecified,
            errorCursorColor = Color.Unspecified
        ),
        isError = vaccinationUiState.isSingleEntryValid(vaccinationUiState.getDate()),
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
fun StatefulPickDateDialog(modifier: Modifier, vaccinationUiState: VaccinationUiState) {
    val state = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker,
        initialSelectedDateMillis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())

    var showDialog by remember { mutableStateOf(false) }

    PickDateDialog(showDialog = showDialog,
        onDismissed = {showDialog = false},
        updateShowDialogOnClick = {showDialog = true},
        vaccinationUiState = vaccinationUiState,
        state = state,
        saveDateSelected = {
                dateState ->
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
            vaccinationUiState.setDate(localDateToString!!)
            localDateToString
        } )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    modifier: Modifier,
    vaccinationUiState: VaccinationUiState,
    expanded: Boolean, onExpand: (Boolean) -> Unit,
    optionSelected: String, onOptionSelected: () -> Unit,
    onDismissed: () -> Unit
) {
    val options = vaccinationUiState.options

        Box(
            contentAlignment = Alignment.TopCenter) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = onExpand
            ) {
                TextField(
                    modifier = Modifier.menuAnchor(),
                    readOnly = true,
                    value = vaccinationUiState.getName(),
                    onValueChange = {
                        vaccinationUiState.setName(optionSelected)
                    },
                    label = { Text("Vaccination name") },
                    isError = vaccinationUiState.isSingleEntryValid(vaccinationUiState.getName()),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )

                ExposedDropdownMenu(
                    modifier = Modifier.exposedDropdownSize(true),
                    expanded = expanded,
                    onDismissRequest = onDismissed
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option) },
                            onClick = onOptionSelected
                        )
                    }
                }
            }
        }

}

@Composable
fun StatefulDropDownMenu(modifier: Modifier, vaccinationUiState: VaccinationUiState) {
    var expanded by remember { mutableStateOf(false) }
    var optionSelected by remember { mutableStateOf(vaccinationUiState.getName()) }
    vaccinationUiState.setName(optionSelected)

    DropDownMenu(
        modifier = modifier, vaccinationUiState = vaccinationUiState,
        expanded = expanded, onExpand = { expanded = !expanded},
        optionSelected = optionSelected, onOptionSelected = {vaccinationUiState.setName(optionSelected)},
        onDismissed = {expanded = false}
    )
}