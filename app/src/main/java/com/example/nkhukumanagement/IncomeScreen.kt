package com.example.nkhukumanagement

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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.data.Income
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import java.time.LocalDate
import java.util.Locale

object IncomeScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Inventory
    override val route: String
        get() = "income"
    override val resourceId: Int
        get() = R.string.income
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg) {
        defaultValue = 1
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IncomeScreen(
    modifier: Modifier = Modifier,
    navigateToAddIncomeScreen: (Int) -> Unit = {},
    incomeViewModel: IncomeViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val incomeList by incomeViewModel.accountsWithIncome.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = ShapeDefaults.Small,
                elevation = FloatingActionButtonDefaults.elevation(),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = contentColorFor(MaterialTheme.colorScheme.secondary),
                onClick = { navigateToAddIncomeScreen(incomeViewModel.incomeUiState.id) }) {
                Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    AnimatedVisibility(visible = listState.isScrollingUp()) {
                        Text(
                            text = "Income",
                            modifier = Modifier.padding(start = 8.dp, top = 3.dp)
                        )
                    }

                }
            }
        }
    ) { innerPadding ->
        IncomeList(
            modifier = modifier.padding(innerPadding),
            incomeList = incomeList.incomeList,
            onItemClick = { income ->
                navigateToAddIncomeScreen(income.id)
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
fun IncomeList(
    modifier: Modifier = Modifier,
    incomeList: List<Income>,
    onItemClick: (Income) -> Unit
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
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(incomeList) { index, incomeItem ->
                IncomeCardItem(
                    income = incomeItem,
                    onItemClick = { onItemClick(incomeItem) }
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
    onItemClick: () -> Unit = {}
) {
    val incomeUiState = income.toIncomeUiState()
    ElevatedCard(
        modifier = modifier
            .clickable(onClick = onItemClick)
            .border(width = Dp.Hairline, color = Color.Unspecified)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Max)) {
            Divider(
                modifier = Modifier.weight(0.02f).fillMaxHeight(),
                thickness = 2.dp,
                color = Color(0xFF023020)
            )
            Column(modifier = Modifier.weight(1f).padding(16.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = incomeUiState.incomeName.split(" ").joinToString(" ") { it.uppercase() },
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
                        text = incomeUiState.getDate(),
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Customer",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = incomeUiState.customer,
                        style = MaterialTheme.typography.bodySmall,
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
                        text = incomeUiState.pricePerItem,
                        style = MaterialTheme.typography.bodySmall,
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Quantity",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = incomeUiState.quantity,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (incomeUiState.notes.isNotBlank()) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            text = "NOTES",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth().weight(weight = 1f, fill = true),
                            text = incomeUiState.notes,
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
                        text = incomeUiState.totalIncome,
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
                notes = ""
            )
        )
    }
}