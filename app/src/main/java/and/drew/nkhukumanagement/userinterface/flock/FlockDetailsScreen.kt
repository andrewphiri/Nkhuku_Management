package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.data.Weight
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.userinterface.vaccination.toVaccinationUiState
import and.drew.nkhukumanagement.userinterface.weight.toWeightUiState
import and.drew.nkhukumanagement.utils.BaseSingleRowDetailsItem
import and.drew.nkhukumanagement.utils.DateUtils
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
    navigateToFlockHealthScreen: (Int) -> Unit = {},
    navigateToVaccinationScreen: (Int) -> Unit,
    navigateToFeedScreen: (Int) -> Unit = {},
    navigateToWeightScreen: (Int) -> Unit = {},
    flockEntryViewModel: FlockEntryViewModel,
    detailsViewModel: FlockDetailsViewModel = hiltViewModel()
) {
    val flockWithVaccinations by detailsViewModel.flockWithVaccinationsStateFlow.collectAsState()
    val flockWithFeed by detailsViewModel.flockWithFeedStateFlow.collectAsState()
    val flockWithWeight by detailsViewModel.flockWithWeightStateFlow.collectAsState()
    val flock by detailsViewModel.flock.collectAsState(
        initial = flockEntryViewModel.flockUiState.copy(
            datePlaced = DateUtils().dateToStringLongFormat(LocalDate.now()),
            quantity = "0",
            donorFlock = "0",
            cost = "0"
        ).toFlock()
    )

    MainFlockDetailsScreen(
        modifier = modifier,
        canNavigateBack = canNavigateBack,
        onNavigateUp = onNavigateUp,
        navigateToFeedScreen = navigateToFeedScreen,
        navigateToFlockHealthScreen = navigateToFlockHealthScreen,
        navigateToVaccinationScreen = navigateToVaccinationScreen,
        navigateToWeightScreen = navigateToWeightScreen,
        flock = flock,
        onUpdateUiState = flockEntryViewModel::updateUiState,
        totalFeedQtyConsumed = flockWithFeed?.feedList?.sumOf { it.consumed },
        vaccinations = flockWithVaccinations?.vaccinations,
        weights = flockWithWeight?.weights
    )

//    flock.toFlockUiState(true).let { flockEntryViewModel.updateUiState(it) }
//    Scaffold(
//        topBar = {
//            FlockManagementTopAppBar(
//                title = stringResource(FlockDetailsDestination.resourceId),
//                canNavigateBack = canNavigateBack,
//                navigateUp = onNavigateUp
//            )
//        }
//    ) { innerPadding ->
//
//        LazyVerticalStaggeredGrid(
//            modifier = modifier.padding(innerPadding).fillMaxSize(),
//            columns = StaggeredGridCells.Fixed(2),
//            contentPadding = PaddingValues(8.dp),
//            verticalItemSpacing = 16.dp,
//            horizontalArrangement = Arrangement.spacedBy(16.dp).also { Arrangement.Center },
//        ) {
//            item {
//                flockWithVaccinations?.flock?.let { flock ->
//                    HealthCard(
//                        flock = flock,
//                        onHealthCardClick = {
//                            if (flock.active) {
//                                navigateToFlockHealthScreen(flock.id)
//                            }
//                        })
//                }
//            }
//            item {
//                flockWithVaccinations?.let {
//                    it.flock?.let { flock ->
//                        VaccinationList(
//                            modifier = modifier,
//                            vaccinationUiStateList = it.vaccinations,
//                            onVaccinationCardClick = { id ->
//                                if (flock.active) {
//                                    navigateToVaccinationScreen(id)
//                                }
//                            },
//                            flock = flock
//                        )
//                    }
//                }
//            }
//            item {
//                flockWithFeed?.feedList?.sumOf { it.consumed }.let { totalQty ->
//                    if (totalQty != null) {
//                        FeedCard(
//                            quantityConsumed = totalQty,
//                            flockUiState = flock.toFlockUiState(enabled = true),
//                            onFeedCardClick = { id ->
//                                if (flock.active) {
//                                    navigateToFeedScreen(id)
//                                }
//                            }
//                        )
//                    }
//                }
//            }
//            item {
//                val weight = flockWithWeight?.weights?.lastOrNull { it.weight > 0.0 }
//                    ?: flockWithWeight?.weights?.first()
//                if (weight != null) {
//                    flock.let { flock ->
//                        WeightCard(weight = weight,
//                            flock = flock,
//                            onWeightCardClick = { id ->
//                                if (flock.active) {
//                                    navigateToWeightScreen(id)
//                                }
//                            })
//                    }
//                }
//            }
//        }
//    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainFlockDetailsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    navigateToFlockHealthScreen: (Int) -> Unit = {},
    navigateToVaccinationScreen: (Int) -> Unit,
    navigateToFeedScreen: (Int) -> Unit = {},
    navigateToWeightScreen: (Int) -> Unit = {},
    flock: Flock,
    vaccinations: List<Vaccination>?,
    totalFeedQtyConsumed: Double?,
    weights: List<Weight>?,
    onUpdateUiState: (FlockUiState) -> Unit,
) {

    onUpdateUiState(flock.toFlockUiState().copy(enabled = true))
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(FlockDetailsDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->

        LazyVerticalStaggeredGrid(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp).also { Arrangement.Center },
        ) {
            item {
                    HealthCard(
                        modifier = Modifier.semantics { contentDescription = "Health" },
                        flock = flock,
                        onHealthCardClick = {
                            if (flock.active) {
                                navigateToFlockHealthScreen(it.id)
                            }
                        })

            }
            item {

                        VaccinationList(
                            modifier = Modifier.semantics { contentDescription = "Vaccines" },
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
                    FeedCard(
                        modifier = Modifier.semantics { contentDescription = "Feed" },
                        quantityConsumed = totalFeedQtyConsumed,
                        flockUiState = flock.toFlockUiState(enabled = true),
                        onFeedCardClick = { id ->
                            if (flock.active) {
                                navigateToFeedScreen(id)
                            }
                        }
                    )
                }

            }
            item {
                val weight = weights?.lastOrNull { it.weight > 0.0 }
                    ?: weights?.first()
                if (weight != null) {
                    flock.let { flock ->
                        WeightCard(
                            modifier = Modifier.semantics { contentDescription = "Weight" },
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
        costPerBird = flock.costPerBird,
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Default.MedicalServices,
                contentDescription = "Health of flock"
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Health",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Divider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            BaseSingleRowDetailsItem(
                label = "Mortality",
                value = flockUiState.getMortality(),
                weightA = 2f
            )

            BaseSingleRowDetailsItem(
                label = "Culls",
                value = flockUiState.getCulls(),
                weightA = 2f
            )

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
            modifier = Modifier.padding(16.dp),
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
                text = "Feed",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Divider(
                thickness = Dp.Hairline, color = MaterialTheme.colorScheme.tertiary
            )

            BaseSingleRowDetailsItem(
                label = "Bags(50Kg)",
                value = "${(quantityConsumed / 50).roundToInt()}"
            )
            BaseSingleRowDetailsItem(
                label = "Total Feed Consumed",
                value = "${String.format("%.2f", quantityConsumed)} Kg"
            )
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
    ElevatedCard(modifier = modifier.padding(4.dp)) {
        Column(
            modifier = Modifier.padding(16.dp).clickable { onVaccinationCardClick(flock.id) },
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
                text = "Vaccination",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Divider(
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
        notes = vaccination.notes
    ).toVaccinationUiState()

    Card(modifier = modifier) {
        Column {

            BaseSingleRowDetailsItem(
                label = vaccinationUiState.getDate(),
                value = vaccinationUiState.getName(),
                style = MaterialTheme.typography.labelSmall
            )
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

            BaseSingleRowDetailsItem(
                label = "Actual",
                value = "${weightUiState.actualWeight} Kg",
                weightA = 1.5f,
            )

            BaseSingleRowDetailsItem(
                label = "Standard",
                value = "${weightUiState.standard} Kg",
                weightA = 1.5f
            )
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