package com.example.nkhukumanagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import kotlinx.coroutines.launch

@Composable
fun PlannerScreen(modifier: Modifier = Modifier,
                  canNavigateBack: Boolean = false,
                  plannerViewModel: PlannerViewModel,
                  navigateToResultsScreen: () -> Unit = {}
){
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isCalculateButtonEnabled by remember { mutableStateOf(false) }
    Scaffold (
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Planner.resourceId),
                canNavigateBack = canNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ){ innerPadding ->
        isCalculateButtonEnabled = plannerViewModel.plannerUiState.isValid()
        PlannerCardEntry(
            modifier = modifier.padding(innerPadding),
            isCalculateButtonEnabled = plannerViewModel.plannerUiState.isValid(),
            onCalculate = {
                if (checkNumberExceptions(plannerViewModel.plannerUiState)) {
                    navigateToResultsScreen()
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message = "Please enter a valid number.")
                    }
                }
                 },
            plannerUiState = plannerViewModel.plannerUiState,
            onValueChanged = plannerViewModel::updateUiState
        )
    }

}
@Composable
fun PlannerCardEntry(
    modifier: Modifier = Modifier,
    onCalculate: () -> Unit = {},
    isCalculateButtonEnabled: Boolean,
    plannerUiState: PlannerUiState,
    onValueChanged: (PlannerUiState) -> Unit = {}) {
    //Define dependent checkboxes states
    val (checkboxState, onStateChange) = rememberSaveable { mutableStateOf(false) }
    val (checkboxState2, onStateChange2) = rememberSaveable { mutableStateOf(false) }

    //TriStateCheckbox state reflects state of dependent checkboxes
    val parentState = remember(checkboxState, checkboxState2) {
        if (checkboxState && checkboxState2) ToggleableState.On
        else if(!checkboxState && !checkboxState2) ToggleableState.Off
        else ToggleableState.Indeterminate
    }
    //click on ParentCheckbox to set state for child checkboxes
    val onParentClick = {
        val state = parentState != ToggleableState.On
        onStateChange(state)
        onStateChange2(state)
        onValueChanged(plannerUiState.copy(areDrinkersAvailable = state, areFeedersAvailable = state))

    }
    Column(modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = plannerUiState.quantityToOrder,
            onValueChange = { onValueChanged(plannerUiState.copy(quantityToOrder = it)) },
            label = { Text("How many chicks would you like to order?")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Column {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ){
                TriStateCheckbox(
                    state = parentState,
                    onClick = onParentClick
                )
                Text("Check what you have")
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column(modifier = Modifier
                .padding(start = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row (horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = checkboxState,
                        onCheckedChange = {
                            onStateChange(it)
                            onValueChanged(plannerUiState.copy(areFeedersAvailable = it))
                        })
                    Text("Feeders")
                }
                Row (horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically){
                    Checkbox(checked = checkboxState2,
                        onCheckedChange = {
                            onStateChange2(it)
                            onValueChanged(plannerUiState.copy(areDrinkersAvailable = it))
                        }
                    )
                    Text("Drinkers")
                }

            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onCalculate,
            enabled = isCalculateButtonEnabled
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Calculate",
                textAlign = TextAlign.Center)
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PlannerPreview() {
    NkhukuManagementTheme {
        PlannerCardEntry(isCalculateButtonEnabled = true, plannerUiState = PlannerUiState())
    }
}