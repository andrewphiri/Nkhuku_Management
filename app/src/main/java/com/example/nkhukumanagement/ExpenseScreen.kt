package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import com.example.nkhukumanagement.data.Expense
import com.example.nkhukumanagement.data.Income
import com.example.nkhukumanagement.ui.theme.Shapes
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations

object ExpenseScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "expense"
    override val resourceId: Int
        get() = R.string.expense
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg) {
        defaultValue = 1
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseScreen(
    navigateToAddExpenseScreen: (Int) -> Unit = {},
    expenseViewModel: ExpenseViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val accountsWithExpense by expenseViewModel.accountsWithExpense.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = ShapeDefaults.Small,
                elevation = FloatingActionButtonDefaults.elevation(),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = contentColorFor(MaterialTheme.colorScheme.secondary),
                onClick = { navigateToAddExpenseScreen(expenseViewModel.expenseUiState.id) }) {
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
                navigateToAddExpenseScreen(expense.id)
            }
        )
    }

}

/**
 * Returns whether the lazy list is currently scrolling up.
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
    onItemClick: (Expense) -> Unit
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
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(expenseList) { index, expenseItem ->
                ExpenseCardItem(
                    expense = expenseItem,
                    onItemClick = { onItemClick(expenseItem) }
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
    onItemClick: () -> Unit = {}
) {
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

            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = expensesUiState.expenseName,
                    style = MaterialTheme.typography.headlineSmall
                )
                Row(
                    modifier = modifier.height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(0.5f),
                        text = "Date",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.weight(1.5f),
                        text = expensesUiState.getDate(),
                        style = MaterialTheme.typography.bodySmall
                    )


                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Supplier",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = expensesUiState.supplier,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = modifier.height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Unit Price",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = expensesUiState.costPerItem,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Quantity",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = expensesUiState.quantity,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (expensesUiState.notes.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            text = "Notes",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth().weight(weight = 3f, fill = true),
                            text = expensesUiState.notes,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = expensesUiState.totalExpense,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

}