package and.drew.nkhukumanagement.userinterface.feed

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.Feed
import and.drew.nkhukumanagement.data.FlockWithFeed
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockUiState
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.AddNewEntryDialog
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DateUtils
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.asLiveData
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import java.time.LocalDate

object FeedScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "Feed"
    override val resourceId: Int
        get() = R.string.feed
    const val flockIdArg = "id"
    const val feedIdArg = "feedIdArg"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg) {
        defaultValue = 1
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    feedViewModel: FeedViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel,
    contentType: ContentType
) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val flockWithFeed by feedViewModel.flockWithFeed.collectAsState(
        initial = FlockWithFeed(flock = null, feedList = listOf())
    )

    val feed = feedViewModel.feed.asLiveData()

    val feedList: List<Feed> = flockWithFeed.feedList ?: listOf()
    val feedUiStateList: MutableList<FeedUiState> = mutableListOf()

    for (feedUiState in feedList) {
        feedUiStateList.add(feedUiState.toFeedUiState())
    }
    feedViewModel.setFeedList(feedUiStateList.toMutableStateList())

    MainFeedScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        onNavigateUp = onNavigateUp,
        flockUiState = flockEntryViewModel.flockUiState,
        feedList = flockWithFeed.feedList,
        setFeedList = {
            feedViewModel.setFeedList(it.toMutableStateList())
        },
        onItemChange = feedViewModel::updateFeedState,
        updateFeed = {
            coroutineScope.launch {
                feedViewModel.updateFeed(it)
            }
        },
        feedUiState = feedViewModel.feedUiState,
        feedStateList = feedUiStateList,
        setFeedState = {
            feedViewModel.setFeedState(it)
        },
        contentType = contentType,
        onItemClick = {
            feedViewModel.setFeedID(it)

            feed.observe(lifecycleOwner) {
                feedViewModel.setFeedState(it.toFeedUiState())
            }

        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainFeedScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    flockUiState: FlockUiState,
    feedList: List<Feed>?,
    setFeedList: (MutableList<FeedUiState>) -> Unit,
    onItemChange: (Int, FeedUiState) -> Unit,
    updateFeed: (FeedUiState) -> Unit,
    feedUiState: FeedUiState,
    feedStateList: List<FeedUiState>,
    setFeedState: (FeedUiState) -> Unit,
    contentType: ContentType,
    onItemClick: (Int) -> Unit
) {
    BackHandler {
        onNavigateUp()
    }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val feedUiStateList: MutableList<FeedUiState> = mutableListOf()
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var isAddTypeDialogShowing by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    if (feedList != null) {
        for (feedState in feedList) {
            feedUiStateList.add(feedState.toFeedUiState())
        }
    }
    setFeedList(feedUiStateList.toMutableStateList())

    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(FeedScreenDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                contentType = contentType
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            FeedConsumptionList(
                onItemChange = onItemChange,
                onItemClick = {
                    onItemClick(it)
                    showDialog = true
                },
                showDialog = showDialog,
                expanded = expanded,
                onExpand = { expanded = !expanded },
                onDismiss = {
                    showDialog = false
                    expanded = false
                    setFeedState(feedUiState.copy(type = "", actualConsumed = ""))
                },
                onTypeDialogShowing = { isAddTypeDialogShowing = true },
                onInnerDialogDismiss = {
                    isAddTypeDialogShowing = false
                    expanded = false
                },
                isAddFeedTypeDialogShowing = isAddTypeDialogShowing,
                flockUiState = flockUiState,
                onUpdateFeed = { feedUiState ->
                    if (checkNumberExceptions(feedUiState)) {
                        coroutineScope.launch {
                            updateFeed(feedUiState)
                            showDialog = false
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
                feedList = feedStateList,
                feedUiState = feedUiState,
                setFeedState = setFeedState
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedConsumptionList(
    modifier: Modifier = Modifier,
    flockUiState: FlockUiState,
    feedUiState: FeedUiState,
    feedList: List<FeedUiState>,
    setFeedState: (FeedUiState) -> Unit,
    onItemChange: (Int, FeedUiState) -> Unit,
    onItemClick: (Int) -> Unit,
    showDialog: Boolean,
    expanded: Boolean,
    onExpand: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onInnerDialogDismiss: () -> Unit,
    onUpdateFeed: (FeedUiState) -> Unit,
    isAddFeedTypeDialogShowing: Boolean,
    onTypeDialogShowing: () -> Unit
) {
    Column(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(0.5f),
                text = ""
            )
            Text(
                modifier = Modifier.weight(1.41f, fill = true).padding(4.dp),
                text = stringResource(R.string.consumption_per_bird),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall
            )

            VerticalDivider(
                modifier = Modifier.weight(0.01f).fillMaxHeight(),
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                modifier = Modifier.weight(1.51f, fill = true).padding(4.dp),
                text = stringResource(R.string.total_feed_consumed),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall
            )
        }

        Row(modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(0.5f),
                text = ""
            )
            Text(
                modifier = Modifier.weight(0.7f, fill = true),
                text = stringResource(R.string.actual_kg),
                style = MaterialTheme.typography.bodySmall,
                fontSize = TextUnit(10f, TextUnitType.Sp),
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.weight(0.7f, fill = true),
                text = stringResource(R.string.standard_kg),
                style = MaterialTheme.typography.bodySmall,
                fontSize = TextUnit(10f, TextUnitType.Sp),
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.weight(0.01f)
            )

            Text(
                modifier = Modifier.weight(0.75f, fill = true),
                text = stringResource(R.string.actual_kg),
                style = MaterialTheme.typography.bodySmall,
                fontSize = TextUnit(10f, TextUnitType.Sp),
                textAlign = TextAlign.Center
            )



            Text(
                modifier = Modifier.weight(0.75f, fill = true),
                text = stringResource(R.string.standard_kg),
                style = MaterialTheme.typography.bodySmall,
                fontSize = TextUnit(10f, TextUnitType.Sp),
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(
            modifier = Modifier.semantics { contentDescription = "Feed list" },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(feedList) { index, feedItem ->
                FeedCardItem(
                    feedUiState = feedItem,
                    onChangedValue = {
                        onItemChange(index, it)
                    },
                    onItemClick = {
                        onItemClick(it)
                    }
                )
            }
        }

        UpdateFeedDialog(
            showDialog = showDialog,
            expanded = expanded,
            onExpand = onExpand,
            onDismiss = onDismiss,
            onTypeDialogShowing = onTypeDialogShowing,
            onInnerDialogDismiss = onInnerDialogDismiss,
            feedUiState = feedUiState,
            isAddFeedTypeDialogShowing = isAddFeedTypeDialogShowing,
            onChangedValue = {
                try {
                    setFeedState(
                        feedUiState.copy(
                            type = it.type,
                            actualConsumed = it.actualConsumed, actualConsumptionPerBird = String
                                .format(
                                    "%.3f",
                                    it.actualConsumed.toDouble() / flockUiState.getStock()
                                        .toDouble()
                                )
                        )
                    )
                } catch (e: NumberFormatException) {
                    setFeedState(
                        feedUiState.copy(
                            type = it.type,
                            actualConsumed = ""
                        )
                    )
                }

            },
            onUpdateFeed = onUpdateFeed
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedCardItem(
    modifier: Modifier = Modifier,
    feedUiState: FeedUiState,
    onChangedValue: (FeedUiState) -> Unit = {},
    onItemClick: (Int) -> Unit
) {

    Row(
        modifier = modifier.height(IntrinsicSize.Max).padding(4.dp)
            .clickable(onClick = { onItemClick(feedUiState.id) }),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.weight(0.5f).padding(end = 4.dp),
            text = feedUiState.week,
            style = MaterialTheme.typography.bodyMedium
        )

        TextField(
            modifier = Modifier.weight(0.7f),
            value = feedUiState.actualConsumptionPerBird,
            onValueChange = {
                onChangedValue(feedUiState.copy(actualConsumptionPerBird = it))
            },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)
            ),
            textStyle = MaterialTheme.typography.labelSmall.also {
                LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center
                )
            },
        )

        VerticalDivider(
            modifier = Modifier.weight(0.01f).fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        TextField(
            modifier = Modifier.weight(0.7f),
            value = feedUiState.standardConsumptionPerBird,
            onValueChange = {
                onChangedValue(feedUiState.copy(standardConsumptionPerBird = it))
            },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)
            ),
            textStyle = MaterialTheme.typography.labelSmall.also {
                LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center
                )
            }
        )

        VerticalDivider(
            modifier = Modifier.weight(0.01f).fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        TextField(
            modifier = Modifier.weight(0.75f),
            value = feedUiState.actualConsumed,
            onValueChange = {
                onChangedValue(feedUiState.copy(actualConsumed = it))
            },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)
            ),
            textStyle = MaterialTheme.typography.labelSmall.also {
                LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center
                )
            }
        )
        VerticalDivider(
            modifier = Modifier.weight(0.01f).fillMaxHeight(),
            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
        )

        TextField(
            modifier = Modifier.weight(0.75f),
            value = feedUiState.standardConsumption,
            onValueChange = {
                onChangedValue(feedUiState.copy(standardConsumption = it))
            },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = LocalContentColor.current.alpha),
                disabledTextColor = LocalContentColor.current.copy(LocalContentColor.current.alpha)
            ),
            textStyle = MaterialTheme.typography.labelSmall.also {
                LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center
                )
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun UpdateFeedDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    feedUiState: FeedUiState,
    onChangedValue: (FeedUiState) -> Unit = {},
    expanded: Boolean,
    onExpand: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onInnerDialogDismiss: () -> Unit,
    onUpdateFeed: (FeedUiState) -> Unit,
    isAddFeedTypeDialogShowing: Boolean,
    onTypeDialogShowing: () -> Unit,
) {
    var newFeedType by remember { mutableStateOf("") }
    var isSaveButtonEnabled by remember { mutableStateOf(true) }
    isSaveButtonEnabled = feedUiState.actualConsumed != "0.0"
            && feedUiState.actualConsumed.isNotBlank()
            && checkNumberExceptions(feedUiState)
    val keyboardController = LocalSoftwareKeyboardController.current
    var actualFeedConsumed by remember { mutableStateOf("") }

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
                        text = feedUiState.week,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Row {
                        Box(
                            modifier = Modifier.weight(0.8f),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            ExposedDropdownMenuBox(
                                modifier = Modifier.semantics { contentDescription = "feed type" },
                                expanded = expanded,
                                onExpandedChange = onExpand
                            ) {
                                TextField(
                                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                                    readOnly = true,
                                    value = feedUiState.type,
                                    onValueChange = {
                                        onChangedValue(feedUiState.copy(type = it))
                                    },
                                    label = { Text(stringResource(R.string.feed_type)) },
                                    isError = feedUiState.isSingleEntryValid(feedUiState.type),
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                                )

                                ExposedDropdownMenu(
                                    modifier = Modifier.exposedDropdownSize(true),
                                    expanded = expanded,
                                    onDismissRequest = onInnerDialogDismiss
                                ) {
                                    feedUiState.options.forEach { option ->
                                        DropdownMenuItem(
                                            modifier = Modifier.semantics {
                                                contentDescription = option
                                            },
                                            text = { Text(text = option) },
                                            onClick = {
                                                onChangedValue(feedUiState.copy(type = option))
                                                onExpand(expanded)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        IconButton(
                            modifier = Modifier.weight(0.2f),
                            onClick = onTypeDialogShowing
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add type",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    TextField(
                        value = if (feedUiState.actualConsumed == stringResource(R.string._0_0)) actualFeedConsumed else feedUiState.actualConsumed,
                        onValueChange = {
                            actualFeedConsumed =  it
                            onChangedValue(feedUiState.copy(actualConsumed =  it))
                                        },
                        label = { Text(text = stringResource(R.string.quantity)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        )
                    )

                    AddNewEntryDialog(
                        entry = newFeedType,
                        showDialog = isAddFeedTypeDialogShowing,
                        onValueChanged = { newFeedType = it },
                        onDismissed = onInnerDialogDismiss,
                        label = stringResource(R.string.feed_type),
                        onSaveEntry = {
                            feedUiState.options.add(newFeedType)
                            onChangedValue(feedUiState.copy(type = newFeedType))
                            onInnerDialogDismiss()
                        },
                        isEnabled = newFeedType.isNotBlank()
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
                                actualFeedConsumed = ""
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
                            onClick = { onUpdateFeed(feedUiState.copy( actualConsumed = actualFeedConsumed)) }
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NkhukuManagementTheme {
        val feedUiState = FeedUiState(
            name = "Starter",
            week = "Week 1",
            actualConsumptionPerBird = "167",
            standardConsumptionPerBird = "167",
            standardConsumption = "7500",
            actualConsumed = "6500",
            feedingDate = LocalDate.now().toString()
        )
        //FeedCardItem(feedUiState = feedUiState, onItemClick = {})
    }
}