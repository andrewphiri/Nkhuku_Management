package and.drew.nkhukumanagement.userinterface.accounts


import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.utils.AccountDetailsCurrentScreen
import and.drew.nkhukumanagement.utils.BaseSingleRowItem
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.ShowFilterOverflowMenu
import and.drew.nkhukumanagement.utils.currencyFormatter
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AccountsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    userPrefsViewModel: UserPrefsViewModel,
    navigateToTransactionsScreen: (Int) -> Unit,
    contentType: ContentType,
    onClickSettings: () -> Unit
) {
    val accountsSummaryList by accountsViewModel.accountsList.collectAsState()
    val currency by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )

    if (contentType == ContentType.LIST_ONLY) {
        MainAccountsScreen(
            modifier = modifier,
            canNavigateBack = canNavigateBack,
            accountsSummaryList = accountsSummaryList.accountsSummary,
            navigateToTransactionsScreen = navigateToTransactionsScreen,
            onClickSettings = onClickSettings,
            currencyLocale = currency.currencyLocale
        )
    } else {
        AccountsAndDetailsScreen(
            accountsViewModel = accountsViewModel,
            userPrefsViewModel = userPrefsViewModel,
            onClickSettings = {}
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AccountsAndDetailsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    accountsViewModel: AccountsViewModel,
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    incomeViewModel: IncomeViewModel = hiltViewModel(),
    userPrefsViewModel: UserPrefsViewModel,
    onClickSettings: () -> Unit
) {
    val accountsSummaryList by accountsViewModel.accountsList.collectAsState()
    val currency by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )
    var showDetailsPane by rememberSaveable { mutableStateOf(false) }
    var currentScreen by rememberSaveable { mutableStateOf(AccountDetailsCurrentScreen.TRANSACTIONS_SCREEN) }
    var currentPageTransactionScreen by rememberSaveable { mutableStateOf(0) }

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                MainAccountsScreen(
                    modifier = modifier,
                    canNavigateBack = canNavigateBack,
                    accountsSummaryList = accountsSummaryList.accountsSummary,
                    navigateToTransactionsScreen = {
                        showDetailsPane = true
                        accountsViewModel.setAccountsID(it)
                    },
                    onClickSettings = onClickSettings,
                    currencyLocale = currency.currencyLocale
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
                        AccountDetailsCurrentScreen.TRANSACTIONS_SCREEN -> {
                            TransactionScreen(
                                canNavigateBack = false,
                                navigateToAddExpenseScreen = { expenseID, accountID ->
                                    expenseViewModel.setExpenseID(expenseID)
                                    currentScreen = AccountDetailsCurrentScreen.ADD_EXPENSE_SCREEN
                                },
                                navigateToAddIncomeScreen = { incomeID, accountID ->
                                    incomeViewModel.setIncomeID(incomeID)
                                    currentScreen = AccountDetailsCurrentScreen.ADD_INCOME_SCREEN
                                },
                                userPrefsViewModel = userPrefsViewModel,
                                onNavigateUp = {
                                    currentScreen = AccountDetailsCurrentScreen.TRANSACTIONS_SCREEN
                                },
                                initialPage = currentPageTransactionScreen,
                                onPageChanged = {
                                    currentPageTransactionScreen = it
                                }
                            )
                        }

                        AccountDetailsCurrentScreen.ADD_EXPENSE_SCREEN -> {
                            AddExpenseScreen(
                                onNavigateUp = {
                                    currentScreen = AccountDetailsCurrentScreen.TRANSACTIONS_SCREEN
                                },
                                userPrefsViewModel = userPrefsViewModel
                            )
                        }

                        AccountDetailsCurrentScreen.ADD_INCOME_SCREEN -> {
                            AddIncomeScreen(
                                onNavigateUp = {
                                    currentScreen = AccountDetailsCurrentScreen.TRANSACTIONS_SCREEN
                                },
                                userPrefsViewModel = userPrefsViewModel
                            )
                        }
                    }
                }
            }
        }

    }


}
@Composable
fun MainAccountsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    accountsSummaryList: List<AccountsSummary>,
    navigateToTransactionsScreen: (Int) -> Unit,
    onClickSettings: () -> Unit,
    currencyLocale: String
) {
    var accountsList = accountsSummaryList.filter { it.flockActive }
    var isFilterMenuShowing by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Accounts.resourceId),
                isFilterButtonEnabled = accountsSummaryList.isNotEmpty(),
                canNavigateBack = canNavigateBack,
                onClickFilter = {
                    isFilterMenuShowing = !isFilterMenuShowing
                },
                onClickSettings = onClickSettings
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ShowFilterOverflowMenu(
                modifier = Modifier.align(Alignment.End),
                isOverflowMenuExpanded = isFilterMenuShowing,
                onDismiss = { isFilterMenuShowing = false },
                onClickAll = {
                    accountsList = accountsSummaryList
                    isFilterMenuShowing = false
                },
                onClickActive = {
                    accountsList = accountsSummaryList.filter { it.flockActive }
                    isFilterMenuShowing = false
                },
                onClickInactive = {
                    accountsList = accountsSummaryList.filter { !it.flockActive }
                    isFilterMenuShowing = false
                }
            )

            AccountsList(
                modifier = Modifier.semantics { contentDescription = "Accounts list" },
                accountsList = accountsList,
                onItemClick = { accountSummary ->
                    if (accountSummary.flockActive) {
                        navigateToTransactionsScreen(accountSummary.id)
                    }
                },
                currencyLocale = currencyLocale
            )
        }
    }
}

@Composable
fun AccountsList(
    modifier: Modifier = Modifier,
    accountsList: List<AccountsSummary>,
    onItemClick: (AccountsSummary) -> Unit,
    currencyLocale: String
) {
    if (accountsList.isEmpty()) {
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
        LazyColumn(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(accountsList) { index, accountsSummary ->
                SummaryAccountsCard(
                    accountsSummary = accountsSummary,
                    onAccountsClick = { onItemClick(accountsSummary) },
                    currencyLocale = currencyLocale
                )
            }
        }
    }

}

@Composable
fun SummaryAccountsCard(
    modifier: Modifier = Modifier,
    accountsSummary: AccountsSummary,
    onAccountsClick: () -> Unit = {},
    currencyLocale: String
) {
    ElevatedCard(modifier = modifier.clickable(onClick = onAccountsClick)) {

        Box {
            if (!accountsSummary.flockActive) {
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
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = accountsSummary.batchName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )

                Divider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = Dp.Hairline,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    BaseSingleRowItem(
                        label = "Income",
                        value = currencyFormatter(accountsSummary.totalIncome, currencyLocale),
                        styleForLabel = MaterialTheme.typography.bodyMedium,
                        styleForTitle = MaterialTheme.typography.bodyMedium,
                        textAlignA = TextAlign.Center,
                        textAlignB = TextAlign.Center,
                        weightA = 1f,
                        weightB = 1f
                    )
                    BaseSingleRowItem(
                        label = "Expenses",
                        value = currencyFormatter(accountsSummary.totalExpenses, currencyLocale),
                        styleForLabel = MaterialTheme.typography.bodyMedium,
                        styleForTitle = MaterialTheme.typography.bodyMedium,
                        textAlignA = TextAlign.Center,
                        textAlignB = TextAlign.Center,
                        weightA = 1f,
                        weightB = 1f
                    )
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Text(
//                        modifier = Modifier.weight(weight = 1f, fill = true),
//                        text = "Income",
//                        textAlign = TextAlign.Center
//                    )
//                    Text(
//                        modifier = Modifier.weight(weight = 1f, fill = true),
//                        text = accountsSummary.totalIncome.toString(),
//                        textAlign = TextAlign.Center
//                    )
//                }

                    Divider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = Dp.Hairline,
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    BaseSingleRowItem(
                        label = if (accountsSummary.variance > 0)
                            "Profit" else if (accountsSummary.variance < 0) "Loss" else "Break-Even",
                        value = currencyFormatter(accountsSummary.variance, currencyLocale),
                        styleForLabel = MaterialTheme.typography.bodyMedium,
                        styleForTitle = MaterialTheme.typography.bodyMedium,
                        colorForTitle =
                        if (accountsSummary.variance > 0) Color.Green
                        else if (accountsSummary.variance == 0.0) Color.Black
                        else Color.Red,
                        textAlignA = TextAlign.Center,
                        textAlignB = TextAlign.Center,
                        weightA = 1f,
                        weightB = 1f
                    )
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    Text(
//                        modifier = Modifier.weight(weight = 1f, fill = true),
//                        text = "Profit",
//                        fontWeight = FontWeight.Bold,
//                        textAlign = TextAlign.Center
//                    )
//                    Text(
//                        modifier = Modifier.weight(weight = 1f, fill = true),
//                        text = accountsSummary.variance.toString(),
//                        fontWeight = FontWeight.Bold,
//                        textAlign = TextAlign.Center,
//                        color =
//                        if (accountsSummary.totalIncome > accountsSummary.totalExpenses) Color.Green
//                        else if (accountsSummary.totalIncome == accountsSummary.totalExpenses) Color.Black
//                        else Color.Red
//                    )
//                }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AccountsCardPreview() {
    NkhukuManagementTheme {
//        SummaryAccountsCard(
//            accountsSummary =
//            AccountsSummary(
//                flockUniqueID = "", totalExpenses = 2500.0, totalIncome = 2600.0,
//                batchName = "August Batch", variance = 100.0
//            )
//        )
    }
}