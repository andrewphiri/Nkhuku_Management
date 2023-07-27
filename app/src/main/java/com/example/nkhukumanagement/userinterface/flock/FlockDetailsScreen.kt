package com.example.nkhukumanagement.userinterface.flock

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.nkhukumanagement.FlockManagementTopAppBar
import com.example.nkhukumanagement.R
import com.example.nkhukumanagement.data.Feed
import com.example.nkhukumanagement.data.Flock
import com.example.nkhukumanagement.data.Vaccination
import com.example.nkhukumanagement.data.Weight
import com.example.nkhukumanagement.toFeedUiState
import com.example.nkhukumanagement.ui.theme.NkhukuManagementTheme
import com.example.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import com.example.nkhukumanagement.utils.DateUtils
import java.time.LocalDate
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
    val arguments = listOf(navArgument(flockIdArg)  {
        defaultValue = 1
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
    navigateToFlockEdit: (Int) -> Unit = {},
    navigateToVaccinationScreen: (Int) -> Unit,
    navigateToFeedScreen: (Int) -> Unit = {},
    navigateToWeightScreen: (Int) -> Unit = {},
    flockEntryViewModel: FlockEntryViewModel,
    detailsViewModel: FlockDetailsViewModel = hiltViewModel()
){
    val flockWithVaccinations by detailsViewModel.detailsVaccinationUiState.collectAsState()
    val flockWithFeed by detailsViewModel.detailsFeedUiState.collectAsState()
    val flockWithWeight by detailsViewModel.detailsWeightUiState.collectAsState()
    val flock by detailsViewModel.flock.collectAsState(initial = flockEntryViewModel.flockUiState.copy(
        datePlaced = DateUtils().convertLocalDateToString(LocalDate.now()), quantity = "0", donorFlock = "0"
    ).toFlock())


    flockEntryViewModel.updateUiState(flock.toFlockUiState(true))
    Scaffold (
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(FlockDetailsDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
            ){ innerPadding ->

        LazyVerticalStaggeredGrid(
            modifier = modifier.padding(innerPadding).fillMaxSize(),
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp).also { Arrangement.Center },
        ) {
            item {
                flockWithVaccinations.flock?.let {
                    HealthCard(flock = it, onHealthCardClick = { navigateToFlockEdit(flock.id) }) }
            }
            item {
                VaccinationList(modifier =  modifier,
                    vaccinationUiStateList = flockWithVaccinations.vaccinations,
                    onVaccinationCardClick = {
                        navigateToVaccinationScreen(it)},
                    flock = flock)
            }
            item {
                flockWithFeed.feedList?.last()?.let {
                    FeedCard(
                        feed = it,
                        flockUiState = flock.toFlockUiState(enabled = true),
                        onFeedCardClick = {id ->
                            navigateToFeedScreen(id)
                        }
                    )
                }
            }
            item {
                flockWithWeight.weights?.last()?.let { WeightCard(weight = it,
                    flock = flock,
                    onWeightCardClick = { id -> navigateToWeightScreen(id) }) }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthCard(modifier: Modifier = Modifier, flock: Flock, onHealthCardClick: (Flock) -> Unit) {
    val flockUiState = Flock(
        id = flock.id,
        uniqueId = flock.uniqueId,
        batchName = flock.batchName,
        breed = flock.breed,
        datePlaced = flock.datePlaced,
        mortality = flock.mortality,
        numberOfChicksPlaced = flock.numberOfChicksPlaced,
        culls = flock.culls,
        stock = flock.stock,
        donorFlock = flock.donorFlock
    ).toFlockUiState()
    ElevatedCard(
        modifier = modifier
            .clickable { onHealthCardClick(flock) }
    ) {
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

           Card {
               Row (
                   modifier = Modifier.height(IntrinsicSize.Min).padding(8.dp),
                   verticalAlignment = Alignment.CenterVertically,
                   horizontalArrangement = Arrangement.spacedBy(8.dp)
               ) {
                   Text(
                       modifier = Modifier.weight(2f).fillMaxWidth()
                           .padding(2.dp),
                       text = "Mortality",
                       style = MaterialTheme.typography.bodySmall,
                       textAlign = TextAlign.Start
                   )

                   Divider(
                       modifier = Modifier.weight(0.01f).fillMaxHeight(),
                       thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
                   )

                   Text(
                       modifier = Modifier.weight(1f).fillMaxWidth()
                           .padding(2.dp),
                       text = flockUiState.getMortality(),
                       style = MaterialTheme.typography.bodySmall,
                       textAlign = TextAlign.Start
                   )
               }
           }
           Card {
               Row(
                   modifier = Modifier.height(IntrinsicSize.Min).padding(8.dp),
                   verticalAlignment = Alignment.CenterVertically,
                   horizontalArrangement = Arrangement.spacedBy(8.dp)
               ) {
                   Text(
                       modifier = Modifier.weight(2f).fillMaxWidth()
                           .padding(2.dp),
                       text = "Culls",
                       style = MaterialTheme.typography.bodySmall,
                       textAlign = TextAlign.Start
                   )

                   Divider(
                       modifier = Modifier.weight(0.01f).fillMaxHeight(),
                       thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
                   )

                   Text(
                       modifier = Modifier.weight(1f).fillMaxWidth()
                           .padding(2.dp),
                       text = flockUiState.getCulls(),
                       style = MaterialTheme.typography.bodySmall,
                       textAlign = TextAlign.Start
                   )
               }
           }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedCard(modifier: Modifier = Modifier, feed: Feed, flockUiState: FlockUiState,
             onFeedCardClick: (Int) -> Unit) {
    val feedUiState = Feed(
     id = feed.id,
     flockUniqueId = feed.flockUniqueId,
     name = feed.name,
     type = feed.type,
     consumed = feed.consumed,
     feedingDate = feed.feedingDate
    ).toFeedUiState()

    ElevatedCard  (
        modifier = modifier
            .clickable { onFeedCardClick(flockUiState.id) }
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

            Card {
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min).padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .padding(2.dp),
                        text = "Consumption/Bird:",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )

                    Divider(
                        modifier = Modifier.weight(0.01f).fillMaxHeight(),
                        thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
                    )

                    Text(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .padding(2.dp),
                        text = "${
                            String.format(
                                "%.2f",
                                feedUiState.consumed.toDouble() / flockUiState.getBirdsRemaining()
                            )
                        } Kgs",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )
                }
            }
                Card {
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Min).padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            modifier = Modifier.weight(1f).fillMaxWidth()
                                .padding(2.dp),
                            text = "Total Feed Consumed:",
                            style = MaterialTheme.typography.bodySmall,

                            textAlign = TextAlign.Start
                        )

                        Divider(
                            modifier = Modifier.weight(0.01f).fillMaxHeight(),
                            thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VaccinationList(modifier: Modifier, vaccinationUiStateList: List<Vaccination>,
                    onVaccinationCardClick: (Int) -> Unit, flock: Flock) {
    ElevatedCard (modifier = modifier.padding(4.dp),) {
        Column (
            modifier = Modifier.padding(16.dp).clickable { onVaccinationCardClick(flock.id) },
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
                text = "Vaccination",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Divider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )
            vaccinationUiStateList.forEach { uiState ->
                VaccinationCard(vaccination = uiState)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VaccinationCard(modifier: Modifier = Modifier, vaccination: Vaccination) {
    val vaccinationUiState = Vaccination(
        id = vaccination.id,
        flockUniqueId = vaccination.flockUniqueId,
        date = vaccination.date,
        name = vaccination.name,
        notes = vaccination.notes

    ).toVaccinationUiState()

    Card(modifier = modifier){
        Column {
            Row (
                modifier = Modifier.height(IntrinsicSize.Min).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),text = vaccinationUiState.getDate(),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )

                Divider(
                    modifier = Modifier.weight(0.01f).fillMaxHeight(),
                    thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    modifier = Modifier.weight(1f).fillMaxWidth()
                        .padding(2.dp),text = vaccinationUiState.getName(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = TextUnit(value = 10f, type = TextUnitType.Sp ),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightCard(modifier: Modifier = Modifier, weight: Weight, flock: Flock, onWeightCardClick: (Int) -> Unit) {
    val weightUiState = Weight(
        id = weight.id,
        flockUniqueId = weight.flockUniqueId,
        week = weight.week,
        weight = weight.weight,
        expectedWeight = weight.expectedWeight,
        measuredDate = weight.measuredDate
    ).toWeightUiState()

    ElevatedCard (modifier = modifier
        .clickable { onWeightCardClick(flock.id) }) {
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

            Text(
                modifier = Modifier.fillMaxWidth()
                    .padding(2.dp),
                text = weightUiState.week,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Card {
                Row(modifier = Modifier.height(IntrinsicSize.Min).padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        modifier = Modifier.weight(1.5f).fillMaxWidth()
                            .padding(2.dp),
                        text = "Actual",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Start
                    )
                    Divider(
                        modifier = Modifier.weight(0.01f).fillMaxHeight(),
                        thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
                    )

                    Text(
                        modifier = Modifier.weight(1f).fillMaxWidth()
                            .padding(2.dp),
                        text = "${weightUiState.actualWeight} Kg",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        textAlign = TextAlign.Start
                    )
                }
            }

               Card {
                   Row(modifier = Modifier.height(IntrinsicSize.Min).padding(8.dp),
                       verticalAlignment = Alignment.CenterVertically,
                       horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                       Text(
                           modifier = Modifier.weight(1.5f).fillMaxWidth()
                               .padding(2.dp),
                           text = "Standard",
                           style = MaterialTheme.typography.bodySmall,
                           textAlign = TextAlign.Start
                       )
                       Divider(
                           modifier = Modifier.weight(0.01f).fillMaxHeight(),
                           thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
                       )

                       Text(
                           modifier = Modifier.weight(1f).fillMaxWidth()
                               .padding(2.dp),
                           text = "${weightUiState.standard} Kg",
                           style = MaterialTheme.typography.bodySmall,
                           color = MaterialTheme.colorScheme.tertiary,
                           textAlign = TextAlign.Start
                       )
                   }
               }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun FeedPreview() {
    NkhukuManagementTheme {
//        FeedCard(
//            feed = Feed(name = "Novatek", type = "Starter", consumed = 17.00),
//            flockUiState = FlockUiState(quantity = "250", mortality = 6)
//        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun WeightPreview() {
    NkhukuManagementTheme {

    }
}