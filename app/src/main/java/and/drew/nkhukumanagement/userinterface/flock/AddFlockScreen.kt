package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.AddNewEntryDialog
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.DropDownMenuDialog
import and.drew.nkhukumanagement.utils.PickerDateDialog
import android.os.Build
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    flockEntryViewModel: FlockEntryViewModel,
    userPrefsViewModel: UserPrefsViewModel,
    contentType: ContentType
) {

    BackHandler {
        onNavigateUp()
        flockEntryViewModel.resetAll()
    }
    val currency by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )

    MainAddFlockScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        onNavigateUp = {
            onNavigateUp()
            flockEntryViewModel.resetAll()
        },
        navigateToVaccinationsScreen = navigateToVaccinationsScreen,
        flockUiState = flockEntryViewModel.flockUiState,
        onItemValueChange = flockEntryViewModel::updateUiState,
        currencySymbol = currency.symbol,
        contentType = contentType
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainAddFlockScreen(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean,
    navigateToVaccinationsScreen: (FlockUiState) -> Unit,
    flockUiState: FlockUiState,
    onItemValueChange: (FlockUiState) -> Unit,
    currencySymbol: String,
    contentType: ContentType
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    Scaffold(
        modifier = Modifier.semantics { contentDescription = "Add flock" },
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(AddFlockDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = { onNavigateUp() },
                contentType = contentType
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding).verticalScroll(scrollState)) {
            AddFlockBody(
                flockUiState = flockUiState,
                onItemValueChange = onItemValueChange,
                onVaccinationsScreen = {
                    if (checkNumberExceptions(flockUiState)) {
                        flockUiState.setStock(it.quantity, it.donorFlock)
                        navigateToVaccinationsScreen(flockUiState)
                    } else {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(message = context.getString(R.string.please_enter_a_valid_number))
                        }
                    }
                },
                currencySymbol = currencySymbol
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddFlockBody(
    flockUiState: FlockUiState,
    onItemValueChange: (FlockUiState) -> Unit,
    modifier: Modifier = Modifier,
    onVaccinationsScreen: (FlockUiState) -> Unit,
    currencySymbol: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
    ) {
        AddFlockInputForm(
            flockUiState = flockUiState, modifier = modifier,
            onValueChanged = onItemValueChange,
            currencySymbol = currencySymbol
        )
        Button(
            modifier = Modifier
                .semantics { contentDescription = "navigate to vaccination screen" }
                .fillMaxWidth()
                .padding(top = 16.dp),
            onClick = { onVaccinationsScreen(flockUiState) },
            enabled = flockUiState.enabled
        ) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.set_vaccination_days),
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
    onValueChanged: (FlockUiState) -> Unit = {},
    currencySymbol: String
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
    var isBatchNameBlank by remember { mutableStateOf(false) }
    var isQuantityBlank by remember { mutableStateOf(false) }
    var isPriceBlank by remember { mutableStateOf(false) }
    var isDonorBlank by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp).also { Arrangement.Center }
    ) {

        Row {
            DropDownMenuDialog(
                modifier = Modifier.weight(0.8f).semantics { contentDescription = "breed options" },
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
                label = stringResource(R.string.breed)
            )
            IconButton(
                modifier = Modifier.weight(0.2f),
                onClick = { isBreedDialogShowing = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_breed),
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
            label = stringResource(R.string.add_breed)
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = flockUiState.batchName,
            onValueChange = {
                onValueChanged(flockUiState.copy(batchName = it))
                isBatchNameBlank = flockUiState.isSingleEntryValid(it)
            },
            label = { Text(text = stringResource(R.string.batch_name)) },
            enabled = true,
            singleLine = true,
            isError = isBatchNameBlank
        )

        PickerDateDialog(
            showDialog = showDialog,
            onDismissed = { showDialog = false },
            label = stringResource(R.string.date_received),
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
            modifier = Modifier
                .fillMaxWidth(),
            value = flockUiState.quantity,
            onValueChange = {
                onValueChanged(flockUiState.copy(quantity = it))
                isQuantityBlank = flockUiState.isSingleEntryValid(it)
            },
            label = { Text(text = stringResource(R.string.number_of_chicks_placed)) },
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            isError = isQuantityBlank
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = flockUiState.cost,
            onValueChange = {
                onValueChanged(flockUiState.copy(cost = it))
                isPriceBlank = flockUiState.isSingleEntryValid(it)
            },
            label = { Text(stringResource(R.string.price_per_bird)) },
            prefix = {
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = currencySymbol
                )

            },
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isPriceBlank
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = flockUiState.donorFlock,
            onValueChange = {
                onValueChanged(flockUiState.copy(donorFlock = it))
                isDonorBlank = flockUiState.isSingleEntryValid(it)
            },
            label = { Text(stringResource(R.string.donor_flock)) },
            isError = isDonorBlank,
            enabled = true,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

    }

}


@Preview(showBackground = true)
@Composable
private fun AddFlockScreenPreview() {
    NkhukuManagementTheme {
//        AddFlockBody()
    }
}