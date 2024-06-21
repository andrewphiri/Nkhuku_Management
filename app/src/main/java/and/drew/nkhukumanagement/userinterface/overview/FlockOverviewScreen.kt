package and.drew.nkhukumanagement.userinterface.overview

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.Account
import and.drew.nkhukumanagement.data.Eggs
import and.drew.nkhukumanagement.data.EggsSummary
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.ui.theme.GreenColor
import and.drew.nkhukumanagement.ui.theme.lightBrown
import and.drew.nkhukumanagement.ui.theme.orange
import and.drew.nkhukumanagement.ui.theme.sapphireBlue
import and.drew.nkhukumanagement.userinterface.home.HomeViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.BaseSingleRowItem
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DropDownMenuDialog
import and.drew.nkhukumanagement.utils.PieChart
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument

object FlockOverviewDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "Flock Overview"
    override val resourceId: Int
        get() = R.string.flock_overview
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg) {
        defaultValue = 1
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockOverviewScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    contentType: ContentType
) {
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val eggSummaryList by overviewViewModel.eggSummaryList.collectAsState()

    MainFlockOverviewScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        onNavigateUp = onNavigateUp,
        flocks = homeUiState.flockList,
        setFlockTotals = {
            overviewViewModel.flockTotalsList(it)
        },
        contentType = contentType,
        eggsSummary = eggSummaryList
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainFlockOverviewScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    flocks: List<Flock>,
    setFlockTotals: (List<Flock>) -> List<Account>,
    contentType: ContentType,
    eggsSummary: List<EggsSummary>?
) {
    //val homeUiState by homeViewModel.homeUiState.collectAsState()
    val context = LocalContext.current
    val flockOptions: MutableMap<String, String> = mutableMapOf(
        context.getString(R.string.all) to context.getString(
            R.string.all_flock
        )
    )
    var playAnimation by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var isExpanded by remember { mutableStateOf(false) }
    var defaultDropDownMenuValue by remember {
        mutableStateOf(
            flockOptions[context.getString(R.string.all)] ?: ""
        )
    }

    flocks.forEach {
        flockOptions[it.uniqueId] = it.batchName
    }
    // Filter the list based on the batch picked
    //Use the batch name(value) to get the key(unique ID) and compare it to the Account Summary unique ID
    val flockList: List<Account> =
        if (defaultDropDownMenuValue == flockOptions[context.getString(R.string.all)])
            setFlockTotals(flocks)
         else
            setFlockTotals(flocks.filter {
                it.uniqueId == flockOptions.entries.find { it.value == defaultDropDownMenuValue }?.key
            })


    val eggsSummaryList: List<EggsSummary>? =
        if (defaultDropDownMenuValue == flockOptions[context.getString(R.string.all)])
            eggsSummary
         else
            eggsSummary?.filter {
                it.flockUniqueID == flockOptions.entries.find { it.value == defaultDropDownMenuValue }?.key
            }


    LaunchedEffect(flockList) {
        if (flocks.isNotEmpty()) {
            playAnimation = true
        }
    }

    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(FlockOverviewDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                contentType = contentType
            )
        },
    ) { innerPadding ->
        if (flocks.isEmpty()) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(R.string.no_records),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(innerPadding)
                    .verticalScroll(state = scrollState, enabled = true),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OverviewFlockCard(
                    totalFlockList = flockList,
                    playAnimation = playAnimation,
                    value = defaultDropDownMenuValue,
                    isExpanded = isExpanded,
                    onExpanded = { isExpanded = !isExpanded },
                    onDismissed = { isExpanded = false },
                    onValueChanged = {
                        defaultDropDownMenuValue = it
                    },
                    flockOptions = flockOptions.values.toList(),
                    eggsSummary = eggsSummaryList
                )
            }
        }
    }
}

@Composable
fun OverviewFlockCard(
    modifier: Modifier = Modifier,
    totalFlockList: List<Account>,
    playAnimation: Boolean,
    isExpanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    value: String,
    onValueChanged: (String) -> Unit,
    onDismissed: () -> Unit,
    flockOptions: List<String>,
    eggsSummary: List<EggsSummary>?
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DropDownMenuDialog(
            value = value,
            expanded = isExpanded,
            onExpand = onExpanded,
            onOptionSelected = onValueChanged,
            onDismissed = onDismissed,
            options = flockOptions,
            label = stringResource(R.string.flock)
        )

        PieChart(
            modifier = Modifier.padding(16.dp),
            input = totalFlockList,
            animationPlayed = playAnimation
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            color = totalFlockList[0].color,
                            shape = RoundedCornerShape(3.dp)
                        )
                        .size(20.dp)
                )
                BaseSingleRowItem(
                    modifier = Modifier.padding(start = 8.dp),
                    label = stringResource(R.string.healthy_birds),
                    value = totalFlockList[0].amount.toInt().toString(),
                    styleForLabel = MaterialTheme.typography.bodyMedium,
                    styleForTitle = MaterialTheme.typography.bodyMedium,
                    weightA = 1f,
                    textAlignB = TextAlign.End
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            color = totalFlockList[1].color,
                            shape = RoundedCornerShape(3.dp)
                        )
                        .size(20.dp)
                )
                BaseSingleRowItem(
                    modifier = Modifier.padding(start = 8.dp),
                    label = stringResource(R.string.mortality),
                    value = totalFlockList[1].amount.toInt().toString(),
                    styleForLabel = MaterialTheme.typography.bodyMedium,
                    styleForTitle = MaterialTheme.typography.bodyMedium,
                    weightA = 1f,
                    textAlignB = TextAlign.End
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            color = totalFlockList[2].color,
                            shape = RoundedCornerShape(3.dp)
                        )
                        .size(20.dp)
                )
                BaseSingleRowItem(
                    modifier = Modifier.padding(start = 8.dp),
                    label = stringResource(R.string.culls),
                    value = totalFlockList[2].amount.toInt().toString(),
                    styleForLabel = MaterialTheme.typography.bodyMedium,
                    styleForTitle = MaterialTheme.typography.bodyMedium,
                    weightA = 1f,
                    textAlignB = TextAlign.End
                )
            }

            if (eggsSummary != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = lightBrown,
                                shape = RoundedCornerShape(3.dp)
                            )
                            .size(20.dp)
                    )
                    BaseSingleRowItem(
                        modifier = Modifier.padding(start = 8.dp),
                        label = stringResource(R.string.good_eggs),
                        value = eggsSummary.sumOf { it.totalGoodEggs }.toString(),
                        styleForLabel = MaterialTheme.typography.bodyMedium,
                        styleForTitle = MaterialTheme.typography.bodyMedium,
                        weightA = 1f,
                        textAlignB = TextAlign.End
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = orange,
                                shape = RoundedCornerShape(3.dp)
                            )
                            .size(20.dp)
                    )
                    BaseSingleRowItem(
                        modifier = Modifier.padding(start = 8.dp),
                        label = stringResource(R.string.bad_eggs),
                        value = eggsSummary.sumOf { it.totalBadEggs }.toString(),
                        styleForLabel = MaterialTheme.typography.bodyMedium,
                        styleForTitle = MaterialTheme.typography.bodyMedium,
                        weightA = 1f,
                        textAlignB = TextAlign.End
                    )
                }
            }




            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = Dp.Hairline
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                )
                BaseSingleRowItem(
                    modifier = Modifier.padding(start = 8.dp),
                    label = stringResource(R.string.total_ordered),
                    styleForLabel = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    styleForTitle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    value = totalFlockList[0].total.toInt().toString(),
                    weightA = 1f,
                    textAlignB = TextAlign.End
                )

            }
        }
    }
}
