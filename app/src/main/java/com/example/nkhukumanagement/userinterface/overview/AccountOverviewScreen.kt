package com.example.nkhukumanagement.userinterface.overview

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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.data.Account
import com.example.nkhukumanagement.userinterface.home.HomeViewModel
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import com.example.nkhukumanagement.utils.BaseSingleRowItem
import com.example.nkhukumanagement.utils.DropDownMenuDialog
import com.example.nkhukumanagement.utils.PieChart
import com.example.nkhukumanagement.utils.currencyFormatter


object AccountOverviewDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.AttachMoney
    override val route: String
        get() = "Account Overview"
    override val resourceId: Int
        get() = R.string.account_overview
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg) {
        defaultValue = 1
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AccountOverviewScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val overviewUiState by overviewViewModel.accountsList.collectAsState()
    val flockList by homeViewModel.homeUiState.collectAsState()
    val flockOptions: MutableMap<String, String> = mutableMapOf("All" to "All flock")

    var playAnimation by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var isExpanded by remember { mutableStateOf(false) }
    var defaultDropDownMenuValue by remember { mutableStateOf(flockOptions["All"] ?: "") }

    flockList.flockList.forEach {
        flockOptions[it.uniqueId] = it.batchName
    }
    // Filter the list based on the batch picked
    //Use the batch name(value) to get the key(unique ID) and compare it to the Account Summary unique ID
    val accountList: List<Account> = if (defaultDropDownMenuValue == flockOptions["All"])
        overviewViewModel.accountsTotalsList(overviewUiState.accountsList) else
        overviewViewModel.accountsTotalsList(overviewUiState.accountsList
            .filter { it.flockUniqueID == flockOptions.entries.find { it.value == defaultDropDownMenuValue }?.key })


    LaunchedEffect(accountList) {
        if (overviewUiState.accountsList.isNotEmpty()) {
            playAnimation = true
        }
    }
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(AccountOverviewDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
    ) { innerPadding ->
        if (overviewUiState.accountsList.isEmpty()) {
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
                OverviewAccountsCard(
                    totalAccountsList = accountList,
                    playAnimation = playAnimation,
                    value = defaultDropDownMenuValue,
                    isExpanded = isExpanded,
                    onExpanded = { isExpanded = !isExpanded },
                    onDismissed = { isExpanded = false },
                    onValueChanged = {
                        defaultDropDownMenuValue = it
                    },
                    flockOptions = flockOptions.values.toList()
                )
            }
        }
    }
}

@Composable
fun OverviewAccountsCard(
    modifier: Modifier = Modifier,
    totalAccountsList: List<Account>,
    playAnimation: Boolean,
    isExpanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    value: String,
    onValueChanged: (String) -> Unit,
    onDismissed: () -> Unit,
    flockOptions: List<String>
) {
    val netProfitMargin = if (totalAccountsList[0].amount == 0.0) 0.0 else
        (totalAccountsList[0].net / totalAccountsList[0].amount) * 100
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
            label = "Flock"
        )
        PieChart(
            modifier = Modifier.padding(16.dp),
            input = totalAccountsList,
            animationPlayed = playAnimation
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            color = totalAccountsList[0].color,
                            shape = RoundedCornerShape(3.dp)
                        )
                        .size(20.dp)
                )
                BaseSingleRowItem(
                    modifier = Modifier.padding(start = 8.dp),
                    label = totalAccountsList[0].description,
                    value = currencyFormatter(totalAccountsList[0].amount),
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
                            color = totalAccountsList[1].color,
                            shape = RoundedCornerShape(3.dp)
                        )
                        .size(20.dp)
                )
                BaseSingleRowItem(
                    modifier = Modifier.padding(start = 8.dp),
                    label = totalAccountsList[1].description,
                    value = currencyFormatter(totalAccountsList[1].amount),
                    styleForLabel = MaterialTheme.typography.bodyMedium,
                    styleForTitle = MaterialTheme.typography.bodyMedium,
                    weightA = 1f,
                    textAlignB = TextAlign.End
                )
            }


            Divider(
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
                    label = "Net",
                    styleForLabel = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    styleForTitle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    value = currencyFormatter(totalAccountsList[0].net),
                    weightA = 1f,
                    textAlignB = TextAlign.End
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                )
                BaseSingleRowItem(
                    modifier = Modifier.padding(start = 8.dp),
                    label = "Net Profit Margin",
                    styleForLabel = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    styleForTitle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    value = "${String.format("%.2f", netProfitMargin)} %",
                    weightA = 1f,
                    textAlignB = TextAlign.End
                )
            }
        }
    }

}