package and.drew.nkhukumanagement.userinterface.planner

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DropDownMenuDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable object PlannerScreenNav

@Composable
fun PlannerScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    plannerViewModel: PlannerViewModel,
    navigateToResultsScreen: () -> Unit = {},
    onClickSettings: () -> Unit,
    contentType: ContentType
) {

    MainPlannerScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        plannerUiState = plannerViewModel.plannerUiState,
        onClickSettings = onClickSettings,
        onValueChanged = plannerViewModel::updateUiState,
        navigateToResultsScreen = navigateToResultsScreen,
        contentType = contentType
    )
}

@Composable
fun MainPlannerScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    plannerUiState: PlannerUiState,
    onValueChanged: (PlannerUiState) -> Unit,
    navigateToResultsScreen: () -> Unit = {},
    onClickSettings: () -> Unit,
    contentType: ContentType
) {
    val context = LocalContext.current
    val flockTypeOptions = context.resources.getStringArray(R.array.types_of_flocks).toList()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isCalculateButtonEnabled by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(R.string.planner),
                canNavigateBack = canNavigateBack,
                onClickSettings = onClickSettings,
                contentType = contentType
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        isCalculateButtonEnabled = plannerUiState.isValid()
        PlannerCardEntry(
            modifier = modifier.padding(innerPadding),
            isCalculateButtonEnabled = plannerUiState.isValid(),
            onCalculate = {
                if (checkNumberExceptions(plannerUiState)) {
                    navigateToResultsScreen()
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message = context.getString(R.string.please_enter_a_valid_number))
                    }
                }
            },
            plannerUiState = plannerUiState,
            onValueChanged = onValueChanged,
            flockOptions = flockTypeOptions.take(2),
            flockType = plannerUiState.flockType
        )
    }

}

@Composable
fun PlannerCardEntry(
    modifier: Modifier = Modifier,
    onCalculate: () -> Unit = {},
    isCalculateButtonEnabled: Boolean,
    plannerUiState: PlannerUiState,
    onValueChanged: (PlannerUiState) -> Unit = {},
    flockType: String,
    flockOptions: List<String>,
) {
    val context = LocalContext.current
    //Define dependent checkboxes states
    val (checkboxState, onStateChange) = rememberSaveable { mutableStateOf(false) }
    val (checkboxState2, onStateChange2) = rememberSaveable { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }

    //TriStateCheckbox state reflects state of dependent checkboxes
    val parentState = remember(checkboxState, checkboxState2) {
        if (checkboxState && checkboxState2) ToggleableState.On
        else if (!checkboxState && !checkboxState2) ToggleableState.Off
        else ToggleableState.Indeterminate
    }
    //click on ParentCheckbox to set state for child checkboxes
    val onParentClick = {
        val state = parentState != ToggleableState.On
        onStateChange(state)
        onStateChange2(state)
        onValueChanged(
            plannerUiState.copy(
                areDrinkersAvailable = state,
                areFeedersAvailable = state
            )
        )

    }
    Column(
        modifier = modifier.padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        DropDownMenuDialog(
            value = flockType,
            onDismissed = { expanded = false },
            options = flockOptions,
            onOptionSelected = {
                onValueChanged(plannerUiState.copy(flockType = it))
            },
            onExpand = { expanded = !expanded },
            label = stringResource(R.string.flock_type),
            expanded = expanded,
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = plannerUiState.quantityToOrder,
            onValueChange = { onValueChanged(plannerUiState.copy(quantityToOrder = it)) },
            label = { Text(stringResource(R.string.how_many_chicks_would_you_like_to_order)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TriStateCheckbox(
                    modifier = Modifier.semantics {
                        contentDescription =
                            context.getString(R.string.tick_all)
                    },
                    state = parentState,
                    onClick = onParentClick
                )
                Text(stringResource(R.string.tick_what_you_have))
            }

            Spacer(modifier = Modifier.size(16.dp))

            Column(
                modifier = Modifier
                    .padding(start = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = checkboxState,
                        onCheckedChange = {
                            onStateChange(it)
                            onValueChanged(plannerUiState.copy(areFeedersAvailable = it))
                        })
                    Text(stringResource(R.string.feeders))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = checkboxState2,
                        onCheckedChange = {
                            onStateChange2(it)
                            onValueChanged(plannerUiState.copy(areDrinkersAvailable = it))
                        }
                    )
                    Text(stringResource(R.string.drinkers))
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
                text = stringResource(R.string.calculate),
                textAlign = TextAlign.Center
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PlannerPreview() {
    NkhukuManagementTheme {
        PlannerCardEntry(
            isCalculateButtonEnabled = true,
            plannerUiState = PlannerUiState(),
            flockType = "Broiler", flockOptions = listOf("Broiler", "Layer"))
    }
}