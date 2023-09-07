package com.example.nkhukumanagement

import android.graphics.fonts.FontStyle
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.ui.theme.Shapes
import com.example.nkhukumanagement.userinterface.flock.EditFlockDestination
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations

object PlannerResultsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Calculate
    override val route: String
        get() = "planner result"
    override val resourceId: Int
        get() = R.string.estimation
}

@Composable
fun PlannerResultScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    plannerViewModel: PlannerViewModel
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(PlannerResultsDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->

        LazyVerticalGrid(
            modifier = modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                FeedResultsCard(planner = plannerViewModel.plannerUiState.toPlanner())
            }
            if (!plannerViewModel.plannerUiState.areFeedersAvailable) {
                item {
                    FeedersResultsCard(
                        planner = plannerViewModel.plannerUiState.toPlanner()
                    )
                }
            }
            if (!plannerViewModel.plannerUiState.areDrinkersAvailable) {
                item {
                    DrinkersResultsCard(planner = plannerViewModel.plannerUiState.toPlanner())
                }
            }
        }
    }

}

@Composable
fun FeedResultsCard(modifier: Modifier = Modifier, planner: Planner) {

    Card(
        modifier = modifier,
        shape = Shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Feed",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Divider(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "Feed Type",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Quantity",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Bags(50 Kg)",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "Starter",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "${String.format("%.2f", planner.starterNeeded)} Kg",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.totalStarterBags.toString(),
                        textAlign = TextAlign.Center
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "Grower",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "${String.format("%.2f", planner.growerNeeded)} Kg",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.totalGrowerBags.toString(),
                        textAlign = TextAlign.Center
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "Finisher",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "${String.format("%.2f", planner.finisherNeeded)} Kg",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.totalFinisherBags.toString(),
                        textAlign = TextAlign.Center
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "TOTAL",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.totalFeed.toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.totalBags.toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FeedersResultsCard(modifier: Modifier = Modifier, planner: Planner) {
    Card(
        modifier = modifier,
        shape = Shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Feeders",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Divider(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "Age(Days)",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Type",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Quantity",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "1 - 10",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Trays",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.chicksTray.toString(),
                        textAlign = TextAlign.Center
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "10 - 21",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Chick feeders",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.smallFeedersNeeded.toString(),
                        textAlign = TextAlign.Center
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "21 - market",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Adult Cone/Tube feeder",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.bigFeedersNeeded.toString(),
                        textAlign = TextAlign.Center
                    )
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "21 - market",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Trough",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "5-7 cm space per bird",
                        textAlign = TextAlign.Center
                    )
                }

            }
        }
    }
}

@Composable
fun DrinkersResultsCard(modifier: Modifier = Modifier, planner: Planner) {
    Card(
        modifier = modifier,
        shape = Shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Drinkers",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Divider(
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.tertiary
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "Age(Days)",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Type",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Quantity",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "1 - 21",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Chick drinkers",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.smallDrinkersNeeded.toString(),
                        textAlign = TextAlign.Center
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "21 - market",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "10L Manual feeders",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.bigDrinkersNeeded.toString(),
                        textAlign = TextAlign.Center
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "21 - market",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Automatic drinkers",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.automaticDrinkers.toString(),
                        textAlign = TextAlign.Center
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = "21 - market",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Nipple drinkers",
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.nippleDrinkers.toString(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun FeedResultsPreview() {
    NkhukuManagementTheme {
        FeedResultsCard(planner = PlannerUiState().toPlanner())
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun FeedersResultsPreview() {
    NkhukuManagementTheme {
        FeedersResultsCard(planner = PlannerUiState().toPlanner())
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun DrinkersResultsPreview() {
    NkhukuManagementTheme {
        DrinkersResultsCard(planner = PlannerUiState().toPlanner())
    }
}