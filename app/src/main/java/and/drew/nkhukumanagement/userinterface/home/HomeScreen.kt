package and.drew.nkhukumanagement.userinterface.home

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockWithVaccinations
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.accounts.AccountsViewModel
import and.drew.nkhukumanagement.userinterface.accounts.ExpenseViewModel
import and.drew.nkhukumanagement.userinterface.accounts.IncomeViewModel
import and.drew.nkhukumanagement.userinterface.feed.FeedScreen
import and.drew.nkhukumanagement.userinterface.feed.FeedViewModel
import and.drew.nkhukumanagement.userinterface.flock.EditFlockViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockEditScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockHealthScreen
import and.drew.nkhukumanagement.userinterface.flock.toFlockUiState
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.userinterface.vaccination.AddVaccinationsScreen
import and.drew.nkhukumanagement.userinterface.vaccination.VaccinationViewModel
import and.drew.nkhukumanagement.userinterface.weight.WeightScreen
import and.drew.nkhukumanagement.userinterface.weight.WeightViewModel
import and.drew.nkhukumanagement.utils.BaseSingleRowItem
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.FlockDetailsCurrentScreen.DETAILS_SCREEN
import and.drew.nkhukumanagement.utils.FlockDetailsCurrentScreen.EDIT_FLOCK_SCREEN
import and.drew.nkhukumanagement.utils.FlockDetailsCurrentScreen.FEED_SCREEN
import and.drew.nkhukumanagement.utils.FlockDetailsCurrentScreen.FLOCK_HEALTH_SCREEN
import and.drew.nkhukumanagement.utils.FlockDetailsCurrentScreen.VACCINATION_SCREEN
import and.drew.nkhukumanagement.utils.FlockDetailsCurrentScreen.WEIGHT_SCREEN
import and.drew.nkhukumanagement.utils.ShowAlertDialog
import and.drew.nkhukumanagement.utils.ShowFilterOverflowMenu
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    contentType: ContentType,
    modifier: Modifier = Modifier,
    navigateToAddFlock: () -> Unit,
    navigateToFlockDetails: (Int) -> Unit,
    onClickSettings: () -> Unit = {},
    homeViewModel: HomeViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel,
    vaccinationViewModel: VaccinationViewModel = hiltViewModel(),
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    detailsViewModel: FlockDetailsViewModel = hiltViewModel(),
    editFlockViewModel: EditFlockViewModel = hiltViewModel(),
    incomeViewModel: IncomeViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    feedViewModel: FeedViewModel = hiltViewModel(),
    weightViewModel: WeightViewModel = hiltViewModel(),
    userPrefsViewModel: UserPrefsViewModel
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val vaccinationList by vaccinationViewModel.flockWithVaccinationsStateFlow.collectAsState(
        initial = FlockWithVaccinations(
            flock = null, vaccinations = listOf()
        )
    )
    var accountSummary: AccountsSummary? by remember {
        mutableStateOf(
            AccountsSummary(
                flockUniqueID = "",
                batchName = "",
                totalIncome = 0.0,
                totalExpenses = 0.0,
                variance = 0.0
            )
        )
    }
    var flockID by rememberSaveable { mutableStateOf(-1) }

    val coroutineScope = rememberCoroutineScope()


    if (contentType == ContentType.LIST_ONLY) {
        MainHomeScreen(
            modifier = modifier,
            contentType = contentType,
            navigateToAddFlock = {
                flockEntryViewModel.resetAll()
                navigateToAddFlock()
            },
            navigateToFlockDetails = navigateToFlockDetails,
            onClickSettings = onClickSettings,
            flocks = homeUiState.flockList,
            resetFlock = {
                flockEntryViewModel.resetAll()
            },
            onOverflowMenuClicked = {
                flockID = it
                vaccinationViewModel.setFlockID(flockID)
            },
            deleteFlock = { index ->
                if (vaccinationList?.vaccinations != null) {
                    vaccinationList?.vaccinations?.forEach { vaccine ->
                        //Log.i("CANCEL_NOTIFICATION", vaccine.toString())
                        vaccinationViewModel.cancelNotification(vaccine)
                    }
                }
                coroutineScope.launch {
                    val uniqueId = homeUiState.flockList[index].uniqueId
                    flockEntryViewModel.deleteFlock(uniqueId)
                    vaccinationViewModel.deleteVaccination(uniqueId)
                    vaccinationViewModel.deleteFeed(uniqueId)
                    vaccinationViewModel.deleteWeight(uniqueId)
                    flockEntryViewModel.deleteFlockHealth(uniqueId)
                    accountsViewModel.deleteAccountsSummary(uniqueId)
                    incomeViewModel.deleteIncome(uniqueId)
                    expenseViewModel.deleteExpense(uniqueId)
                }
            },
            onClose = { flock ->
                if (vaccinationList?.vaccinations != null) {
                    vaccinationList?.vaccinations?.forEach { vaccine ->
                        vaccinationViewModel.cancelNotification(vaccine)
                       // Log.i("VACCINE", vaccine.toString())
                    }
                }
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
    } else {
        HomeScreenListAndDetails(
            modifier = modifier,
            navigateToAddFlock = {
                flockEntryViewModel.resetAll()
                navigateToAddFlock()
            },
            navigateToFlockDetails = {
                detailsViewModel.setFlockID(it)

            },
            onClickSettings = onClickSettings,
            flocks = homeUiState.flockList,
            resetFlock = {
                flockEntryViewModel.resetAll()
            },
            deleteFlock = { index ->
                coroutineScope.launch {
                    val uniqueId = homeUiState.flockList[index].uniqueId
                    if (vaccinationList?.vaccinations != null) {
                        vaccinationList?.vaccinations?.forEach { vaccine ->
                            vaccinationViewModel.cancelNotification(vaccine)
                        }
                    }
                    flockEntryViewModel.deleteFlock(uniqueId)
                    vaccinationViewModel.deleteVaccination(uniqueId)
                    vaccinationViewModel.deleteFeed(uniqueId)
                    vaccinationViewModel.deleteWeight(uniqueId)
                    flockEntryViewModel.deleteFlockHealth(uniqueId)
                    accountsViewModel.deleteAccountsSummary(uniqueId)
                    incomeViewModel.deleteIncome(uniqueId)
                    expenseViewModel.deleteExpense(uniqueId)
                }
            },
            onClose = { flock1 ->
                if (vaccinationList?.vaccinations != null) {
                    vaccinationList?.vaccinations?.forEach { vaccine ->
                        vaccinationViewModel.cancelNotification(vaccine)
                    }
                }
                accountsViewModel.flockRepository.getFlockAndAccountSummary(flock1.id)
                    .observe(lifecycleOwner) { flockAndSummary ->
                        accountSummary = flockAndSummary.accountsSummary
                    }
                coroutineScope.launch {
                    if (flock1.active) {
                        flockEntryViewModel.updateItem(
                            flockUiState = flock1.toFlockUiState().copy(active = false)
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
                            flockUiState = flock1.toFlockUiState().copy(active = true)
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
            },
            canNavigateBack = false,
            onNavigateUp = {},
            flockEntryViewModel = flockEntryViewModel,
            editFlockViewModel = editFlockViewModel,
            userPrefsViewModel = userPrefsViewModel,
            vaccinationViewModel = vaccinationViewModel,
            feedViewModel = feedViewModel,
            weightViewModel = weightViewModel,
            contentType = contentType,
            onOverflowMenuClicked = {
                flockID = it
                vaccinationViewModel.setFlockID(flockID)
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenListAndDetails(
    modifier: Modifier = Modifier,
    navigateToAddFlock: () -> Unit,
    navigateToFlockDetails: (Int) -> Unit,
    onClickSettings: () -> Unit = {},
    flocks: List<Flock>,
    resetFlock: () -> Unit,
    deleteFlock: (Int) -> Unit,
    onClose: (Flock) -> Unit,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    flockEntryViewModel: FlockEntryViewModel,
    editFlockViewModel: EditFlockViewModel,
    userPrefsViewModel: UserPrefsViewModel,
    vaccinationViewModel: VaccinationViewModel,
    feedViewModel: FeedViewModel,
    weightViewModel: WeightViewModel,
    contentType: ContentType,
    onOverflowMenuClicked: (Int) -> Unit
) {
    var showDetailsPane by rememberSaveable { mutableStateOf(false) }
    var currentScreen by rememberSaveable { mutableStateOf(DETAILS_SCREEN) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f),
            ) {
                MainHomeScreen(
                    navigateToAddFlock = navigateToAddFlock,
                    navigateToFlockDetails = {
                        showDetailsPane = true
                        currentScreen = DETAILS_SCREEN
                        navigateToFlockDetails(it)
                    },
                    onClickSettings = onClickSettings,
                    flocks = flocks,
                    resetFlock = resetFlock,
                    deleteFlock = deleteFlock,
                    onClose = onClose,
                    contentType = contentType,
                    onOverflowMenuClicked = onOverflowMenuClicked,
                )
            }

            Spacer(
                modifier = Modifier
                    .weight(0.001f)
                    .fillMaxHeight()
                    .width(Dp.Hairline)
                    .padding(top = 16.dp, bottom = 16.dp)
                    .background(color = MaterialTheme.colorScheme.tertiary),

                )

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                if (showDetailsPane) {
                    when (currentScreen) {
                        DETAILS_SCREEN -> {
                            FlockDetailsScreen(
                                canNavigateBack = canNavigateBack,
                                onNavigateUp = onNavigateUp,
                                navigateToFlockHealthScreen = {
                                    editFlockViewModel.setFlockID(flockID = it)
                                    currentScreen = FLOCK_HEALTH_SCREEN
                                },
                                navigateToVaccinationScreen = {
                                    vaccinationViewModel.setFlockID(it)
                                    currentScreen = VACCINATION_SCREEN
                                },
                                navigateToFeedScreen = {
                                    feedViewModel.setFlockID(it)
                                    currentScreen = FEED_SCREEN
                                },
                                navigateToWeightScreen = {
                                    weightViewModel.setFlockID(it)
                                    currentScreen = WEIGHT_SCREEN
                                },
                                flockEntryViewModel = flockEntryViewModel,
                                contentType = contentType
                            )
                        }

                        FLOCK_HEALTH_SCREEN -> {
                            AnimatedVisibility(
                                visible = true,
                                enter = expandIn(),
                                exit = scaleOut()
                            ) {
                                //editFlockViewModel.setFlockID(flockID)
                                FlockHealthScreen(
                                    onNavigateUp = {
                                        currentScreen = DETAILS_SCREEN
                                    },
                                    navigateToFlockEditScreen = { flockId, healthId ->
                                        editFlockViewModel.setFlockID(flockID = flockId)
                                        editFlockViewModel.setHealthID(healthId = healthId)
                                        currentScreen = EDIT_FLOCK_SCREEN
                                    },
                                    editFlockViewModel = editFlockViewModel,
                                    contentType = contentType
                                )
                            }

                        }

                        WEIGHT_SCREEN -> {
                            AnimatedVisibility(
                                visible = true,
                                enter = expandIn(),
                                exit = scaleOut()
                            ) {
                                WeightScreen(
                                    onNavigateUp = {
                                        currentScreen = DETAILS_SCREEN
                                    },
                                    contentType = contentType
                                )
                            }
                        }

                        FEED_SCREEN -> {
                            AnimatedVisibility(
                                visible = true,
                                enter = expandIn(),
                                exit = scaleOut()
                            ) {
                                FeedScreen(
                                    onNavigateUp = {
                                        currentScreen = DETAILS_SCREEN
                                    },
                                    flockEntryViewModel = flockEntryViewModel,
                                    contentType = contentType
                                )
                            }
                        }

                        VACCINATION_SCREEN -> {
                            AnimatedVisibility(
                                visible = true,
                                enter = expandIn(),
                                exit = scaleOut()
                            ) {
                                AddVaccinationsScreen(
                                    onNavigateUp = {
                                        currentScreen = DETAILS_SCREEN
                                    },
                                    navigateBack = {},
                                    flockEntryViewModel = flockEntryViewModel,
                                    userPrefsViewModel = userPrefsViewModel,
                                    contentType = contentType
                                )
                            }
                        }

                        EDIT_FLOCK_SCREEN -> {
                            FlockEditScreen(
                                flockEntryViewModel = flockEntryViewModel,
                                onNavigateUp = {
                                    currentScreen = FLOCK_HEALTH_SCREEN
                                },
                                editFlockViewModel = editFlockViewModel,
                                contentType = contentType
                            )
                        }
                    }

                }

            }

        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainHomeScreen(
    modifier: Modifier = Modifier,
    navigateToAddFlock: () -> Unit,
    navigateToFlockDetails: (Int) -> Unit,
    onClickSettings: () -> Unit = {},
    flocks: List<Flock>,
    resetFlock: () -> Unit,
    deleteFlock: (Int) -> Unit,
    onClose: (Flock) -> Unit,
    contentType: ContentType,
    onOverflowMenuClicked: (Int) -> Unit,
) {
    var flockList = flocks.filter { it.active }
    val listState = rememberLazyListState()
    var isFilterMenuShowing by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Home.resourceId),
                canNavigateBack = false,
                onClickSettings = onClickSettings,
                isFilterButtonEnabled = flocks.isNotEmpty(),
                onClickFilter = {
                    isFilterMenuShowing = !isFilterMenuShowing
                },
                contentType = contentType
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
                    modifier = Modifier
                        .semantics { contentDescription = "FlockAddition" }
                        .navigationBarsPadding(),
                    onClick = navigateToAddFlock,
                    shape = ShapeDefaults.Small,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    elevation = FloatingActionButtonDefaults.elevation()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_flock),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(innerPadding)
        ) {
            ShowFilterOverflowMenu(
                modifier = Modifier
                    .align(if (contentType == ContentType.LIST_AND_DETAIL)  Alignment.TopCenter.also { Alignment.End }  else Alignment.TopEnd),
                isOverflowMenuExpanded = isFilterMenuShowing,
                onDismiss = { isFilterMenuShowing = false },
                onClickAll = {
                    flockList = flocks
                    isFilterMenuShowing = false
                },
                onClickActive = {
                    flockList = flocks.filter { it.active }
                    isFilterMenuShowing = false
                },
                onClickInactive = {
                    flockList = flocks.filter { !it.active }
                    isFilterMenuShowing = false
                }
            )

            FlockBodyList(
                modifier = modifier,
                flockList = flockList,
                onItemClick = navigateToFlockDetails,
                listState = listState,
                onDelete = { index ->
                    deleteFlock(index)
                },
                onClose = { flock ->
                    onClose(flock)
                },
                contentType = contentType,
                onOverflowMenuClicked = onOverflowMenuClicked
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockBodyList(
    modifier: Modifier = Modifier,
    flockList: List<Flock>,
    onItemClick: (Int) -> Unit,
    listState: LazyListState,
    onDelete: (Int) -> Unit,
    onClose: (Flock) -> Unit,
    contentType: ContentType,
    onOverflowMenuClicked: (Int) -> Unit
) {
    if (flockList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
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
            onClose = onClose,
            contentType = contentType,
            onOverflowMenuClicked = onOverflowMenuClicked
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
    onOverflowMenuClicked: (Int) -> Unit,
    listState: LazyListState,
    onDelete: (Int) -> Unit,
    contentType: ContentType
) {
    var selectedItem by rememberSaveable { mutableStateOf(0) }
    LazyColumn(
        modifier = modifier
            .semantics { contentDescription = "flockList" },
        state = listState
    ) {
        itemsIndexed(flockList) { index, flock ->
            FlockCard(
                flock = flock,
                onItemClick = { flock1 ->
                    selectedItem = flock1.id
                    onItemClick(flock)
                },
                onClose = onClose,
                onDelete = { onDelete(index) },
                selectedID = selectedItem,
                contentType = contentType,
                onOverflowMenuClicked = onOverflowMenuClicked
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
    onOverflowMenuClicked: (Int) -> Unit,
    onClose: (Flock) -> Unit,
    selectedID: Int,
    contentType: ContentType
) {
    var isFlockItemMenuShowing by remember { mutableStateOf(false) }
    var isAlertDialogShowing by remember { mutableStateOf(false) }
    var isCloseAlertDialogShowing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val color = if (selectedID == flock.id && contentType == ContentType.LIST_AND_DETAIL)
        CardDefaults.outlinedCardColors(containerColor = Color.LightGray) else
        CardDefaults.outlinedCardColors(containerColor = Color.Transparent)
    ShowAlertDialog(
        onDismissAlertDialog = { isCloseAlertDialogShowing = false },
        onConfirm = {
            onClose(flock)
            isCloseAlertDialogShowing = false
        },
        isAlertDialogShowing = isCloseAlertDialogShowing,
        title = if (flock.active) stringResource(R.string.close_flock_record) else stringResource(R.string.reopen_flock_record),
        message = if (flock.active) stringResource(R.string.are_you_sure_you_want_to_close_this_flock_record) else
            stringResource(R.string.are_you_sure_you_want_to_reopen_this_flock_record),
        confirmButtonText = stringResource(R.string.yes),
        dismissButtonText = stringResource(R.string.no)
    )

    OutlinedCard(
        modifier = modifier.padding(8.dp)
            .clickable {
                onItemClick(flock)
            },
        elevation = CardDefaults.cardElevation(),
        colors = color
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
                        text = stringResource(R.string.closed),
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
                            painter = painterResource(R.drawable.add_flock_placeholder),
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
                                label = stringResource(R.string.name),
                                value = flock.batchName,
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
                                        onOverflowMenuClicked(it)
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
                                    title = stringResource(R.string.delete_flock),
                                    message = stringResource(R.string.this_cannot_be_undone)
                                )
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            BaseSingleRowItem(
                                label = stringResource(R.string.breed),
                                value = flock.breed
                            )
                            BaseSingleRowItem(
                                label = stringResource(R.string.date),
                                value = DateUtils().dateToStringLongFormat(flock.datePlaced)
                            )
                            if (flock.active) {
                                BaseSingleRowItem(
                                    label = stringResource(R.string.age),
                                    value = context.resources.getQuantityString(
                                        R.plurals.day_s, DateUtils().calculateAge(
                                            birthDate = flock.datePlaced
                                        ).toInt(), DateUtils().calculateAge(
                                            birthDate = flock.datePlaced
                                        )
                                    ),
                                )
                            }

                            BaseSingleRowItem(
                                label = stringResource(R.string.quantity),
                                value = (flock.numberOfChicksPlaced + flock.donorFlock).toString()
                            )
                            BaseSingleRowItem(
                                label = stringResource(R.string.mortality),
                                value = flock.mortality.toString()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "${stringResource(R.string.stock)}: ${(flock.stock)}",
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
    onShowMenu: (Int) -> Unit = {},
    onShowAlertDialog: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {},
    title: String,
    message: String
) {
    ShowAlertDialog(
        onDismissAlertDialog = onDismissAlertDialog,
        onConfirm = onDelete,
        isAlertDialogShowing = isAlertDialogShowing,
        title = title,
        message = message
    )
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(
            onClick = { onShowMenu(flock.id) }
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
                text = { Text(stringResource(R.string.delete)) },
                onClick = onShowAlertDialog
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = if (flock.active) stringResource(R.string.close) else stringResource(
                            R.string.reopen
                        )
                    )
                },
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