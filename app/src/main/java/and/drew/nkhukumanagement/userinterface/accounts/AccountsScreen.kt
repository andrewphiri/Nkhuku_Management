package and.drew.nkhukumanagement.userinterface.accounts


import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.serialization.Serializable


@Serializable object AccountsScreenNav

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
            onClickSettings = {},
            contentType = contentType
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
    onClickSettings: () -> Unit,
    contentType: ContentType
) {
    val accountsSummaryList by accountsViewModel.accountsList.collectAsState()
    val currency by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )
    var showDetailsPane by rememberSaveable { mutableStateOf(false) }
    var currentScreen by rememberSaveable { mutableStateOf(AccountDetailsCurrentScreen.TRANSACTIONS_SCREEN) }
    var currentPageTransactionScreen by rememberSaveable { mutableStateOf(0) }
    var incomeID by rememberSaveable { mutableStateOf(0) }
    var expenseID by rememberSaveable { mutableStateOf(0) }
    var accountID by rememberSaveable { mutableStateOf(0) }

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(0.75f)
            ) {
                MainAccountsScreen(
                    modifier = modifier,
                    canNavigateBack = canNavigateBack,
                    accountsSummaryList = accountsSummaryList.accountsSummary,
                    navigateToTransactionsScreen = {
                        showDetailsPane = true
                        accountID = it
                        accountsViewModel.setAccountsID(it)
                    },
                    onClickSettings = onClickSettings,
                    currencyLocale = currency.currencyLocale,
                    contentType = contentType,
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
                                navigateToAddExpenseScreen = { expenseId, accountId ->
//                                    expenseViewModel.setExpenseID(expenseID)
                                    expenseID = expenseId
                                    accountID = accountId
                                    currentScreen = AccountDetailsCurrentScreen.ADD_EXPENSE_SCREEN
                                },
                                navigateToAddIncomeScreen = { incomeId, accountId ->
//                                    incomeViewModel.setIncomeID(incomeID)
                                    incomeID = incomeId
                                    accountID = accountId
                                    currentScreen = AccountDetailsCurrentScreen.ADD_INCOME_SCREEN
                                },
                                userPrefsViewModel = userPrefsViewModel,
                                onNavigateUp = {
                                    currentScreen = AccountDetailsCurrentScreen.TRANSACTIONS_SCREEN
                                },
                                initialPage = currentPageTransactionScreen,
                                onPageChanged = {
                                    currentPageTransactionScreen = it
                                },
                                contentType = contentType,
                                accountID = accountID
                            )
                        }

                        AccountDetailsCurrentScreen.ADD_EXPENSE_SCREEN -> {
                            AddExpenseScreen(
                                onNavigateUp = {
                                    currentScreen = AccountDetailsCurrentScreen.TRANSACTIONS_SCREEN
                                },
                                userPrefsViewModel = userPrefsViewModel,
                                contentType = contentType,
                                expenseID = expenseID,
                                accountID = accountID
                            )
                        }

                        AccountDetailsCurrentScreen.ADD_INCOME_SCREEN -> {
                            AddIncomeScreen(
                                onNavigateUp = {
                                    currentScreen = AccountDetailsCurrentScreen.TRANSACTIONS_SCREEN
                                },
                                userPrefsViewModel = userPrefsViewModel,
                                contentType = contentType,
                                accountID = accountID,
                                incomeID = incomeID
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
    currencyLocale: String,
    contentType: ContentType = ContentType.LIST_ONLY,
) {
    var accountsList = accountsSummaryList.filter { it.flockActive }
    var isFilterMenuShowing by remember { mutableStateOf(false) }
    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(R.string.accounts),
                isFilterButtonEnabled = accountsSummaryList.isNotEmpty(),
                canNavigateBack = canNavigateBack,
                onClickFilter = {
                    isFilterMenuShowing = !isFilterMenuShowing
                },
                onClickSettings = onClickSettings,
                contentType = contentType
            )
        }
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
                currencyLocale = currencyLocale,
                contentType = contentType
            )
        }
    }
}

@Composable
fun AccountsList(
    modifier: Modifier = Modifier,
    accountsList: List<AccountsSummary>,
    onItemClick: (AccountsSummary) -> Unit,
    currencyLocale: String,
    contentType: ContentType
) {
    var selectedItem by rememberSaveable { mutableStateOf(0) }
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
            modifier = modifier,
        ) {
            itemsIndexed(accountsList) { index, accountsSummary ->
                SummaryAccountsCard(
                    accountsSummary = accountsSummary,
                    onAccountsClick = {
                        selectedItem = it.id
                        onItemClick(accountsSummary)
                    },
                    currencyLocale = currencyLocale,
                    selectedID = selectedItem,
                    contentType = contentType
                )
            }
        }
    }

}

@Composable
fun SummaryAccountsCard(
    modifier: Modifier = Modifier,
    accountsSummary: AccountsSummary,
    onAccountsClick: (AccountsSummary) -> Unit = {},
    currencyLocale: String,
    selectedID: Int,
    contentType: ContentType
) {
    val color = if (selectedID == accountsSummary.id && contentType == ContentType.LIST_AND_DETAIL)
        CardDefaults.outlinedCardColors(containerColor = Color.LightGray) else
        CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ElevatedCard(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = { onAccountsClick(accountsSummary) }),
        colors = color
    ) {

        Box {
            if (!accountsSummary.flockActive) {
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
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = accountsSummary.batchName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = Dp.Hairline,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    BaseSingleRowItem(
                        label = stringResource(R.string.income),
                        value = currencyFormatter(accountsSummary.totalIncome, currencyLocale),
                        styleForLabel = MaterialTheme.typography.bodyMedium,
                        styleForTitle = MaterialTheme.typography.bodyMedium,
                        textAlignA = TextAlign.Center,
                        textAlignB = TextAlign.Center,
                        weightA = 1f,
                        weightB = 1f
                    )
                    BaseSingleRowItem(
                        label = stringResource(R.string.expenses),
                        value = currencyFormatter(accountsSummary.totalExpenses, currencyLocale),
                        styleForLabel = MaterialTheme.typography.bodyMedium,
                        styleForTitle = MaterialTheme.typography.bodyMedium,
                        textAlignA = TextAlign.Center,
                        textAlignB = TextAlign.Center,
                        weightA = 1f,
                        weightB = 1f
                    )

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = Dp.Hairline,
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    BaseSingleRowItem(
                        label = if (accountsSummary.variance > 0)
                            stringResource(R.string.profit) else if (accountsSummary.variance < 0) stringResource(
                            R.string.loss
                        ) else stringResource(R.string.break_even),
                        value = currencyFormatter(accountsSummary.variance, currencyLocale),
                        styleForLabel = MaterialTheme.typography.bodyMedium,
                        styleForTitle = MaterialTheme.typography.bodyMedium,
                        colorForTitle =
                        if (accountsSummary.variance > 0) Color.Green
                        else if (accountsSummary.variance == 0.0) MaterialTheme.colorScheme.onSurface
                        else Color.Red,
                        textAlignA = TextAlign.Center,
                        textAlignB = TextAlign.Center,
                        weightA = 1f,
                        weightB = 1f
                    )

                }
            }
        }
    }
}
