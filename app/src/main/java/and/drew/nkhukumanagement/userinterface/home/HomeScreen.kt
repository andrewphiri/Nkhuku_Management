package and.drew.nkhukumanagement.userinterface.home

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.accounts.AccountsViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import and.drew.nkhukumanagement.userinterface.flock.toFlockUiState
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.userinterface.vaccination.VaccinationViewModel
import and.drew.nkhukumanagement.utils.BaseSingleRowItem
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.ShowAlertDialog
import and.drew.nkhukumanagement.utils.ShowFilterOverflowMenu
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToAddFlock: () -> Unit,
    navigateToFlockDetails: (Int) -> Unit,
    onSignOut: () -> Unit = {},
    homeViewModel: HomeViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel,
    vaccinationViewModel: VaccinationViewModel,
    accountsViewModel: AccountsViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    var accountSummary: AccountsSummary? = AccountsSummary(
        flockUniqueID = "",
        batchName = "",
        totalIncome = 0.0,
        totalExpenses = 0.0,
        variance = 0.0
    )
    var flockList = homeUiState.flockList.filter { it.active }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isFilterMenuShowing by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            flockEntryViewModel.resetAll()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Home.resourceId),
                canNavigateBack = false,
                onSignOut = onSignOut,
                isFilterButtonEnabled = homeUiState.flockList.isNotEmpty(),
                onClickFilter = {
                    isFilterMenuShowing = !isFilterMenuShowing
                }
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
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            ShowFilterOverflowMenu(
                modifier = Modifier.align(Alignment.End),
                isOverflowMenuExpanded = isFilterMenuShowing,
                onDismiss = { isFilterMenuShowing = false },
                onClickAll = {
                    flockList = homeUiState.flockList
                    isFilterMenuShowing = false
                },
                onClickActive = {
                    flockList = homeUiState.flockList.filter { it.active }
                    isFilterMenuShowing = false
                },
                onClickInactive = {
                    flockList = homeUiState.flockList.filter { !it.active }
                    isFilterMenuShowing = false
                }
            )

            FlockBody(
                flockList = flockList,
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
                },
                onClose = { flock ->

                    accountsViewModel.flockRepository.getFlockAndAccountSummary(flock.id)
                        .observe(lifecycleOwner) { flockAndSummary ->
                            accountSummary = flockAndSummary.accountsSummary
                        }
                    coroutineScope.launch {

                        if (flock.active) {
                            flockEntryViewModel.updateItem(
                                flockUiState = flock.toFlockUiState().copy(active = false)
                            )
                            accountSummary?.let {
                                AccountsSummary(
                                    id = it.id,
                                    totalIncome = it.totalIncome,
                                    flockUniqueID = it.flockUniqueID,
                                    totalExpenses = it.totalExpenses,
                                    variance = it.variance,
                                    batchName = it.batchName,
                                    flockActive = false
                                )
                            }?.let { accountsViewModel.updateAccountsSummary(it) }
                        } else {
                            flockEntryViewModel.updateItem(
                                flockUiState = flock.toFlockUiState().copy(active = true)
                            )
                            accountSummary?.let {
                                AccountsSummary(
                                    id = it.id,
                                    totalIncome = it.totalIncome,
                                    flockUniqueID = it.flockUniqueID,
                                    totalExpenses = it.totalExpenses,
                                    variance = it.variance,
                                    batchName = it.batchName,
                                    flockActive = true
                                )
                            }?.let { accountsViewModel.updateAccountsSummary(it) }
                        }
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockBody(
    modifier: Modifier = Modifier,
    flockList: List<Flock>,
    onItemClick: (Int) -> Unit,
    listState: LazyListState,
    onDelete: (Int) -> Unit,
    onClose: (Flock) -> Unit
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
            listState = listState,
            onDelete = onDelete,
            onClose = onClose
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
    onClose: (Flock) -> Unit,
    listState: LazyListState, onDelete: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = listState
    ) {
        itemsIndexed(flockList) { index, flock ->
            FlockCard(flock = flock,
                onItemClick = onItemClick,
                onClose = onClose,
                onDelete = { onDelete(index) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockCard(
    modifier: Modifier = Modifier,
    flock: Flock,
    onItemClick: (Flock) -> Unit,
    onDelete: () -> Unit,
    onClose: (Flock) -> Unit
) {
    var isFlockItemMenuShowing by remember { mutableStateOf(false) }
    var isAlertDialogShowing by remember { mutableStateOf(false) }
    var isCloseAlertDialogShowing by remember { mutableStateOf(false) }


    ShowAlertDialog(
        onDismissAlertDialog = { isCloseAlertDialogShowing = false },
        onDelete = {
            onClose(flock)
            isCloseAlertDialogShowing = false
        },
        isAlertDialogShowing = isCloseAlertDialogShowing,
        title = if (flock.active) "Close Flock Record" else "Reopen Flock Record",
        message = if (flock.active) "Are you sure you want to close this flock record?" else
            "Are you sure you want to reopen this flock record?",
        confirmButtonText = "Yes",
        dismissButtonText = "No"
    )

    OutlinedCard(
        modifier = modifier.padding(8.dp)
            .clickable { onItemClick(flock) },
        elevation = CardDefaults.cardElevation()
    ) {
        Box {
            if (!flock.active) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 16.dp)
                        .rotate(-45f)
                ) {
                    Text(
                        text = "Closed",
                        textAlign = TextAlign.Center,
                        color = Color.Red
                    )
                }
            }
            Column {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Image(
                            modifier = Modifier
                                .size(75.dp)
                                .align(Alignment.CenterHorizontally),
                            painter = painterResource(R.drawable.icon4),
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
                                    flock = flock,
                                    isOverflowMenuExpanded = isFlockItemMenuShowing,
                                    isAlertDialogShowing = isAlertDialogShowing,
                                    onDismissAlertDialog = { isAlertDialogShowing = false },
                                    onShowMenu = {
                                        isFlockItemMenuShowing = true
                                    },
                                    onShowAlertDialog = { isAlertDialogShowing = true },
                                    onDismiss = { isFlockItemMenuShowing = false },
                                    onDelete = {
                                        onDelete()
                                        isAlertDialogShowing = false
                                        isFlockItemMenuShowing = false
                                    },
                                    onClose = {
                                        isFlockItemMenuShowing = false
                                        isCloseAlertDialogShowing = true
                                    },
                                    title = "Delete flock?",
                                    message = "This cannot be undone."
                                )
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
                            if (flock.active) {
                                BaseSingleRowItem(
                                    label = "Age: ",
                                    value = "${
                                        DateUtils().calculateAge(
                                            birthDate = flock.datePlaced
                                        )
                                    } day/s",
                                )
                            }

                            BaseSingleRowItem(
                                label = "Quantity: ",
                                value = (flock.numberOfChicksPlaced + flock.donorFlock).toString()
                            )
                            BaseSingleRowItem(
                                label = "Mortality: ",
                                value = flock.mortality.toString()
                            )

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
}


@Composable
fun ShowOverflowMenu(
    modifier: Modifier = Modifier,
    flock: Flock,
    isOverflowMenuExpanded: Boolean = false,
    isAlertDialogShowing: Boolean = false,
    onDismissAlertDialog: () -> Unit = {},
    onShowMenu: () -> Unit = {},
    onShowAlertDialog: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {},
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
                text = { Text(text = if (flock.active) "Close" else "Reopen") },
                onClick = onClose
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