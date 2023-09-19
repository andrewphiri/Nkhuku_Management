package com.example.nkhukumanagement


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.res.stringResource
import com.example.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nkhukumanagement.data.Account
import com.example.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import com.example.nkhukumanagement.userinterface.home.HomeViewModel
import com.example.nkhukumanagement.utils.BaseSingleRowItem
import com.example.nkhukumanagement.utils.PieChart

@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val overviewUiState by overviewViewModel.accountsList.collectAsState()
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val accountList: List<Account> = overviewViewModel.accountsTotalsList(overviewUiState.accountsList)
    val flockList: List<Account> = overviewViewModel.flockTotalsList(homeUiState.flockList)
    var playAnimation by remember { mutableStateOf(false) }


    LaunchedEffect(accountList) {
        if (accountList.isNotEmpty()) {
            playAnimation = true
        }
    }
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Overview.resourceId),
                canNavigateBack = canNavigateBack
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            OverviewCard(
                totalAccountsList = overviewViewModel.accountsTotalsList(overviewUiState.accountsList),
                playAnimation = playAnimation,
                totalFlockList = flockList
            )
        }

    }
}

@Composable
fun OverviewCard(
    modifier: Modifier = Modifier,
    totalAccountsList: List<Account>,
    playAnimation: Boolean,
    totalFlockList: List<Account>
) {
    if (totalAccountsList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = modifier.align(Alignment.Center),
                text = stringResource(R.string.no_records),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyVerticalGrid(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            columns = GridCells.Fixed(1)
        ) {
            item {
                OverviewAccountsCard(
                    totalAccountsList = totalAccountsList,
                    playAnimation = playAnimation
                )
            }
            item {
                OverviewFlockCard(
                    totalFlockList = totalFlockList,
                    playAnimation = playAnimation
                )
            }
        }
    }
}

@Composable
fun OverviewAccountsCard(
    totalAccountsList: List<Account>,
    playAnimation: Boolean
) {
    OutlinedCard(modifier = Modifier.padding(16.dp)) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            text = "Accounts",
            style = MaterialTheme.typography.headlineMedium
        )

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dp.Hairline)

        Column (
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
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
                        value = totalAccountsList[0].amount.toString(),
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
                        value = totalAccountsList[1].amount.toString(),
                        styleForLabel = MaterialTheme.typography.bodyMedium,
                        styleForTitle = MaterialTheme.typography.bodyMedium,
                        weightA = 1f,
                        textAlignB = TextAlign.End
                    )
                }


                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = Dp.Hairline)

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
                        value = totalAccountsList[0].net.toString(),
                        weightA = 1f,
                        textAlignB = TextAlign.End
                    )
                }
            }
        }
    }
}

@Composable
fun OverviewFlockCard(
    totalFlockList: List<Account>,
    playAnimation: Boolean
) {
    OutlinedCard(modifier = Modifier.padding(16.dp)) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            text = "Flock Summary",
            style = MaterialTheme.typography.headlineMedium
        )

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = Dp.Hairline)

        Column (
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
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
                        label = totalFlockList[0].description,
                        value = totalFlockList[0].amount.toString(),
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
                        label = totalFlockList[1].description,
                        value = totalFlockList[1].amount.toString(),
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
                        label = totalFlockList[2].description,
                        value = totalFlockList[2].amount.toString(),
                        styleForLabel = MaterialTheme.typography.bodyMedium,
                        styleForTitle = MaterialTheme.typography.bodyMedium,
                        weightA = 1f,
                        textAlignB = TextAlign.End
                    )
                }

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = Dp.Hairline)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                    )
                    BaseSingleRowItem(
                        modifier = Modifier.padding(start = 8.dp),
                        label = "Total Ordered",
                        styleForLabel = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        styleForTitle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        value = totalFlockList[0].total.toString(),
                        weightA = 1f,
                        textAlignB = TextAlign.End
                    )
                }
            }
        }
    }
}