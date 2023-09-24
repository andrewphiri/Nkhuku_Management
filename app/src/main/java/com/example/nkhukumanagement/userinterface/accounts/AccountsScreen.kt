package com.example.nkhukumanagement.userinterface.accounts


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.data.AccountsSummary
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import com.example.nkhukumanagement.utils.BaseSingleRowItem
import com.example.nkhukumanagement.utils.currencyFormatter

@Composable
fun AccountsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    accountsViewModel: AccountsViewModel = hiltViewModel(),
    navigateToTransactionsScreen: (Int) -> Unit
) {
    val accountsSummaryList by accountsViewModel.accountsList.collectAsState()
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(NavigationBarScreens.Accounts.resourceId),
                canNavigateBack = canNavigateBack
            )
        }
    ) { innerPadding ->
        Column { }
        AccountsList(
            modifier = Modifier.padding(innerPadding),
            accountsList = accountsSummaryList.accountsSummary,
            onItemClick = { navigateToTransactionsScreen(it) }
        )
    }
}

@Composable
fun AccountsList(
    modifier: Modifier,
    accountsList: List<AccountsSummary>,
    onItemClick: (Int) -> Unit
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
            itemsIndexed(accountsList) { index, item ->
                SummaryAccountsCard(accountsSummary = item,
                    onAccountsClick = { onItemClick(item.id) })
            }
        }
    }

}

@Composable
fun SummaryAccountsCard(
    modifier: Modifier = Modifier,
    accountsSummary: AccountsSummary,
    onAccountsClick: () -> Unit = {}
) {
    ElevatedCard(modifier = modifier.clickable(onClick = onAccountsClick)) {
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
                    value = currencyFormatter(accountsSummary.totalIncome),
                    styleForLabel = MaterialTheme.typography.bodyMedium,
                    styleForTitle = MaterialTheme.typography.bodyMedium,
                    textAlignA = TextAlign.Center,
                    textAlignB = TextAlign.Center,
                    weightA = 1f,
                    weightB = 1f
                )
                BaseSingleRowItem(
                    label = "Expenses",
                    value = currencyFormatter(accountsSummary.totalExpenses),
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
                    value = currencyFormatter(accountsSummary.variance),
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AccountsCardPreview() {
    NkhukuManagementTheme {
        SummaryAccountsCard(
            accountsSummary =
            AccountsSummary(
                flockUniqueID = "", totalExpenses = 2500.0, totalIncome = 2600.0,
                batchName = "August Batch", variance = 100.0
            )
        )
    }
}