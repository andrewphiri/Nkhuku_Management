package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.FeedUiState
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.checkNumberExceptions
import com.example.nkhukumanagement.data.Feed
import com.example.nkhukumanagement.isSingleEntryValid
import com.example.nkhukumanagement.toFeedUiState
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
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
    flockEntryViewModel: FlockEntryViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val flockWithFeed by feedViewModel.flockWithFeed.collectAsState()
    val feedList: List<Feed> = flockWithFeed.feedList ?: listOf()
    val feedUiStateList: MutableList<FeedUiState> = mutableListOf()
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var isAddTypeDialogShowing by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }

    for (feedUiState in feedList) {
        feedUiStateList.add(feedUiState.toFeedUiState())
    }
    feedViewModel.setFeedList(feedUiStateList.toMutableStateList())


    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(FeedScreenDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            FeedConsumptionList(
                feedViewModel = feedViewModel,
                onItemChange = feedViewModel::updateFeedState,
                onItemClick = {
                    showDialog = true
                },
                showDialog = showDialog,
                expanded = expanded,
                onExpand = { expanded = !expanded },
                onDismiss = {
                    showDialog = false
                    expanded = false
                },
                onTypeDialogShowing = { isAddTypeDialogShowing = true },
                onInnerDialogDismiss = { isAddTypeDialogShowing = false },
                isAddFeedTypeDialogShowing = isAddTypeDialogShowing,
                flockUiState = flockEntryViewModel.flockUiState,
                onUpdateFeed = { feedUiState ->

                    if (checkNumberExceptions(feedUiState)) {
                        coroutineScope.launch {
                            feedViewModel.updateFeed(feedUiState)
                        }.invokeOnCompletion {
                            showDialog = false
                        }
                    } else {
                        coroutineScope.launch { snackBarHostState.showSnackbar(message = "Please enter a valid number.") }
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedConsumptionList(
    modifier: Modifier = Modifier,
    feedViewModel: FeedViewModel,
    flockUiState: FlockUiState,
    onItemChange: (Int, FeedUiState) -> Unit,
    onItemClick: () -> Unit,
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
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Text(
                modifier = Modifier.weight(0.5f),
                text = ""
            )
            Text(
                modifier = Modifier.weight(1.41f, fill = true).padding(4.dp),
                text = "CONSUMPTION PER BIRD",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall
            )

            Divider(
                modifier = Modifier.weight(0.02f).fillMaxHeight(),
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                modifier = Modifier.weight(1.51f, fill = true).padding(4.dp),
                text = "TOTAL FEED CONSUMED",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall
            )
        }

        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Text(
                modifier = Modifier.weight(0.5f),
                text = ""
            )
            Text(
                modifier = Modifier.weight(0.70f, fill = true),
                text = "ACTUAL \n Kg",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(0.71f, fill = true),
                text = "STANDARD \n Kg",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )


            Text(
                modifier = Modifier.weight(0.755f, fill = true),
                text = "ACTUAL \n Kg",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(0.755f, fill = true),
                text = "STANDARD \n Kg",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(feedViewModel.getFeedList()) { index, feedItem ->
                FeedCardItem(
                    feedUiState = feedItem,
                    onChangedValue = {
                        onItemChange(index, it)
                    },
                    onItemClick = {
                        onItemClick()
                        feedViewModel.setFeedState(feedItem)
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
            feedUiState = feedViewModel.feedUiState,
            isAddFeedTypeDialogShowing = isAddFeedTypeDialogShowing,
            onChangedValue = {
                try {
                    feedViewModel.setFeedState(
                        feedViewModel.feedUiState.copy(
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
                    feedViewModel.setFeedState(
                        feedViewModel.feedUiState.copy(
                            type = it.type,
                            actualConsumed = it.actualConsumed
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
    onItemClick: () -> Unit
) {

    Row(
        modifier = modifier.height(IntrinsicSize.Max).padding(4.dp)
            .clickable(onClick = onItemClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            modifier = Modifier.weight(0.5f),
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

        Divider(
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

        Divider(
            modifier = Modifier.weight(0.02f).fillMaxHeight(),
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

        Divider(
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
    onTypeDialogShowing: () -> Unit
) {
    var newFeedType by remember { mutableStateOf("") }
    var isSaveButtonEnabled by remember { mutableStateOf(true) }
    isSaveButtonEnabled = feedUiState.isSingleEntryValid(feedUiState.type)
            && feedUiState.actualConsumed != "0.0"
            && feedUiState.actualConsumed.isNotBlank()
            && checkNumberExceptions(feedUiState)
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
                                    label = { Text("Feed type") },
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
                        value = if (feedUiState.actualConsumed == "0.0") "" else feedUiState.actualConsumed,
                        onValueChange = { onChangedValue(feedUiState.copy(actualConsumed = it)) },
                        label = { Text("Quantity") },
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
                        label = "Feed type",
                        onSaveBreed = {
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
                            onClick = onDismiss
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                text = "Cancel",
                                textAlign = TextAlign.Center
                            )
                        }

                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = isSaveButtonEnabled,
                            onClick = { onUpdateFeed(feedUiState) }
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