package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.AccountsWithExpense
import and.drew.nkhukumanagement.data.Expense
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.BaseAccountRow
import and.drew.nkhukumanagement.utils.BaseSingleRowItem
import and.drew.nkhukumanagement.utils.OverflowMenu
import and.drew.nkhukumanagement.utils.currencyFormatter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.launch

object ExpenseScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "expense"
    override val resourceId: Int
        get() = R.string.expense
    const val expenseIDArg = "id"
    val routeWithArgs = "$route/{$expenseIDArg}"
    val arguments = listOf(navArgument(expenseIDArg) {
        defaultValue = 1
        type = NavType.IntType
    })
}


@Composable
fun ExpenseScreen(
    navigateToAddExpenseScreen: (Int, Int) -> Unit = { _, _ -> },
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    userPrefsViewModel: UserPrefsViewModel,
    accountID: Int
) {
    val coroutineScope = rememberCoroutineScope()
    //val accountsIdArg by accountsViewModel.accountsID.collectAsState(initial = 0)
    val currency by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )

    val accountsWithExpense by accountsViewModel.accountsWithExpense.collectAsState(
        AccountsWithExpense(
            accountsSummary = AccountsSummary(
                flockUniqueID = "",
                batchName = "",
                totalIncome = 0.0,
                totalExpenses = 0.0,
                variance = 0.0
            )
        )
    )

    LaunchedEffect(key1 = accountsWithExpense) {
        accountsViewModel.getAccountsWithExpense(accountID)
    }

    MainExpenseScreen(
        navigateToAddExpenseScreen = navigateToAddExpenseScreen,
        deleteExpense = {
            coroutineScope.launch {
                expenseViewModel.deleteExpense(it)
            }
        },
        accountsSummary = accountsWithExpense?.accountsSummary,
        expenseList = accountsWithExpense?.expenseList ?: emptyList(),
        accountsIdArg = accountID,
        updateAccountWhenDeletingExpense = { accountsSummary, expense ->
            coroutineScope.launch {
                accountsViewModel.updateAccountWhenDeletingExpense(
                    accountsSummary = accountsSummary,
                    expense = expense
                )
            }
        },
        currencyLocale = currency.currencyLocale
    )
}

@Composable
fun MainExpenseScreen(
    navigateToAddExpenseScreen: (Int, Int) -> Unit,
    deleteExpense: (Expense) -> Unit,
    accountsSummary: AccountsSummary?,
    expenseList: List<Expense>,
    accountsIdArg: Int,
    updateAccountWhenDeletingExpense: (AccountsSummary?, Expense) -> Unit,
    currencyLocale: String
) {
    val listState = rememberLazyListState()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.semantics { contentDescription = "Add Expense" },
                shape = ShapeDefaults.Small,
                elevation = FloatingActionButtonDefaults.elevation(),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = contentColorFor(MaterialTheme.colorScheme.secondary),
                onClick = { navigateToAddExpenseScreen(0, accountsIdArg) }) {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    AnimatedVisibility(visible = listState.isScrollingUp()) {
                        Text(
                            text = stringResource(R.string.expense),
                            modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        ExpenseList(
            modifier = Modifier.padding(innerPadding),
            expenseList = expenseList.sortedBy { it.date },
            onItemClick = { expense ->
                navigateToAddExpenseScreen(expense.id, accountsIdArg)
            },
            onDeleteExpense = { expense ->
                updateAccountWhenDeletingExpense(
                    accountsSummary,
                    expense
                )
                deleteExpense(expense)
            },
            currencyLocale = currencyLocale,
            listState = listState
        )
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
fun ExpenseList(
    modifier: Modifier = Modifier,
    expenseList: List<Expense>,
    onItemClick: (Expense) -> Unit,
    onDeleteExpense: (Expense) -> Unit,
    currencyLocale: String,
    listState: LazyListState
) {
    if (expenseList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = modifier.align(Alignment.Center),
                text = stringResource(R.string.no_expense_recorded),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = listState
        ) {
            itemsIndexed(expenseList) { index, expenseItem ->
                ExpenseCardItem(
                    expense = expenseItem,
                    onItemClick = { onItemClick(expenseItem) },
                    onDeleteExpense = { onDeleteExpense(expenseItem) },
                    currencyLocale = currencyLocale
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseCardItem(
    modifier: Modifier = Modifier,
    expense: Expense,
    onItemClick: () -> Unit = {},
    onDeleteExpense: () -> Unit,
    currencyLocale: String
) {
    var isMenuShowing by remember { mutableStateOf(false) }
    var isAlertDialogShowing by remember { mutableStateOf(false) }
    val expensesUiState = expense.toExpenseUiState()
    ElevatedCard(
        modifier = modifier
            .clickable(onClick = onItemClick)
            .border(width = Dp.Hairline, color = Color.Unspecified)
    ) {

        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
            VerticalDivider(
                modifier = Modifier.weight(0.02f).fillMaxHeight(),
                thickness = 4.dp,
                color = Color.Red
            )

            Column(
                modifier = Modifier.weight(1f).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = expensesUiState.expenseName,
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Column(modifier = Modifier.weight(0.2f)) {
                        OverflowMenu(
                            modifier = Modifier.align(Alignment.End),
                            isOverflowMenuExpanded = isMenuShowing,
                            isAlertDialogShowing = isAlertDialogShowing,
                            onDismissAlertDialog = { isAlertDialogShowing = false },
                            onShowMenu = { isMenuShowing = true },
                            onShowAlertDialog = { isAlertDialogShowing = true },
                            onDismiss = { isMenuShowing = false },
                            onDelete = {
                                onDeleteExpense()
                                isAlertDialogShowing = false
                                isMenuShowing = false
                            },
                            title = stringResource(R.string.delete_expense),
                            message = stringResource(R.string.this_cannot_be_undone),
                            dropDownMenuItemLabel = stringResource(R.string.delete_expense_label)
                        )
                    }
                }

                BaseAccountRow(
                    labelA = stringResource(R.string.date),
                    weightForLabelA = 0.5f,
                    titleA = expensesUiState.getDate(),
                    weightForTitleA = 1.5f,
                    labelB = stringResource(R.string.supplier),
                    titleB = expensesUiState.supplier
                )
                 BaseAccountRow(
                     labelA = stringResource(R.string.unit_price),
                     weightForLabelA = 0.5f,
                     weightForTitleA = 1.5f,
                     titleA = currencyFormatter(
                         expensesUiState.costPerItem.toDouble(),
                         currencyLocale
                     ),
                     labelB = stringResource(R.string.quantity),
                     titleB = expensesUiState.quantity
                 )


                if (expensesUiState.notes.isNotBlank()) {
                    BaseSingleRowItem(
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.notes),
                        value = expensesUiState.notes,
                        weightA = 0.2f,
                        weightB = 1f,
                        colorForTitle = Color.Red,
                        styleForTitle = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = currencyFormatter(
                            expensesUiState.totalExpense.toDouble(),
                            currencyLocale
                        ),
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

}