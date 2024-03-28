package and.drew.nkhukumanagement.userinterface.accounts

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.AccountsWithIncome
import and.drew.nkhukumanagement.data.Income
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.ui.theme.GreenColor
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
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
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import java.time.LocalDate

object IncomeScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "income"
    override val resourceId: Int
        get() = R.string.income
    const val incomeIDArg = "id"
    val routeWithArgs = "$route/{$incomeIDArg}"
    val arguments = listOf(navArgument(incomeIDArg) {
        defaultValue = 1
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IncomeScreen(
    modifier: Modifier = Modifier,
    navigateToAddIncomeScreen: (Int, Int) -> Unit = { _, _ -> },
    incomeViewModel: IncomeViewModel = hiltViewModel(),
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    userPrefsViewModel: UserPrefsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val accountsIdArg by accountsViewModel.accountsID.collectAsState(initial = 0)
    val currency by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )
    val accountsWithIncome by accountsViewModel.accountsWithIncome.collectAsState(
        AccountsWithIncome(
            accountsSummary = AccountsSummary(
                flockUniqueID = "",
                batchName = "",
                totalIncome = 0.0,
                totalExpenses = 0.0,
                variance = 0.0
            )
        )
    )

    MainIncomeScreen(
        navigateToAddIncomeScreen = navigateToAddIncomeScreen,
        deleteIncome = {
            coroutineScope.launch {
                incomeViewModel.deleteIncome(it)
            }
        },
        accountsSummary = accountsWithIncome.accountsSummary,
        incomeList = accountsWithIncome.incomeList,
        accountsIdArg = accountsIdArg,
        updateAccountWhenDeletingIncome = { accountsSummary, income ->
            coroutineScope.launch {
                accountsViewModel.updateAccountWhenDeletingIncome(
                    accountsSummary = accountsSummary,
                    income = income
                )
            }
        },
        currencyLocale = currency.currencyLocale
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainIncomeScreen(
    modifier: Modifier = Modifier,
    navigateToAddIncomeScreen: (Int, Int) -> Unit,
    deleteIncome: (Income) -> Unit,
    accountsSummary: AccountsSummary,
    incomeList: List<Income>,
    accountsIdArg: Int,
    updateAccountWhenDeletingIncome: (AccountsSummary, Income) -> Unit,
    currencyLocale: String
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.semantics { contentDescription = "Add Income" },
                shape = ShapeDefaults.Small,
                elevation = FloatingActionButtonDefaults.elevation(),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = contentColorFor(MaterialTheme.colorScheme.secondary),
                onClick = { navigateToAddIncomeScreen(0, accountsIdArg) }) {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    AnimatedVisibility(visible = listState.isScrollingUp()) {
                        Text(
                            text = stringResource(R.string.income),
                            modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                        )
                    }

                }
            }
        }
    ) { innerPadding ->
        IncomeList(
            modifier = modifier.padding(innerPadding),
            incomeList = incomeList,
            onItemClick = { income ->
                navigateToAddIncomeScreen(income.id, accountsIdArg)
            },
            onDeleteIncome = { income ->
                coroutineScope.launch {
                    updateAccountWhenDeletingIncome(
                        accountsSummary,
                        income
                    )
                    deleteIncome(income)
                }
            },
            currencyLocale = currencyLocale
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
fun IncomeList(
    modifier: Modifier = Modifier,
    incomeList: List<Income>,
    onItemClick: (Income) -> Unit,
    onDeleteIncome: (Income) -> Unit,
    currencyLocale: String
) {
    if (incomeList.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = modifier.align(Alignment.Center),
                text = stringResource(R.string.no_income_recorded),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(incomeList) { index, incomeItem ->
                IncomeCardItem(
                    income = incomeItem,
                    onItemClick = { onItemClick(incomeItem) },
                    onDeleteIncome = { onDeleteIncome(incomeItem) },
                    currencyLocale = currencyLocale
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IncomeCardItem(
    modifier: Modifier = Modifier,
    income: Income,
    onItemClick: () -> Unit = {},
    onDeleteIncome: () -> Unit,
    currencyLocale: String
) {
    var isMenuShowing by remember { mutableStateOf(false) }
    var isAlertDialogShowing by remember { mutableStateOf(false) }
    val incomeUiState = income.toIncomeUiState()
    ElevatedCard(
        modifier = modifier
            .clickable(onClick = onItemClick)
            .border(width = Dp.Hairline, color = Color.Unspecified)
    ) {

        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
            VerticalDivider(
                modifier = Modifier.weight(0.02f).fillMaxHeight(),
                thickness = 4.dp,
                color = GreenColor
            )
            Column(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = incomeUiState.incomeName.split(" ")
                            .joinToString(" ") { it.replaceFirstChar { it.uppercase() } },
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Column(modifier = Modifier.weight(weight = 0.2f)) {
                        OverflowMenu(
                            modifier = Modifier.align(Alignment.End),
                            isOverflowMenuExpanded = isMenuShowing,
                            isAlertDialogShowing = isAlertDialogShowing,
                            onDismissAlertDialog = { isAlertDialogShowing = false },
                            onShowMenu = { isMenuShowing = true },
                            onShowAlertDialog = { isAlertDialogShowing = true },
                            onDismiss = { isMenuShowing = false },
                            onDelete = {
                                onDeleteIncome()
                                isAlertDialogShowing = false
                                isMenuShowing = false
                            },
                            title = stringResource(R.string.delete_income),
                            message = stringResource(R.string.this_cannot_be_undone),
                            dropDownMenuItemLabel = stringResource(R.string.delete_income_label)
                        )
                    }
                }


                BaseAccountRow(
                    labelA = stringResource(R.string.date),
                    weightForLabelA = 0.5f,
                    titleA = incomeUiState.getDate(),
                    weightForTitleA = 1.5f,
                    labelB = stringResource(R.string.customer),
                    titleB = incomeUiState.customer
                )
                BaseAccountRow(
                    labelA = stringResource(R.string.unit_price),
                    titleA = currencyFormatter(
                        incomeUiState.pricePerItem.toDouble(),
                        currencyLocale
                    ),
                    labelB = stringResource(R.string.quantity),
                    titleB = incomeUiState.quantity
                )

                if (incomeUiState.notes.isNotBlank()) {
                    BaseSingleRowItem(
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(R.string.notes),
                        value = incomeUiState.notes,
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
                            incomeUiState.totalIncome.toDouble(),
                            currencyLocale
                        ),
                        color = Color.Green,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(name = "Income Card", showBackground = true)
@Composable
fun IncomeCardPreview() {
    NkhukuManagementTheme {
        IncomeCardItem(
            income = Income(
                date = LocalDate.now(),
                incomeName = "Flock Sale",
                customer = "Shoprite",
                pricePerItem = 100.00,
                quantity = 108,
                totalIncome = 10800.00,
                flockUniqueID = "",
                cumulativeTotalIncome = 10000.25,
                notes = "",
            ), onDeleteIncome = {},
            currencyLocale = "en_ZM"
        )
    }
}