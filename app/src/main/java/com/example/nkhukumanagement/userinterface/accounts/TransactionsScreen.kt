package com.example.nkhukumanagement.userinterface.accounts

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import com.example.nkhukumanagement.userinterface.navigation.TabScreens
import kotlinx.coroutines.launch

object TransactionsScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Calculate
    override val route: String
        get() = "transactions"
    override val resourceId: Int
        get() = R.string.transactions
    const val accountIdArg = "accountId"
    val routeWithArgs = "$route/{$accountIdArg}"
    val arguments = listOf(navArgument(accountIdArg) {
        defaultValue = 0
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    navigateToAddIncomeScreen: (Int,Int) -> Unit,
    navigateToAddExpenseScreen: (Int, Int) -> Unit
) {
    val tabItems = listOf(TabScreens.Income, TabScreens.Expense)
    var pagerState = rememberPagerState()
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                title = stringResource(TransactionsScreenDestination.resourceId)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Tabs(
                tabs = tabItems,
                pagerState = pagerState
            )
            TabScreenContent(
                tabs = tabItems,
                pagerState = pagerState,
                navigateToAddIncomeScreen = navigateToAddIncomeScreen,
                navigateToAddExpenseScreen = navigateToAddExpenseScreen
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(tabs: List<TabScreens>, pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()
    TabRow(selectedTabIndex = pagerState.currentPage) {
        tabs.forEachIndexed { index, tabItem ->
            Tab(
                text = { Text(tabItem.title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabScreenContent(
    tabs: List<TabScreens>,
    pagerState: PagerState,
    navigateToAddIncomeScreen: (Int,Int) -> Unit,
    navigateToAddExpenseScreen: (Int, Int) -> Unit
) {

    HorizontalPager(pageCount = tabs.size, state = pagerState) { page ->
        when (page) {
            0 -> {
                IncomeScreen(navigateToAddIncomeScreen = navigateToAddIncomeScreen)
            }

            1 -> {
                ExpenseScreen(navigateToAddExpenseScreen = navigateToAddExpenseScreen)
            }
        }
    }
}