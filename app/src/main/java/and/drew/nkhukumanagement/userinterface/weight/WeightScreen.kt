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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
    val scrollState = rememberScrollState()
    val weightList: List<Weight> = flockWithWeights.weights ?: listOf()
    val weightUiStateList: MutableList<WeightUiState> = mutableListOf()
    var isEditable by remember { mutableStateOf(false) }
    var isFABVisible by remember { mutableStateOf(true) }
    var title by remember { mutableStateOf("") }
    title = stringResource(WeightScreenDestination.resourceId)
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

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
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFABVisible,
                enter = slideIn(tween(200, easing = LinearOutSlowInEasing),
                    initialOffset = {
                        IntOffset(180, 90)
                    }),
                exit = slideOut(tween(200, easing = FastOutSlowInEasing)) {
                    IntOffset(180, 90)
                }) {
                FloatingActionButton(
                    onClick = {
                        title = context.resources.getString(R.string.edit_weights)
                        isEditable = true
                        isFABVisible = false
                    },
                    shape = ShapeDefaults.Small,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    elevation = FloatingActionButtonDefaults.elevation()
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_weights)
                    )
                }
            }
        }
    ) { innerPadding ->
        isUpdateEnabled =
            weightViewModel.getWeightList().zip(weightUiStateList).all { it.first == it.second }
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
                isEditable = isEditable,
                isUpdateEnabled = isUpdateEnabled,
                onClickCancel = {
                    title = context.resources.getString(R.string.weight)
                    isEditable = false
                    isFABVisible = true

                    weightUiStateList.clear()
                    for (item in weightList) {
                        weightUiStateList.add(item.toWeightUiState())
                    }
                    weightViewModel.setWeightList(weightUiStateList.toMutableStateList())
                },
                onClickUpdate = {
                    val weight = weightViewModel.getWeightList()
                    val updatedWeights = mutableListOf<Weight>()

                    if (weight.all { checkNumberExceptions(it) }) {
                        weight.forEach {
                            updatedWeights.add(it.toWeight())
                        }
                        coroutineScope.launch {
                            weightViewModel.updateWeight(updatedWeights)
                        }.invokeOnCompletion { onNavigateUp() }
                    } else {
                        coroutineScope.launch {
                            snackBarHostState.showSnackbar(context.getString(R.string.please_enter_a_valid_number))
                        }
                    }
                }
            )
            if (isEditable) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            title = context.resources.getString(R.string.weight)
                            isEditable = false
                            isFABVisible = true

                            weightUiStateList.clear()
                            for (item in weightList) {
                                weightUiStateList.add(item.toWeightUiState())
                            }
                            weightViewModel.setWeightList(weightUiStateList.toMutableStateList())
                        }
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.cancel),
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        enabled = isUpdateEnabled,
                        onClick = {
                            val weight = weightViewModel.getWeightList()
                            val updatedWeights = mutableListOf<Weight>()

                            if (weight.all { checkNumberExceptions(it) }) {
                                weight.forEach {
                                    updatedWeights.add(it.toWeight())
                                }
                                coroutineScope.launch {
                                    weightViewModel.updateWeight(updatedWeights)
                                }.invokeOnCompletion { onNavigateUp() }
                            } else {
                                coroutineScope.launch {
                                    snackBarHostState.showSnackbar(context.getString(R.string.please_enter_a_valid_number))
                                }
                            }
                        }
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.update),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun WeightInputList(
    modifier: Modifier = Modifier,
    onItemChange: (Int, WeightUiState) -> Unit,
    weightUiStateList: MutableList<WeightUiState>,
    onClickCancel: () -> Unit,
    onClickUpdate: () -> Unit,
    isUpdateEnabled: Boolean,
    isEditable: Boolean
) {
    Column(
        modifier = modifier
            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min),
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

            HorizontalDivider(
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
                        //isUpdateButtonEnabled(weightViewModel.isUpdateButtonEnabled(weightUiState))
                    },
                    isEditable = isEditable,
                    description = "Weight $index"
                )
            }
            item {
                if (isEditable) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onClickCancel
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.cancel),
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = isUpdateEnabled,
                            onClick = onClickUpdate
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
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
fun WeightCard(
    modifier: Modifier = Modifier,
    weightUiState: WeightUiState,
    onValueChanged: (WeightUiState) -> Unit,
    isEditable: Boolean,
    description: String
) {
    val context = LocalContext.current
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = weightUiState.week
        )

        Row(
            modifier = Modifier.weight(2f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TextField(
                modifier = Modifier
                    .weight(1.5f)
                    .semantics { contentDescription = description },
                value = if (weightUiState.actualWeight == stringResource(R.string._0_0)) "" else weightUiState.actualWeight,
                onValueChange = {
                    onValueChanged(
                        weightUiState.copy(
                            actualWeight = if (it == "") context.getString(
                                R.string._0_0
                            ) else it
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = isEditable,
                colors = TextFieldDefaults.colors(
                    disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                    disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)
                ),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )

            HorizontalDivider(
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
}