package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.Expense
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseScreen(
    navigateToAddExpenseScreen: (Int,Int) -> Unit = { _, _ ->},
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
    accountsViewModel: AccountsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val accountsWithExpense by accountsViewModel.accountsWithExpense.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = ShapeDefaults.Small,
                elevation = FloatingActionButtonDefaults.elevation(),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = contentColorFor(MaterialTheme.colorScheme.secondary),
                onClick = { navigateToAddExpenseScreen(0,accountsViewModel.id) }) {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    AnimatedVisibility(visible = listState.isScrollingUp()) {
                        Text(
                            text = "Expense",
                            modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                        )
                    }

                }
            }
        }
    ) { innerPadding ->
        ExpenseList(
            modifier = Modifier.padding(innerPadding),
            expenseList = accountsWithExpense.expenseList,
            onItemClick = { expense ->
                navigateToAddExpenseScreen(expense.id, accountsViewModel.id)
            },
            onDeleteExpense = { expense ->
                coroutineScope.launch {
                   accountsViewModel.updateAccountWhenDeletingExpense(
                       accountsSummary = accountsWithExpense.accountsSummary,
                       expense = expense
                   )
                    expenseViewModel.deleteExpense(expense)
                }
            }
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
    onDeleteExpense: (Expense) -> Unit
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(expenseList) { index, expenseItem ->
                ExpenseCardItem(
                    expense = expenseItem,
                    onItemClick = { onItemClick(expenseItem) },
                    onDeleteExpense = { onDeleteExpense(expenseItem) }
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
    onDeleteExpense: () -> Unit
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
            Divider(
                modifier = Modifier.weight(0.02f).fillMaxHeight(),
                thickness = 2.dp,
                color = Color.Red
            )

            Column(
                modifier = Modifier.weight(1f).padding(16.dp),
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
                            title = "Delete expense?",
                            message = "This cannot be undone.",
                            dropDownMenuItemLabel = "Delete expense"
                        )
                    }
                }

                BaseAccountRow(
                    labelA = "Date",
                    weightForLabelA = 0.5f,
                    titleA = expensesUiState.getDate(),
                    weightForTitleA = 1.5f,
                    labelB = "Suppler",
                    titleB = expensesUiState.supplier
                )
                 BaseAccountRow(
                     labelA = "Unit Price",
                     titleA = currencyFormatter(expensesUiState.costPerItem.toDouble()),
                     labelB = "Quantity",
                     titleB = expensesUiState.quantity
                 )


                if (expensesUiState.notes.isNotBlank()) {

                    BaseSingleRowItem(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Notes",
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
                        text = currencyFormatter(expensesUiState.totalExpense.toDouble()),
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

}