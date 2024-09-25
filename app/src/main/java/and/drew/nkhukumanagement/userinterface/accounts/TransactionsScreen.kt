package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.userinterface.navigation.TabScreens
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.Tabs
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
    navigateToAddIncomeScreen: (Int, Int) -> Unit,
    navigateToAddExpenseScreen: (Int, Int) -> Unit,
    userPrefsViewModel: UserPrefsViewModel,
    initialPage: Int = 0,
    onPageChanged: (Int) -> Unit = {},
    contentType: ContentType
) {
    val tabItems = listOf(TabScreens.Income, TabScreens.Expense)
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f
    ) {
        // provide pageCount
        2
    }
    onPageChanged(pagerState.currentPage)

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            FlockManagementTopAppBar(
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                title = stringResource(TransactionsScreenDestination.resourceId),
                contentType = contentType
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
                navigateToAddExpenseScreen = navigateToAddExpenseScreen,
                userPrefsViewModel = userPrefsViewModel
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
    navigateToAddIncomeScreen: (Int, Int) -> Unit,
    navigateToAddExpenseScreen: (Int, Int) -> Unit,
    userPrefsViewModel: UserPrefsViewModel
) {
    HorizontalPager(
        modifier = Modifier.semantics { contentDescription = "horizontal pager" },
        state = pagerState,
        pageSpacing = 8.dp,
        userScrollEnabled = true,
        reverseLayout = false,
        beyondBoundsPageCount = 0,
        pageSize = PageSize.Fill,
        pageContent = { page ->
            when (page) {
                0 -> {
                    IncomeScreen(
                        navigateToAddIncomeScreen = navigateToAddIncomeScreen,
                        userPrefsViewModel = userPrefsViewModel
                    )
                }

                1 -> {
                    ExpenseScreen(
                        navigateToAddExpenseScreen = navigateToAddExpenseScreen,
                        userPrefsViewModel = userPrefsViewModel
                    )
                }
            }
        }
    )
}