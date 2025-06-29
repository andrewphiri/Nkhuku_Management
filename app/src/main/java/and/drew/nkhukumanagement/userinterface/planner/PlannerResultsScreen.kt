package and.drew.nkhukumanagement.userinterface.planner

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.ui.theme.Shapes
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.ContentType
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import java.util.Locale

object PlannerResultsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Calculate
    override val route: String
        get() = "Planner result"
    override val resourceId: Int
        get() = R.string.estimation
}

@Serializable
object PlannerResultsScreenNav

@Composable
fun PlannerResultScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    plannerViewModel: PlannerViewModel,
    contentType: ContentType,
    userPrefsViewModel: UserPrefsViewModel,
) {
    val userPrefs by userPrefsViewModel.initialPreferences.collectAsState(
        initial = UserPreferences.getDefaultInstance()
    )

    val unitPref = if (userPrefs.massSystem == "Kilogram (Kg)") "kg" else if
            (userPrefs.massSystem== "Pound (lb)") "lb" else if
                    (userPrefs.massSystem == "Ounce (oz)") "oz" else "g"

    val bagSize = userPrefs.bagSize
    val bag = if (userPrefs.bagSize == "50 Kg" || userPrefs.bagSize == "50 lb") 50 else 25
    Log.d("PlannerResultScreen", "bag: $bag")
    Log.d("PlannerResultScreen", "bagSize: $bagSize")
    Log.d("PlannerResultScreen", "unitPref: $unitPref")
    Log.d("PlannerResultScreen", "unit: ${userPrefs.massSystem}")

    MainPlannerResultScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        onNavigateUp = onNavigateUp,
        plannerUiState = plannerViewModel.plannerUiState,
        contentType = contentType,
        bag = bag,
        bagSize = bagSize,
        unit = userPrefs.massSystem,
        unitPref = unitPref
    )
}

@Composable
fun MainPlannerResultScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    plannerUiState: PlannerUiState,
    contentType: ContentType,
    bag: Int = 50,
    bagSize: String,
    unit: String,
    unitPref: String
) {
    val context = LocalContext.current
    val flockTypeOptions = context.resources.getStringArray(R.array.types_of_flocks).toList()
    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(PlannerResultsDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp,
                contentType = contentType
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
                if (plannerUiState.flockType == flockTypeOptions[0]) {
                    FeedResultsCard(planner = plannerUiState.toPlanner(bagSize = bag, unit), bagSize = bagSize, unitPref = unitPref)
                } else {
                    LayersFeedResultsCard(planner = plannerUiState, bagSize = bagSize, unit = unit, bag = bag, unitPref = unitPref)
                }

            }

            if (plannerUiState.flockType == flockTypeOptions[0]) {
                if (!plannerUiState.areFeedersAvailable) {
                    item {
                        FeedersResultsCard(
                            planner = plannerUiState.toPlanner(bagSize = bag, unit)
                        )
                    }
                }
                if (!plannerUiState.areDrinkersAvailable) {
                    item {
                        DrinkersResultsCard(planner = plannerUiState.toPlanner(bag, unit))
                    }
                }
            }
        }
    }

}

@Composable
fun FeedResultsCard(
    modifier: Modifier = Modifier,
    planner: Planner,
    bagSize: String,
    unitPref: String) {

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
                text = stringResource(R.string.feed),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            HorizontalDivider(
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
                        text = stringResource(R.string.feed_type),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.quantity) + "\n" + unitPref,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = "Bag\n($bagSize)",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = stringResource(R.string.starter),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = String.format(Locale.getDefault(),"%.2f", planner.starterNeeded),
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
                        text = stringResource(R.string.grower),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = String.format(Locale.getDefault(),"%.2f", planner.growerNeeded),
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
                        text = stringResource(R.string.finisher),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = String.format(Locale.getDefault(),"%.2f", planner.finisherNeeded),
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
                        text = stringResource(R.string.total),
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
fun LayersFeedResultsCard(
    modifier: Modifier = Modifier,
    planner: PlannerUiState,
    bagSize: String,
    unitPref: String,
    unit: String,
    bag: Int = 50) {

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
                text = stringResource(R.string.feed),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            HorizontalDivider(
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
                        text = stringResource(R.string.feed_type),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.quantity) + "\n" + unitPref,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = bagSize,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = stringResource(R.string.broiler_starter),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = String.format(Locale.getDefault(),"%.2f", planner.calculateLayerStarter(unit)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.calculateLayerStarterBags(bag, unit).toString(),
                        textAlign = TextAlign.Center
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = stringResource(R.string.pullet_starter),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = String.format(Locale.getDefault(),"%.2f", planner.calculatePulletLayerStarter(unit)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.calculatePulletLayerStarterBags(bag, unit).toString(),
                        textAlign = TextAlign.Center
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = stringResource(R.string.pullet_grower),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = String.format(Locale.getDefault(), "%.2f", planner.calculatePulletGrower(unit)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.calculatePulletGrowerBags(bag, unit).toString(),
                        textAlign = TextAlign.Center
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = stringResource(R.string.pre_layer),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = String.format(Locale.getDefault(), "%.2f", planner.calculatePreLayer(unit)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.calculatePreLayerBags(bag, unit).toString(),
                        textAlign = TextAlign.Center
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = stringResource(R.string.layers_mash),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = String.format(Locale.getDefault(), "%.2f", planner.calculateLayersMash(unit)),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.calculateLayerMashBags(bag, unit).toString(),
                        textAlign = TextAlign.Center
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = stringResource(R.string.total),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.totalFeedLayers(unit).toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = planner.totalLayersBags(bag, unit).toString(),
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
                text = stringResource(R.string.feeders),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            HorizontalDivider(
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
                        text = stringResource(R.string.age_days),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.type),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.quantity),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = stringResource(R.string._1_10),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.trays),
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
                        text = stringResource(R.string._10_21),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.chick_feeders),
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
                        text = stringResource(R.string._21_market),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.adult_cone_tube_feeder),
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
                        text = stringResource(R.string._21_market),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.trough),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string._5_7_cm_space_per_bird),
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
                text = stringResource(R.string.drinkers),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            HorizontalDivider(
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
                        text = stringResource(R.string.age_days),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.type),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.quantity),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(weight = 2f, fill = true),
                        text = stringResource(R.string._1_21),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.chick_drinkers),
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
                        text = stringResource(R.string._21_market),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string._10l_manual_feeders),
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
                        text = stringResource(R.string._21_market),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.automatic_drinkers),
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
                        text = stringResource(R.string._21_market),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        modifier = Modifier.weight(weight = 1f, fill = true),
                        text = stringResource(R.string.nipple_drinkers),
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
        FeedResultsCard(planner = PlannerUiState().toPlanner(50, "Kilogram (Kg)"), bagSize = "50 Kg", unitPref = "kg")
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun FeedersResultsPreview() {
    NkhukuManagementTheme {
        FeedersResultsCard(planner = PlannerUiState().toPlanner(50, "Kilogram (Kg)"))
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview(showBackground = true)
@Composable
fun DrinkersResultsPreview() {
    NkhukuManagementTheme {
        DrinkersResultsCard(planner = PlannerUiState().toPlanner(50, "Kilogram (Kg)"))
    }
}