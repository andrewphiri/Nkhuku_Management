package com.example.nkhukumanagement.userinterface.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import com.example.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import com.example.nkhukumanagement.userinterface.vaccination.VaccinationViewModel
import com.example.nkhukumanagement.utils.BaseSingleRowItem
import com.example.nkhukumanagement.utils.DateUtils
import com.example.nkhukumanagement.utils.ShowAlertDialog
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToAddFlock: () -> Unit,
    navigateToFlockDetails: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel,
    vaccinationViewModel: VaccinationViewModel
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    DisposableEffect(Unit) {
        onDispose {
            flockEntryViewModel.resetAll()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                stringResource(NavigationBarScreens.Home.resourceId),
                canNavigateBack = false
            )
        },
        floatingActionButton = {
            AnimatedVisibility(visible = listState.isScrollingUp(),
                enter = slideIn(tween(200, easing = LinearOutSlowInEasing),
                    initialOffset = {
                        IntOffset(180, 90)
                    }),
                exit = slideOut(tween(200, easing = FastOutSlowInEasing)) {
                    IntOffset(180, 90)
                }
            ) {
                FloatingActionButton(
                    onClick = navigateToAddFlock,
                    modifier = Modifier.navigationBarsPadding(),
                    shape = ShapeDefaults.Small,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    elevation = FloatingActionButtonDefaults.elevation()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add flock",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

        },
    ) { innerPadding ->
        FlockBody(
            modifier = Modifier.padding(innerPadding),
            flockList = homeUiState.flockList,
            onItemClick = navigateToFlockDetails,
            listState = listState,
            onDelete = { index ->
                coroutineScope.launch {
                    val uniqueId = homeUiState.flockList[index].uniqueId
                    flockEntryViewModel.deleteFlock(uniqueId)
                    vaccinationViewModel.deleteVaccination(uniqueId)
                    vaccinationViewModel.deleteFeed(uniqueId)
                    vaccinationViewModel.deleteWeight(uniqueId)
                    flockEntryViewModel.deleteFlockHealth(uniqueId)

                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockBody(
    modifier: Modifier = Modifier,
    flockList: List<Flock>,
    onItemClick: (Int) -> Unit,
    listState: LazyListState,
    onDelete: (Int) -> Unit
) {
    if (flockList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = modifier.align(Alignment.Center),
                text = stringResource(R.string.no_flocks_description),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        FlockList(
            modifier = modifier,
            flockList = flockList,
            onItemClick = { onItemClick(it.id) },
            listState = listState, onDelete = onDelete
        )
    }
}

/**
 * Composable function to detect scroll position of list
 * This will be used to hide FAB when scrolling down, and to Show FAB when scrolling up
 */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) {
        mutableStateOf(firstVisibleItemIndex)
    }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }

    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockList(
    modifier: Modifier = Modifier,
    flockList: List<Flock>,
    onItemClick: (Flock) -> Unit,
    listState: LazyListState, onDelete: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        itemsIndexed(flockList) { index, flock ->
            FlockCard(flock = flock, onItemClick = onItemClick, onDelete = { onDelete(index) })
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockCard(
    modifier: Modifier = Modifier,
    flock: Flock,
    onItemClick: (Flock) -> Unit,
    onDelete: () -> Unit
) {
    var isMenuShowing by remember { mutableStateOf(false) }
    var isAlertDialogShowing by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier.padding(8.dp)
            .clickable { onItemClick(flock) },
        elevation = CardDefaults.cardElevation()
    ) {
        Column {

            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Image(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        painter = painterResource(flock.imageResourceId),
                        contentDescription = ("Breed is ${flock.breed}. Batch number ${flock.id}"),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    modifier = Modifier.weight(3f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BaseSingleRowItem(
                            modifier = Modifier.weight(weight = 1f, fill = true),
                            label = "Name: ",
                            value = flock.batchName,
                            styleForLabel = MaterialTheme.typography.titleSmall,
                            weightA = 0.5f
                        )
                        Column(modifier = Modifier.weight(0.2f)) {
                            ShowOverflowMenu(
                                modifier = Modifier.align(Alignment.End),
                                isOverflowMenuExpanded = isMenuShowing,
                                isAlertDialogShowing = isAlertDialogShowing,
                                onDismissAlertDialog = { isAlertDialogShowing = false },
                                onShowMenu = { isMenuShowing = true },
                                onShowAlertDialog = { isAlertDialogShowing = true },
                                onDismiss = { isMenuShowing = false },
                                onDelete = {
                                    onDelete()
                                    isAlertDialogShowing = false
                                    isMenuShowing = false
                                },
                                title = "Delete flock?",
                                message = "This cannot be undone.")
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        BaseSingleRowItem(
                            label = "Breed: ",
                            value = flock.breed
                        )
                        BaseSingleRowItem(
                            label = "Date: ",
                            value = DateUtils().dateToStringLongFormat(flock.datePlaced)
                        )
                        BaseSingleRowItem(
                            label = "Age: ",
                            value = "${
                                DateUtils().calculateAge(
                                    birthDate = flock.datePlaced,
                                    today = LocalDate.now()
                                )
                            } day/s",
                        )

                        BaseSingleRowItem(
                            label = "Quantity: ",
                            value = (flock.numberOfChicksPlaced + flock.donorFlock).toString()
                        )
                        BaseSingleRowItem(label = "Mortality: ", value = flock.mortality.toString())

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Stock: ${(flock.stock)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier
                                    .padding(8.dp),
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
fun ShowOverflowMenu(
    modifier: Modifier = Modifier,
    isOverflowMenuExpanded: Boolean = false,
    isAlertDialogShowing: Boolean = false,
    onDismissAlertDialog: () -> Unit = {},
    onShowMenu: () -> Unit = {},
    onShowAlertDialog: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    title: String,
    message: String
) {

    ShowAlertDialog(
        onDismissAlertDialog = onDismissAlertDialog,
        onDelete = onDelete,
        isAlertDialogShowing = isAlertDialogShowing,
        title = title,
        message = message
    )
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(
            onClick = onShowMenu
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Show overflow menu"
            )
        }
        DropdownMenu(
            modifier = modifier,
            expanded = isOverflowMenuExpanded,
            onDismissRequest = onDismiss
        ) {
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = onShowAlertDialog
            )
            DropdownMenuItem(
                text = { Text("Close") },
                onClick = {}
            )
            DropdownMenuItem(
                text = { Text("Edit") },
                onClick = { }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ShowPreview() {
    NkhukuManagementTheme {

    }
}