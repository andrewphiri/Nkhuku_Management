package and.drew.nkhukumanagement.userinterface.weight

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.FlockWithWeight
import and.drew.nkhukumanagement.data.Weight
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.ContentType
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

object WeightScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Scale
    override val route: String
        get() = "Weight"
    override val resourceId: Int
        get() = R.string.weight
    const val flockIdArg = "id"
    const val weightIdArg = "weightIdArg"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg) {
        defaultValue = 1
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    weightViewModel: WeightViewModel = hiltViewModel(),
    contentType: ContentType
) {
    BackHandler {
        onNavigateUp()
    }
    val coroutineScope = rememberCoroutineScope()
    val flockWithWeights by weightViewModel.flockWithWeight.collectAsState(
        initial = FlockWithWeight(flock = null, weights = listOf())
    )
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val weightList: List<Weight> = flockWithWeights.weights ?: listOf()
    val weightUiStateList: MutableList<WeightUiState> = mutableListOf()
    var title by remember { mutableStateOf("") }
    title = stringResource(WeightScreenDestination.resourceId)
    val snackBarHostState = remember { SnackbarHostState() }
    var showUpdateDialog by remember { mutableStateOf(false) }
    val weight = weightViewModel.weight.asLiveData()

    var isSaveButtonEnabled by remember { mutableStateOf(true) }
    var actualNewWeight by remember { mutableStateOf(-1.0) }
    isSaveButtonEnabled =
        weightViewModel.weightUiState.actualWeight != "0.0"
                && weightViewModel.weightUiState.actualWeight.isNotBlank()
                && checkNumberExceptions(weightViewModel.weightUiState)
                && weightViewModel.weightUiState.actualWeight != actualNewWeight.toString()

    for (item in weightList) {
        weightUiStateList.add(item.toWeightUiState())
    }

    weightViewModel.setWeightList(weightUiStateList.toMutableStateList())

    var isUpdateEnabled by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = title,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                contentType = contentType
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { innerPadding ->
        isUpdateEnabled =
            weightViewModel.getWeightList().zip(weightUiStateList).all { it.first.actualWeight == it.second.actualWeight }
                .not()
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WeightInputList(
                weightUiStateList = weightViewModel.getWeightList(),
                onItemChange = weightViewModel::updateWeightState,
                onClickUpdate = {
                    if (checkNumberExceptions(it)) {
                        coroutineScope.launch {
                            weightViewModel.updateWeight(it.toWeight())
                            showUpdateDialog = false
                        }
                    } else {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(
                                message = context.getString(
                                    R.string.please_enter_a_valid_number
                                )
                            )
                        }
                    }
                },
                onDismiss = { showUpdateDialog = false },
                showDialog = showUpdateDialog ,
                setWeightState = {
                    weightViewModel.setWeightState(it)
                },
                onItemClick = {
                    weightViewModel.setWeightID(it)
                    weight.observe(lifecycleOwner) {
                        weightViewModel.setWeightState(it.toWeightUiState())
                        actualNewWeight = it.weight
                    }
                    showUpdateDialog = true
                },
                weightUiState = weightViewModel.weightUiState,
                isSaveButtonEnabled = isSaveButtonEnabled
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightInputList(
    modifier: Modifier = Modifier,
    onItemChange: (Int, WeightUiState) -> Unit,
    weightUiState: WeightUiState,
    weightUiStateList: MutableList<WeightUiState>,
    onClickUpdate: (WeightUiState) -> Unit,
    onDismiss: () -> Unit,
    setWeightState: (WeightUiState) -> Unit,
    showDialog: Boolean,
    onItemClick: (Int) -> Unit,
    isSaveButtonEnabled: Boolean
) {
    Column(
        modifier = modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = ""
            )
            Text(
                modifier = Modifier.weight(1.5f, fill = true),
                text = stringResource(R.string.actual_kg),
                textAlign = TextAlign.Center
            )

            VerticalDivider(
                modifier = Modifier.weight(0.01f).fillMaxHeight(),
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                modifier = Modifier.weight(1.5f, fill = true),
                text = stringResource(R.string.standard_kg),
                textAlign = TextAlign.Center
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(weightUiStateList) { index, weightItem ->
                WeightCard(
                    weightUiState = weightItem,
                    onValueChanged = { weight ->
                        onItemChange(index, weight)
                    },
                    description = "Weight $index",
                    onItemClick = {
                        onItemClick(it)
                    }
                )
            }
        }
        UpdateWeightDialog(
            onDismiss = onDismiss,
            onChangedValue = {
                try {
                    setWeightState(
                        weightUiState.copy(
                            week = it.week,
                            actualWeight = it.actualWeight,
                            dateMeasured = it.getDate(),
                            standard = it.standard
                        ))
                } catch (e: NumberFormatException) {
                    setWeightState(
                        weightUiState.copy(
                            week = it.week,
                            actualWeight = ""
                        ))
                }
            },
            onUpdateWeight = onClickUpdate,
            showDialog = showDialog,
            weightUiState = weightUiState,
            isSaveButtonEnabled = isSaveButtonEnabled
        )
    }
}

@Composable
fun WeightCard(
    modifier: Modifier = Modifier,
    weightUiState: WeightUiState,
    onValueChanged: (WeightUiState) -> Unit,
    description: String,
    onItemClick: (Int) -> Unit
) {

    Row(
        modifier = modifier.height(IntrinsicSize.Max)
            .clickable { onItemClick(weightUiState.id) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = weightUiState.week
        )

            TextField(
                modifier = Modifier
                    .weight(1.5f)
                    .semantics { contentDescription = description },
                value = if (weightUiState.actualWeight == stringResource(R.string._0_0)) "" else weightUiState.actualWeight,
                onValueChange = {
                    onValueChanged(
                        weightUiState.copy(
                            actualWeight =  it
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                    disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )

            VerticalDivider(
                modifier = Modifier.weight(0.01f).fillMaxHeight(),
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            TextField(
                modifier = Modifier.weight(1.5f),
                value = weightUiState.standard,
                onValueChange = {
                    onValueChanged(weightUiState.copy(standard = it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                    disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )
        }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UpdateWeightDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    weightUiState: WeightUiState,
    onChangedValue: (WeightUiState) -> Unit = {},
    onDismiss: () -> Unit,
    onUpdateWeight: (WeightUiState) -> Unit,
    isSaveButtonEnabled: Boolean
) {

    var actualWeight by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties()
        ) {
            OutlinedCard(
                modifier = modifier,
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = weightUiState.week,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    TextField(
                        value = if (weightUiState.actualWeight == stringResource(R.string._0_0)) actualWeight else weightUiState.actualWeight,
                        onValueChange = {
                            actualWeight =  it
                            onChangedValue(weightUiState.copy(actualWeight =  it))
                        },
                        label = { Text(text = stringResource(R.string.actual_weight)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(
                                Dp.Hairline,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            onClick = {
                                onDismiss()
                                actualWeight = ""
                            }
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                text = stringResource(R.string.cancel),
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = isSaveButtonEnabled,
                            onClick = { onUpdateWeight(weightUiState.copy( actualWeight = actualWeight)) }
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                text = stringResource(R.string.save),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}