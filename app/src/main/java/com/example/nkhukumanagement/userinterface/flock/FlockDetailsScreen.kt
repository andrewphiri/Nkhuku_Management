package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.FeedUiState
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import kotlin.String

object FlockDetailsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Details
    override val route: String
        get() = "Details"
    override val resourceId: Int
        get() = R.string.details
    const val flockIdArg = "id"
    val routeWithArgs = "$route/{$flockIdArg}"
    val arguments = listOf(navArgument(flockIdArg) {
        type = NavType.IntType
    })
}

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockDetailsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    vaccinationViewModel: VaccinationViewModel,
    flockEntryViewModel: FlockEntryViewModel
){
    Scaffold (
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(FlockDetailsDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
            ){ innerPadding ->

        LazyVerticalGrid(
            modifier = modifier.padding(innerPadding).fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp).also { Arrangement.Center },
            horizontalArrangement = Arrangement.spacedBy(16.dp).also { Arrangement.Center },
        ) { item { HealthCard(flockUiState = flockEntryViewModel.flockUiState) }
            item { VaccinationList(modifier =  modifier, vaccinationUiStateList = vaccinationViewModel.getInitialVaccinationList() ) }
            item { FeedCard(
                feedUiState = FeedUiState(
                    name = "Novatek", type = "Pre-starter", consumed = 12.0),
                flockUiState = flockEntryViewModel.flockUiState
            ) }
            item {
                WeightCard(weightUiState = WeightUiState(week = "Initial weight", weight = 0.04)) }

        }
    }
}

@Composable
fun HealthCard(modifier: Modifier = Modifier, flockUiState: FlockUiState) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.MedicalServices,
                contentDescription = "Health of flock"
            )
            Text(modifier = Modifier.fillMaxWidth(),
            text = "Health",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center)

            Divider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            Row {
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),
                    text = "Mortality",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),
                    text = flockUiState.mortality.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
            }
            Row {
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),
                    text = "Culls",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),
                    text = flockUiState.culls.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedCard(modifier: Modifier = Modifier, feedUiState: FeedUiState, flockUiState: FlockUiState) {
    ElevatedCard  (
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.Inventory,
                contentDescription = "feed",
                contentScale = ContentScale.Fit
            )
            Text(modifier = Modifier.fillMaxWidth(),
                text = "Feed",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center)
            Divider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )
            Row {
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),
                    text = "Consumption/Bird:",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),
                    text = "${
                        String.format(
                            "%.2f",
                            feedUiState.consumed / 2
                        )
                    } Kgs",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
            }
                Row {
                    Text(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .padding(2.dp),
                        text = "Total Feed Consumed:",
                        style = MaterialTheme.typography.bodySmall,

                        textAlign = TextAlign.Start
                    )
                    Text(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .padding(2.dp),
                        text = "${feedUiState.consumed} Kgs",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Start
                    )
            }
        }
    }
}

@Composable
fun VaccinationList(modifier: Modifier, vaccinationUiStateList: List<VaccinationUiState>) {
    ElevatedCard (modifier = modifier.padding(4.dp),) {
        Column (
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.Vaccines,
                contentDescription = "Pending vaccinations",
                contentScale = ContentScale.Fit
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Vaccinations",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Divider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )
            vaccinationUiStateList.forEach { uiState ->
                VaccinationCard(vaccinationUiState = uiState)
            }
        }
    }
}

@Composable
fun VaccinationCard(modifier: Modifier = Modifier, vaccinationUiState: VaccinationUiState) {
    Card(modifier = modifier){
        Column {
            Row {
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),text = vaccinationUiState.getDate(),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Justify
                )
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),text = vaccinationUiState.getName(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun WeightCard(modifier: Modifier = Modifier, weightUiState: WeightUiState) {
    ElevatedCard (modifier = modifier) {
        Column (
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
                ) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.Scale,
                contentDescription = "Average weight",
                contentScale = ContentScale.Fit
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Weight",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Divider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )
            Row {
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),
                    text = weightUiState.week,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),
                    text = weightUiState.weight.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,

                    textAlign = TextAlign.Start
                )

            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun FeedPreview() {
    NkhukuManagementTheme {
        FeedCard(
            feedUiState = FeedUiState(name = "Novatek", type = "Starter", consumed = 17.00),
            flockUiState = FlockUiState(quantity = "250", mortality = 6)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun WeightPreview() {
    NkhukuManagementTheme {

    }
}