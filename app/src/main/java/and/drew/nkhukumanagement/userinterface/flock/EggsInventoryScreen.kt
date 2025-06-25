package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.data.Eggs
import and.drew.nkhukumanagement.data.FlockWithEggs
import androidx.compose.material.icons.filled.Egg
import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.EggsSummary
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockAndEggsSummary
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DateUtils
import and.drew.nkhukumanagement.utils.ShowOverflowMenu
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.LocalDate


object EggsInventoryScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Egg
    override val route: String
        get() = "Eggs Inventory"
    override val resourceId: Int
        get() = R.string.flock
    const val idArg = "id_egg"
    val routeWithArgs = "$route/{$idArg}"
    val arguments = listOf(navArgument(idArg) {
        defaultValue = 0
        type = NavType.IntType
    })
}

@Serializable
data class EggsInventoryScreenNav(
    val flockId: Int,
    val eggId: Int
)

@Composable
fun EggsInventoryScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    eggId: Int,
    flockID: Int,
    navigateToEggsEditScreen: (Int, Int) -> Unit,
    editEggsViewModel: EggsInventoryViewModel = hiltViewModel(),
    contentType: ContentType
) {
    val coroutineScope = rememberCoroutineScope()
    val flockWithEggs by editEggsViewModel.flockWithEggs.collectAsState(
        initial = FlockWithEggs(
            flock = FlockUiState(
                datePlaced = DateUtils().dateToStringLongFormat(LocalDate.now()),
                quantity = "0",
                donorFlock = "0",
                cost = "0"
            ).toFlock(), eggs = listOf()
        )
    )

    val flockAndEggsSummary by editEggsViewModel
        .flockAndEggsSummaryStateFlow
        .collectAsState(
            initial = FlockAndEggsSummary(flock = null,
                eggsSummary = EggsSummary(
                    flockUniqueID = "",
                    totalGoodEggs = 0,
                    totalBadEggs = 0,
                    date = LocalDate.now()
                )
            )
        )

    LaunchedEffect(key1 = eggId) {
        editEggsViewModel.getFlockWithEggs(flockID)
        editEggsViewModel.getFlockAndEggsSummary(flockID)
    }
    //val flockID by editEggsViewModel.flockId.collectAsState(initial = 0)

    val title = flockWithEggs?.flock?.batchName
    MainEggsScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        onNavigateUp = onNavigateUp,
        navigateToEggsEditScreen = { flockId, eggsID ->

            navigateToEggsEditScreen(flockID, eggsID)
        },
        eggsList = flockWithEggs?.eggs?.sortedBy { it.date },
        flockId = flockWithEggs?.flock?.id,
        contentType = contentType,
        title = title ?: stringResource(FlockHealthScreenDestination.resourceId),
        onDelete = { egg ->
                coroutineScope.launch {
                    flockAndEggsSummary?.eggsSummary?.let {
                        EggsSummary(
                            id = it.id,
                            flockUniqueID = it.flockUniqueID,
                            totalGoodEggs = it.totalGoodEggs - egg.goodEggs,
                            totalBadEggs = it.totalBadEggs - egg.badEggs,
                            date = egg.date
                        )
                    }?.let {
                        editEggsViewModel.updateEggsSummary(
                            it
                        )
                    }
                    editEggsViewModel.deleteEggs(egg)
                }
        },
        flock = flockWithEggs?.flock ?: FlockUiState(
            datePlaced = DateUtils().dateToStringLongFormat(LocalDate.now()),
            quantity = "0",
            donorFlock = "0",
            cost = "0"
        ).toFlock()
    )
}

@Composable
fun MainEggsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    navigateToEggsEditScreen: (Int?, Int) -> Unit,
    eggsList: List<Eggs>?,
    flock: Flock,
    onDelete: (Eggs) -> Unit,
    flockId: Int?,
    contentType: ContentType,
    title: String
) {
    BackHandler {
        onNavigateUp()
    }
    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = title,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
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
                    onClick = { navigateToEggsEditScreen(flockId, 0) },
                    modifier = Modifier
                        .semantics { contentDescription = "Add eggs fab" }
                        .navigationBarsPadding(),
                    shape = ShapeDefaults.Small,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    elevation = FloatingActionButtonDefaults.elevation()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add or reduce eggs",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
                EggsList(
                    onItemClick = { eggId ->
                        navigateToEggsEditScreen(flockId, eggId)
                    },
                    eggsList = eggsList,
                    onDelete = onDelete,
                    flock = flock
                )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EggsList(
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit,
    flock: Flock,
    eggsList: List<Eggs>?,
    onDelete: (Eggs) -> Unit
) {
    if (eggsList.isNullOrEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.no_records_add_eggs),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier.weight(1.5f),
                    text = stringResource(R.string.date),
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.weight(1f, fill = true),
                    text = stringResource(R.string.good_eggs),
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(
                    modifier = Modifier.weight(0.01f).fillMaxHeight(),
                    thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    modifier = Modifier.weight(1f, fill = true),
                    text = stringResource(R.string.bad_eggs),
                    textAlign = TextAlign.Center
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                itemsIndexed(eggsList) { index, eggItem ->
                    EggsInventoryCard(
                        eggs = eggItem,
                        onItemClick = { onItemClick(eggItem.id) },
                        flock = flock,
                        onDelete = onDelete
                    )
                }
            }
        }
    }

}

/**
 * Composable function to detect scroll position of list
 * This will be used to hide FAB when scrolling down, and to Show FAB when scrolling up
 */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
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
fun EggsInventoryCard(
    modifier: Modifier = Modifier,
    eggs: Eggs,
    flock: Flock,
    onItemClick: () -> Unit,
    onDelete: (Eggs) -> Unit
) {

    var isEggItemMenuShowing by remember { mutableStateOf(false) }
    var isAlertDialogShowing by remember { mutableStateOf(false) }
    var isCloseAlertDialogShowing by remember { mutableStateOf(false) }

    Card(modifier = modifier.clickable(onClick = onItemClick)) {
        Row(
            modifier = Modifier.padding(16.dp).height(intrinsicSize = IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(weight = 1.5f, fill = true),
                text = DateUtils().dateToStringShortFormat(eggs.date),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.weight(1f, fill = true),
                text = eggs.goodEggs.toString(),
                textAlign = TextAlign.Center
            )

            HorizontalDivider(
                modifier = Modifier.weight(0.01f).fillMaxHeight(),
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                modifier = Modifier.weight(1f, fill = true),
                text = eggs.badEggs.toString(),
                textAlign = TextAlign.Center
            )

            ShowOverflowMenu(
                flock = flock,
                isOverflowMenuExpanded = isEggItemMenuShowing,
                isAlertDialogShowing = isAlertDialogShowing,
                onDismissAlertDialog = { isAlertDialogShowing = false },
                onShowMenu = {
                    isEggItemMenuShowing = true
                },
                onShowAlertDialog = { isAlertDialogShowing = true },
                onDismiss = { isEggItemMenuShowing = false },
                onDelete = {
                    onDelete(eggs)
                    isAlertDialogShowing = false
                    isEggItemMenuShowing = false
                },
                onClose = {
                    isEggItemMenuShowing = false
                    isCloseAlertDialogShowing = true
                },
                title = stringResource(R.string.delete_egg),
                message = stringResource(R.string.this_cannot_be_undone),
                showCloseButton = false
            )
        }
    }

}