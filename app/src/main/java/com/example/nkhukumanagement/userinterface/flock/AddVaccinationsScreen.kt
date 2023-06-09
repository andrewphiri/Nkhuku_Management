package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import com.example.nkhukumanagement.utils.DateUtils
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.String


object AddVaccinationsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.MedicalServices
    override val route: String
        get() = "Add vaccinations"
    override val resourceId: Int
        get() = R.string.add_vaccinations
    const val flockBreedArg = "breed"
    const val dateReceivedArg = "date_received"
    val routeWithArgs = "${route}/{{$flockBreedArg}&{$dateReceivedArg}}"
    val argument = listOf(
        navArgument(flockBreedArg) {type = NavType.StringType},
        navArgument(dateReceivedArg) {type = NavType.StringType}
    )

}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddVaccinationsScreen(modifier: Modifier = Modifier,
                          canNavigateBack: Boolean = true,
                          onNavigateUp: () -> Unit,
                          vaccinationViewModel: VaccinationViewModel ,
                          flockEntryViewModel: FlockEntryViewModel,
){
    val dateReceived = DateUtils().stringToLocalDate(flockEntryViewModel.flockUiState.getDate())
    vaccinationViewModel.setInitialDates(flockEntryViewModel)
    Scaffold (
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(AddVaccinationsDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ){ innerPadding ->
            VaccinationInputList(
                modifier = modifier.padding(innerPadding),
                onItemChange = vaccinationViewModel::updateUiState,
                vaccinationViewModel = vaccinationViewModel
            )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VaccinationInputList(
    modifier: Modifier,
    onItemChange: (Int,VaccinationUiState) -> Unit,
    vaccinationViewModel: VaccinationViewModel
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(vaccinationViewModel.getInitialVaccinationList()) { index, vaccinationUiState ->
            VaccinationCardEntry(vaccinationUiState = vaccinationUiState,
                onValueChanged = {onItemChange(index, it )}
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VaccinationCardEntry(
    modifier: Modifier = Modifier,
    vaccinationUiState: VaccinationUiState,
    onValueChanged: (VaccinationUiState) -> Unit) {
    OutlinedCard(
        modifier = modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(
            modifier = modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Vaccination #${vaccinationUiState.vaccinationNumber}",
            modifier = modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.headlineSmall
            )
            StatefulDropDownMenu(modifier = modifier, vaccinationUiState = vaccinationUiState)
            StatefulPickDateDialog(modifier = modifier, vaccinationUiState = vaccinationUiState)

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = vaccinationUiState.notes,
                textStyle = MaterialTheme.typography.bodySmall,
                onValueChange = { onValueChanged(vaccinationUiState.copy(notes = it))
                                },
                minLines = 2,
                label = { Text(text = "Notes",
                style = MaterialTheme.typography.bodySmall) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickDateDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    onDismissed: () -> Unit,
    updateShowDialogOnClick: (Boolean) -> Unit,
    vaccinationUiState: VaccinationUiState,
    saveDateSelected: (DatePickerState) -> String,
    state: DatePickerState
) {
    vaccinationUiState.setDate(saveDateSelected(state))
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodySmall,
        value = vaccinationUiState.getDate(),
        onValueChange = {vaccinationUiState.setDate(saveDateSelected(state))},
        label = { Text(text = "Vaccination date", style = MaterialTheme.typography.bodySmall)},
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
        initialSelectedDateMillis = DateUtils()
            .stringToLocalDate(vaccinationUiState.getDate())
            .atStartOfDay()
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())

    var showDialog by remember { mutableStateOf(false) }

    PickDateDialog(
        modifier = modifier,
        showDialog = showDialog,
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
            localDateToString!!
        } )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    modifier: Modifier = Modifier,
    vaccinationUiState: VaccinationUiState,
    expanded: Boolean, onExpand: (Boolean) -> Unit,
    optionSelected: String, onOptionSelected: (String) -> Unit,
    onDismissed: () -> Unit
) {
    val options = vaccinationUiState.options

    Box(
            modifier = modifier,
            contentAlignment = Alignment.TopCenter) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = onExpand
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    textStyle = MaterialTheme.typography.bodySmall,
                    readOnly = true,
                    value = vaccinationUiState.getName(),
                    onValueChange = {
                        vaccinationUiState.setName(optionSelected)
                    },
                    label = { Text(text = "Vaccination name", style = MaterialTheme.typography.bodySmall) },
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
                            onClick = {
                                onOptionSelected(option)
                                onExpand(false)
                            }
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
        optionSelected = optionSelected, onOptionSelected = {
            vaccinationUiState.setName(optionSelected)
            optionSelected = it
                                                            },
        onDismissed = {expanded = false}
    )
}