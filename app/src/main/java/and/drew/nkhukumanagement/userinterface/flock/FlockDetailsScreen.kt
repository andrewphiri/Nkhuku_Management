package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.EggsSummary
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.FlockAndEggsSummary
import and.drew.nkhukumanagement.data.FlockWithFeed
import and.drew.nkhukumanagement.data.FlockWithVaccinations
import and.drew.nkhukumanagement.data.FlockWithWeight
import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.data.Weight
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.userinterface.vaccination.toVaccinationUiState
import and.drew.nkhukumanagement.userinterface.weight.toWeightUiState
import and.drew.nkhukumanagement.utils.BaseSingleRowDetailsItem
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.DateUtils
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import java.time.LocalDate
import kotlin.math.roundToInt

object FlockDetailsDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Details
    override val route: String
        get() = "Details"
    override val resourceId: Int
        get() = R.string.details
    const val flockId = "flock_id_arg"
    val routeWithArgs = "$route/{$flockId}"
    const val uri = "nkhuku://www.drew.nkhuku.com"
    val deepLink = listOf(navDeepLink {
        uriPattern = "$uri/{$flockId}"
        action = Intent.ACTION_VIEW

    })
    val arguments = listOf(navArgument(flockId) {
        defaultValue = 0
        type = NavType.IntType
    })
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FlockDetailsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    navigateToFlockHealthScreen: (Int) -> Unit,
    navigateToVaccinationScreen: (Int) -> Unit,
    navigateToFeedScreen: (Int) -> Unit = {},
    navigateToWeightScreen: (Int) -> Unit = {},
    navigateToEggsInventoryScreen: (Int) -> Unit,
    flockEntryViewModel: FlockEntryViewModel,
    detailsViewModel: FlockDetailsViewModel = hiltViewModel(),
    contentType: ContentType
) {

    val flockWithVaccinations by detailsViewModel
        .flockWithVaccinationsStateFlow
        .collectAsState(
            initial = FlockWithVaccinations(flock = null, vaccinations = listOf())
        )
    val flockWithFeed by detailsViewModel
        .flockWithFeedStateFlow
        .collectAsState(
            initial = FlockWithFeed(flock = null, feedList = null)
        )
    val flockWithWeight by detailsViewModel
        .flockWithWeightStateFlow
        .collectAsState(
            initial = FlockWithWeight(flock = null, weights = null)
        )
    val flock by detailsViewModel.flock.collectAsState(
        initial = flockEntryViewModel.flockUiState.copy(
            datePlaced = DateUtils().dateToStringLongFormat(LocalDate.now()),
            quantity = "0",
            donorFlock = "0",
            cost = "0"
        ).toFlock()
    )

    val flockAndEggsSummary by detailsViewModel
        .flockAndEggsSummaryStateFlow
        .collectAsState(
        initial = FlockAndEggsSummary(flock = null,
            eggsSummary = EggsSummary(
                flockUniqueID = "",
                totalGoodEggs = 0,
                totalBadEggs = 0,
                date = LocalDate.now()
            )
        )
    )

    MainFlockDetailsScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        onNavigateUp = {
            onNavigateUp()
            flockEntryViewModel.resetAll()
        },
        navigateToFeedScreen = navigateToFeedScreen,
        navigateToFlockHealthScreen = navigateToFlockHealthScreen,
        navigateToVaccinationScreen = navigateToVaccinationScreen,
        navigateToWeightScreen = navigateToWeightScreen,
        flock = flock,
        onUpdateUiState = flockEntryViewModel::updateUiState,
        totalFeedQtyConsumed = flockWithFeed?.feedList?.sumOf { it.consumed },
        vaccinations = flockWithVaccinations?.vaccinations,
        weights = flockWithWeight?.weights,
        eggsSummary = flockAndEggsSummary.eggsSummary,
        contentType = contentType,
        navigateToEggsInventoryScreen = navigateToEggsInventoryScreen
    )

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainFlockDetailsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    navigateToFlockHealthScreen: (Int) -> Unit,
    navigateToVaccinationScreen: (Int) -> Unit,
    navigateToFeedScreen: (Int) -> Unit,
    navigateToWeightScreen: (Int) -> Unit,
    navigateToEggsInventoryScreen: (Int) -> Unit,
    flock: Flock?,
    eggsSummary: EggsSummary?,
    vaccinations: List<Vaccination>?,
    totalFeedQtyConsumed: Double?,
    weights: List<Weight>?,
    onUpdateUiState: (FlockUiState) -> Unit,
    contentType: ContentType
) {
    val context = LocalContext.current
    val flockTypeOptions = context.resources.getStringArray(R.array.types_of_flocks).toList()
    flock?.toFlockUiState()?.copy(enabled = true)?.let { onUpdateUiState(it) }
    if (flock != null) {
        Scaffold(
            modifier = modifier,
            topBar = {
                FlockManagementTopAppBar(
                    title = flock.batchName,
                    canNavigateBack = canNavigateBack,
                    navigateUp = onNavigateUp,
                    contentType = contentType
                )
            }
        ) { innerPadding ->

            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .padding(innerPadding),
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalItemSpacing = 16.dp,
                horizontalArrangement = Arrangement.spacedBy(16.dp).also { Arrangement.Center },
            ) {
                item {
                    HealthCard(
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.health)
                        },
                        flock = flock,
                        onHealthCardClick = {
                            if (flock.active) {
                                navigateToFlockHealthScreen(it.id)
                            }
                        })
                }
                item {

                    VaccinationList(
                        modifier = Modifier.semantics {
                            contentDescription =
                                context.getString(R.string.vaccines)
                        },
                        vaccinationUiStateList = vaccinations,
                        onVaccinationCardClick = { id ->
                            if (flock.active) {
                                navigateToVaccinationScreen(id)
                            }
                        },
                        flock = flock
                    )

                }
                item {

                    if (totalFeedQtyConsumed != null) {
                        flock.toFlockUiState(enabled = true).let {
                            FeedCard(
                                modifier = Modifier.semantics {
                                    contentDescription = context.getString(R.string.feed)
                                },
                                quantityConsumed = totalFeedQtyConsumed,
                                flockUiState = it,
                                onFeedCardClick = { id ->
                                    if (flock.active) {
                                        navigateToFeedScreen(id)
                                    }
                                }
                            )
                        }
                    }
                }

                item {
                    val weight = weights?.lastOrNull { it.weight > 0.0 }
                        ?: weights?.first()
                    if (weight != null) {
                        flock.let { flock ->
                            WeightCard(
                                modifier = Modifier.semantics {
                                    contentDescription = context.getString(R.string.weight)
                                },
                                weight = weight,
                                flock = flock,
                                onWeightCardClick = { id ->
                                    if (flock.active) {
                                        navigateToWeightScreen(id)
                                    }
                                }
                            )
                        }
                    }
                }

                if (flock.flockType == flockTypeOptions[1]) {
                    item {
                            flock.let { flock ->
                                EggsCard(
                                    modifier = Modifier.semantics {
                                        contentDescription = context.getString(R.string.weight)
                                    },
                                    eggsSummary = eggsSummary,
                                    flock = flock,
                                    onEggsCardClick = { id ->
                                        if (flock.active) {
                                            //Log.d("TAG", "MainFlockDetailsScreen: $id")
                                            navigateToEggsInventoryScreen(id)
                                        }
                                    }
                                )
                            }

                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HealthCard(modifier: Modifier = Modifier, flock: Flock?, onHealthCardClick: (Flock) -> Unit) {
    val flockUiState = flock?.let { flock1 ->
        Flock(
            id = flock1.id,
            uniqueId = flock1.uniqueId,
            batchName = flock1.batchName,
            flockType = flock1.flockType,
            breed = flock1.breed,
            datePlaced = flock1.datePlaced,
            mortality = flock1.mortality,
            numberOfChicksPlaced = flock1.numberOfChicksPlaced,
            costPerBird = flock1.costPerBird,
            culls = flock1.culls,
            stock = flock1.stock,
            donorFlock = flock1.donorFlock,
        ).toFlockUiState()
    }

    ElevatedCard(
        modifier = modifier
            .clickable {
                if (flock != null) {
                    onHealthCardClick(flock)
                }
            }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.MedicalServices,
                contentDescription = stringResource(R.string.health_of_flock)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.health),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            HorizontalDivider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            flockUiState?.getMortality()?.let {
                Card {
                    BaseSingleRowDetailsItem(
                        label = stringResource(R.string.mortality),
                        value = it,
                        weightA = 2f
                    )
                }
            }

            flockUiState?.getCulls()?.let {
                Card {
                    BaseSingleRowDetailsItem(
                        label = stringResource(R.string.culls),
                        value = it,
                        weightA = 2f
                    )
                }
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FeedCard(
    modifier: Modifier = Modifier, quantityConsumed: Double,
    flockUiState: FlockUiState,
    onFeedCardClick: (Int) -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .clickable { onFeedCardClick(flockUiState.id) }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.Inventory,
                contentDescription = "feed",
                contentScale = ContentScale.Fit
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.feed),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            HorizontalDivider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            Card {
                BaseSingleRowDetailsItem(
                    label = stringResource(R.string.bags_50kg),
                    value = "${(quantityConsumed / 50).roundToInt()}"
                )
            }

            Card {
                BaseSingleRowDetailsItem(
                    label = stringResource(R.string.total_feed_consumed).lowercase()
                        .replaceFirstChar { it.uppercase() },
                    value = stringResource(R.string.kg, String.format("%.2f", quantityConsumed))
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VaccinationList(
    modifier: Modifier,
    vaccinationUiStateList: List<Vaccination>?,
    onVaccinationCardClick: (Int) -> Unit, flock: Flock
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .clickable { onVaccinationCardClick(flock.id) },
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.Vaccines,
                contentDescription = "Pending vaccinations",
                contentScale = ContentScale.Fit
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.vaccination),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            HorizontalDivider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )
            if (vaccinationUiStateList != null) {
                vaccinationUiStateList.forEach { uiState ->
                    VaccinationCard(vaccination = uiState)
                }
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
        notes = vaccination.notes,
        notificationUUID = vaccination.notificationUUID,
        hasVaccineBeenAdministered = vaccination.hasVaccineBeenAdministered,
        method = vaccination.method,
    ).toVaccinationUiState()

    Card(modifier = modifier.fillMaxSize()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BaseSingleRowDetailsItem(
                modifier = Modifier.weight(1f, true),
                label = vaccinationUiState.getDate(),
                value = vaccinationUiState.getName(),
                style = MaterialTheme.typography.labelSmall
            )
            Box(
                modifier = Modifier.weight(0.25f)
            ) {
                Checkbox(
                    checked = vaccinationUiState.vaccineAdministered,
                    onCheckedChange = null
                )

            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeightCard(
    modifier: Modifier = Modifier,
    weight: Weight,
    flock: Flock,
    onWeightCardClick: (Int) -> Unit
) {
    val weightUiState = Weight(
        id = weight.id,
        flockUniqueId = weight.flockUniqueId,
        week = weight.week,
        weight = weight.weight,
        expectedWeight = weight.expectedWeight,
        measuredDate = weight.measuredDate
    ).toWeightUiState()

    ElevatedCard(modifier = modifier
        .clickable { onWeightCardClick(flock.id) }) {
        Column(
            modifier = Modifier.padding(8.dp),
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
                text = stringResource(R.string.weight),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            HorizontalDivider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                text = weightUiState.week,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Card {
                BaseSingleRowDetailsItem(
                    label = stringResource(R.string.actual),
                    value = stringResource(R.string.kg_weight, weightUiState.actualWeight),
                    weightA = 1.5f,
                )
            }

            Card {
                BaseSingleRowDetailsItem(
                    label = stringResource(R.string.standard),
                    value = stringResource(R.string.kg_weight, weightUiState.standard),
                    weightA = 1.5f
                )
            }

        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EggsCard(
    modifier: Modifier = Modifier,
    flock: Flock?,
    eggsSummary: EggsSummary?,
    onEggsCardClick: (Int) -> Unit) {

    ElevatedCard(
        modifier = modifier
            .clickable {
                if (flock != null) {
                    onEggsCardClick(flock.id)
                }
            }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.Egg,
                contentDescription = stringResource(R.string.egg_inventory)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.Eggs),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            HorizontalDivider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

                Card {
                    BaseSingleRowDetailsItem(
                        label = stringResource(R.string.good_eggs),
                        value = eggsSummary?.totalGoodEggs.toString(),
                        weightA = 2f
                    )
                }

                Card {
                    BaseSingleRowDetailsItem(
                        label = stringResource(R.string.bad_eggs),
                        value = eggsSummary?.totalBadEggs.toString(),
                        weightA = 2f
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