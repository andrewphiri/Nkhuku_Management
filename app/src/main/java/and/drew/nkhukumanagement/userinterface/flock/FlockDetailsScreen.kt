package and.drew.nkhukumanagement.userinterface.flock

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.backupAndExport.ExportRoomAsPDFViewModel
import and.drew.nkhukumanagement.backupAndExport.ExportRoomViewModel
import and.drew.nkhukumanagement.billing.BillingManager
import and.drew.nkhukumanagement.billing.BillingState
import and.drew.nkhukumanagement.data.EggsSummary
import and.drew.nkhukumanagement.data.Flock
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
import and.drew.nkhukumanagement.utils.convertToKg
import and.drew.nkhukumanagement.utils.convertWeight
import and.drew.nkhukumanagement.utils.formatConsumption
import and.drew.nkhukumanagement.utils.formatToKgConsumption
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.Vaccines
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.android.billingclient.api.ProductDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.Locale
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

@Serializable
data class FlockDetailsScreenNav(val flockId: Int)

@Composable
fun FlockDetailsScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    flockId: Int,
    navigateToFlockHealthScreen: (Int) -> Unit,
    navigateToVaccinationScreen: (Int) -> Unit,
    navigateToFeedScreen: (Int) -> Unit = {},
    navigateToWeightScreen: (Int) -> Unit = {},
    navigateToEggsInventoryScreen: (Int,Int) -> Unit,
    flockEntryViewModel: FlockEntryViewModel,
    detailsViewModel: FlockDetailsViewModel = hiltViewModel(),
    contentType: ContentType,
    exportRoomViewModel: ExportRoomViewModel = hiltViewModel(),
    exportRoomAsPDFViewModel: ExportRoomAsPDFViewModel = hiltViewModel(),
    unitPreference: String,
    bagSize: String
) {
    val activity = LocalActivity.current
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
    val isExportingExcel by exportRoomViewModel.isExporting.collectAsState()
    val isExportingAsPDF by exportRoomAsPDFViewModel.isExporting.collectAsState()
    val errorMessage by exportRoomViewModel.errorMessage.collectAsState()
    val errorMessageAsPDF by exportRoomAsPDFViewModel.errorMessage.collectAsState()

//    FlockAndEggsSummary(flock = null,
//        eggsSummary = EggsSummary(
//            flockUniqueID = "",
//            totalGoodEggs = 0,
//            totalBadEggs = 0,
//            date = LocalDate.now()
//        )
//    )



    val flockAndEggsSummary by detailsViewModel
        .flockAndEggsSummaryStateFlow
        .collectAsState(
        initial = null
    )

    LaunchedEffect(Unit) {
        detailsViewModel.getFlock(flockId)
        detailsViewModel.getFlockWithFeed(flockId)
        detailsViewModel.getFlockWithWeight(flockId)
        detailsViewModel.getFlockWithVaccinations(flockId)
        detailsViewModel.getFlockAndEggsSummary(flockId)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isCircularIndicatorShowing by remember {mutableStateOf(false)}

    var isLoading by remember { mutableStateOf(false) }
    var showPlanDialog by remember { mutableStateOf(false) }
    var selectedProductDetails by remember { mutableStateOf<ProductDetails?>(null) }
    var selectedOfferToken by remember { mutableStateOf<String?>(null) }

    val requestStoragePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            coroutineScope.launch {
                exportRoomViewModel.exportRoomAsExcelFileAndShare(flock ?: return@launch, unitPreference)
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.feature_unavailable),
                    duration = SnackbarDuration.Long
                )
            }
        }
    }

    LaunchedEffect(errorMessage, errorMessageAsPDF) {
        if (errorMessage != null) {
           coroutineScope.launch {
               snackbarHostState.showSnackbar(
                   message = errorMessage ?: "",
                   duration = SnackbarDuration.Long
               )
           }
        }
        if (errorMessageAsPDF != null) {
           coroutineScope.launch {
               snackbarHostState.showSnackbar(
                   message = errorMessageAsPDF ?: "",
                   duration = SnackbarDuration.Long
               )
           }
        }
    }

    // ✨ Subscription Plan Dialog
//    if (showPlanDialog && selectedProductDetails != null && selectedOfferToken != null) {
//        AlertDialog(
//            onDismissRequest = { showPlanDialog = false },
//            title = { Text("Premium Plan") },
//            text = {
//                Column {
//                    Text("Unlock export functionality by subscribing:")
//                    Text(
//                        text = selectedProductDetails!!.name,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text("Price: ${selectedProductDetails!!.subscriptionOfferDetails!!.first().pricingPhases.pricingPhaseList.first().formattedPrice}")
//                }
//            },
//            confirmButton = {
//                TextButton(onClick = {
//                    showPlanDialog = false
//                    coroutineScope.launch {
//                        selectedProductDetails?.let { product ->
//                            selectedOfferToken?.let { token ->
//                                val billingManager = BillingManager(context)
//                                activity?.let { billingManager.launchSubscriptionFlow(it, product, token) }
//                            }
//                        }
//                    }
//                }) {
//                    Text("Subscribe")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { showPlanDialog = false }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }

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
        eggsSummary = flockAndEggsSummary?.eggsSummary,
        contentType = contentType,
        navigateToEggsInventoryScreen = { navigateToEggsInventoryScreen(it, flockId) },
        isCircularIndicatorShowing = isExportingAsPDF || isExportingExcel || isLoading,
        showExportButton = true,
        onClickExportAsExcel = {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) -> {
                    coroutineScope.launch {
                        isCircularIndicatorShowing = true
                        delay(3000)
                        exportRoomViewModel.exportRoomAsExcelFileAndShare(flock ?: return@launch, unitPreference)
                    }.invokeOnCompletion {
                        isCircularIndicatorShowing = false
                    }
                }
                else -> {
                    requestStoragePermission.launch(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            }


//            if (activity != null) {
//                coroutineScope.launch {
//                    isLoading = true
//                    val billingManager = BillingManager(context)
//
//                    billingManager.startConnection {
//                        if (BillingState.isSubscribed) {
//                            coroutineScope.launch {
//                                when (PackageManager.PERMISSION_GRANTED) {
//                                    ContextCompat.checkSelfPermission(
//                                        context,
//                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//                                    ) -> {
//                                        coroutineScope.launch {
//                                            isCircularIndicatorShowing = true
//                                            delay(3000)
//                                            exportRoomViewModel.exportRoomAsExcelFileAndShare(flock ?: return@launch)
//                                        }.invokeOnCompletion {
//                                            isCircularIndicatorShowing = false
//                                        }
//                                    }
//                                    else -> {
//                                        requestStoragePermission.launch(
//                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//                                        )
//                                    }
//                                }
//                                //snackbarHostState.showSnackbar("PDF exported successfully.")
//                            }
//                        } else {
//                            billingManager.queryProductDetails("yearly_subscription_id") { productDetails ->
//                                val offer = productDetails?.subscriptionOfferDetails?.firstOrNull()
//                                if (productDetails != null && offer != null) {
//                                    selectedProductDetails = productDetails
//                                    selectedOfferToken = offer.offerToken
//                                    showPlanDialog = true
//                                } else {
//                                    coroutineScope.launch {
//                                        snackbarHostState.showSnackbar("Subscription unavailable.")
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    isLoading = false
//                }
//            }
        },
        onClickExportAsPDF = {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) -> {
                    coroutineScope.launch {
                        isCircularIndicatorShowing = true
                        delay(3000)
                        exportRoomAsPDFViewModel.exportRoomAsPDFAndShare(flock ?: return@launch, unitPreference)
                    }.invokeOnCompletion {
                        isCircularIndicatorShowing = false
                    }
                }
                else -> {
                    requestStoragePermission.launch(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }

            }
        },
        isExportButtonEnabled = !isExportingExcel || !isExportingAsPDF,
        unitPreference = unitPreference,
        bagSize = bagSize
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
    contentType: ContentType,
    isCircularIndicatorShowing: Boolean = false,
    showExportButton: Boolean = false,
    onClickExportAsExcel: () -> Unit = {},
    onClickExportAsPDF: () -> Unit = {},
    isExportButtonEnabled: Boolean = false,
    bagSize: String,
    unitPreference: String
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
                    contentType = contentType,
                    onClickExportAsExcel = onClickExportAsExcel,
                    showExportButton = showExportButton,
                    onClickExportAsPDF = onClickExportAsPDF,
                    isExportButtonEnabled = isExportButtonEnabled
                )
            }
        ) { innerPadding ->

            if (isCircularIndicatorShowing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.5f),
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            LazyVerticalStaggeredGrid(
                modifier = Modifier
                    .padding(innerPadding)
                    .alpha(if (isCircularIndicatorShowing) 0.5f else 1f),
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
                                },
                                bagSize = bagSize,
                                unitPreference = unitPreference
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
                                },
                                unitPreference = unitPreference
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
                imageVector = Icons.Outlined.MedicalServices,
                contentDescription = stringResource(R.string.health_of_flock),
                colorFilter = ColorFilter.tint(color = Color.Green)
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

@Composable
fun FeedCard(
    modifier: Modifier = Modifier, quantityConsumed: Double,
    flockUiState: FlockUiState,
    onFeedCardClick: (Int) -> Unit,
    bagSize: String,
    unitPreference: String
) {
    val context = LocalContext.current
    var totalFeedQtyConsumed: String by remember { mutableStateOf("") }
    totalFeedQtyConsumed = formatConsumption(quantityConsumed, unitPreference) + if (unitPreference == "Kilogram (Kg)") " Kg"
        else if (unitPreference == "Gram (g)") " g"
    else if (unitPreference == "Pound (lb)") " lb"
    else " oz"

    var totalBagSizeConsumed: Int? by remember { mutableStateOf(0) }
    totalBagSizeConsumed = convertWeight(quantityConsumed,unitPreference)?.div(
        if (bagSize == "50 Kg" || bagSize == "50 lb") 50 else 25
    )?.roundToInt()


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
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(color = Color.Magenta)
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
                    label = bagSize,
                    value = totalBagSizeConsumed.toString(),
                )
            }

            Card {
                BaseSingleRowDetailsItem(
                    label = stringResource(R.string.total_feed_consumed).lowercase()
                        .replaceFirstChar { it.uppercase() },
                    value = totalFeedQtyConsumed
//                        stringResource(R.string.kg, String.format(Locale.getDefault(),"%.2f", quantityConsumed))
                )


            }
        }
    }
}

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
                imageVector = Icons.Outlined.Vaccines,
                contentDescription = "Pending vaccinations",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(color = Color.Red)
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

@Composable
fun VaccinationCard(modifier: Modifier = Modifier, vaccination: Vaccination) {
    val vaccinationUiState = Vaccination(
        id = vaccination.id,
        flockUniqueId = vaccination.flockUniqueId,
        date = vaccination.date,
        name = vaccination.name,
        notes = vaccination.notes,
        notificationUUID = vaccination.notificationUUID,
        notificationUUID2 = vaccination.notificationUUID2,
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

@Composable
fun WeightCard(
    modifier: Modifier = Modifier,
    weight: Weight,
    flock: Flock,
    onWeightCardClick: (Int) -> Unit,
    unitPreference: String
) {
    val weightUiState = Weight(
        id = weight.id,
        flockUniqueId = weight.flockUniqueId,
        week = weight.week,
        weight = weight.weight,
        expectedWeight = weight.expectedWeight,
        measuredDate = weight.measuredDate
    ).toWeightUiState()

    var actualWeight by remember { mutableStateOf(weightUiState.actualWeight) }
    var standardWeight by remember { mutableStateOf(weightUiState.standard) }

     actualWeight= formatConsumption(weight.weight, unitPreference) + if (unitPreference == "Kilogram (Kg)") " Kg"
     else if (unitPreference == "Gram (g)") " g"
     else if (unitPreference == "Pound (lb)") " lb"
     else " oz"
     standardWeight= formatConsumption(weight.expectedWeight, unitPreference) + if (unitPreference == "Kilogram (Kg)") " Kg"
     else if (unitPreference == "Gram (g)") " g"
     else if (unitPreference == "Pound (lb)") " lb"
     else " oz"

    ElevatedCard(modifier = modifier
        .clickable { onWeightCardClick(flock.id) }) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(50.dp),
                imageVector = Icons.Outlined.Scale,
                contentDescription = "Average weight",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(color = Color.Cyan)
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
                    value = actualWeight,
                    weightA = 1.5f,
                )
            }

            Card {
                BaseSingleRowDetailsItem(
                    label = stringResource(R.string.standard),
                    value = standardWeight,
                    weightA = 1.5f
                )
            }

        }
    }
}

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
                contentDescription = stringResource(R.string.egg_inventory),
                colorFilter = ColorFilter.tint(color = Color.Yellow)
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