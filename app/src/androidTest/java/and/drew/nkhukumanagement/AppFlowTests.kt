package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.Expense
import and.drew.nkhukumanagement.data.FlockDao
import and.drew.nkhukumanagement.data.FlockDatabase
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.accounts.TransactionsScreenDestination
import and.drew.nkhukumanagement.userinterface.feed.FeedUiState
import and.drew.nkhukumanagement.userinterface.feed.toFeed
import and.drew.nkhukumanagement.userinterface.flock.EditFlockDestination
import and.drew.nkhukumanagement.userinterface.flock.FlockHealthScreenDestination
import and.drew.nkhukumanagement.userinterface.flock.FlockUiState
import and.drew.nkhukumanagement.userinterface.flock.toFlock
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuNavHost
import and.drew.nkhukumanagement.userinterface.overview.AccountOverviewDestination
import and.drew.nkhukumanagement.userinterface.overview.FlockOverviewDestination
import and.drew.nkhukumanagement.userinterface.planner.PlannerResultsDestination
import and.drew.nkhukumanagement.userinterface.tips.TipsArticlesListDestination
import and.drew.nkhukumanagement.userinterface.vaccination.AddVaccinationsDestination
import and.drew.nkhukumanagement.userinterface.vaccination.VaccinationUiState
import and.drew.nkhukumanagement.userinterface.vaccination.toVaccination
import and.drew.nkhukumanagement.userinterface.weight.WeightUiState
import and.drew.nkhukumanagement.userinterface.weight.toWeight
import and.drew.nkhukumanagement.utils.DateUtils
import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import okhttp3.internal.wait
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

@HiltAndroidTest
class AppFlowTests {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()
    val context = ApplicationProvider.getApplicationContext<Context>()
    lateinit var navController: TestNavHostController
    lateinit var userPrefsViewModel: UserPrefsViewModel

    @Inject
    lateinit var database: FlockDatabase
    lateinit var dao: FlockDao
    val signInViewModel = SignInViewModel()

    @Before
    fun init() {
        hiltRule.inject()
        dao = database.flockDao()
        navController = TestNavHostController(context)
        navController.navigatorProvider.addNavigator(ComposeNavigator())
        val screens = listOf(
            NavigationBarScreens.Home,
            NavigationBarScreens.Accounts,
            NavigationBarScreens.Planner,
            NavigationBarScreens.Tips,
            NavigationBarScreens.Overview
        )
        composeRule.activity.setContent {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination?.route
            val navigationBarShowing = screens.any { it.route == currentDestination }
            userPrefsViewModel = hiltViewModel()
            Scaffold(
                bottomBar = {
                    BottomNavigationForApp(
                        navController = navController,
                        screens = screens,
                        isNavigationBarShowing = navigationBarShowing,
                    )
                }
            ) { innerPadding ->
                NkhukuNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    userPrefsViewModel = userPrefsViewModel,
                    isAccountSetupSkipped = false,
                    isEmailVerified = false,
                    isUserSignedIn = false
                )
            }
        }


        val dummyFlockUiStateList = listOf(
            FlockUiState(
                id = 1,
                uniqueId = "ID1",
                batchName = "Batch1",
                breed = "Breed1",
                quantity = "100",
                cost = "2.0",
                donorFlock = "1",
                imageResourceId = R.drawable.add_flock_placeholder
            ),
            FlockUiState(
                id = 2,
                uniqueId = "ID2",
                batchName = "Batch2",
                breed = "Breed2",
                quantity = "200",
                cost = "3.0",
                donorFlock = "2",
                imageResourceId = R.drawable.add_flock_placeholder
            ),
            FlockUiState(
                id = 3,
                uniqueId = "ID3",
                batchName = "Batch3",
                breed = "Breed3",
                quantity = "300",
                cost = "4.0",
                donorFlock = "3",
                imageResourceId = R.drawable.add_flock_placeholder
            ),
            FlockUiState(
                id = 4,
                uniqueId = "ID4",
                batchName = "Batch4",
                breed = "Breed4",
                quantity = "400",
                cost = "5.0",
                donorFlock = "4",
                imageResourceId = R.drawable.add_flock_placeholder
            ),
            FlockUiState(
                id = 5,
                uniqueId = "ID5",
                batchName = "Batch5",
                breed = "Breed5",
                quantity = "500",
                cost = "6.0",
                donorFlock = "5",
                imageResourceId = R.drawable.add_flock_placeholder
            ),
            FlockUiState(
                id = 6,
                uniqueId = "ID6",
                batchName = "Batch6",
                breed = "Breed6",
                quantity = "600",
                cost = "7.0",
                donorFlock = "6",
                imageResourceId = R.drawable.add_flock_placeholder
            )
        )

        val vaccinationUiState = VaccinationUiState(
            id = 0, flockUniqueId = "",
            vaccinationNumber = 1,
            name = "",
            date = "",
            notes = ""
        )
        val vaccinationList = listOf(
            VaccinationUiState(
                vaccinationNumber = 1, name = "Gumburro",
                date = DateUtils().vaccinationDate(
                    date = LocalDate.now(), day = 9,
                    vaccinationUiState = vaccinationUiState
                )
            ),
            VaccinationUiState(
                vaccinationNumber = 2, name = "Lasota",
                date = DateUtils().vaccinationDate(
                    date = LocalDate.now(), day = 13,
                    vaccinationUiState = vaccinationUiState
                )
            )
        )
        val dateReceived = DateUtils().dateToStringLongFormat(
            LocalDate.now()
        )
        val weightList = listOf(
            WeightUiState(
                week = "Initial",
                flockUniqueID = "",
                actualWeight = "0",
                standard = "0.040",
                dateMeasured = dateReceived
            ),
            WeightUiState(
                week = "Week 1",
                flockUniqueID = "",
                actualWeight = "0",
                standard = "0.180",
                dateMeasured = DateUtils().weightDate(
                    date = LocalDate.now(),
                    day = 7,
                    weightUiState = WeightUiState()
                )
            ),
            WeightUiState(
                week = "Week 2",
                flockUniqueID = "",
                actualWeight = "0",
                standard = "0.440",
                dateMeasured = DateUtils().weightDate(
                    date = LocalDate.now(),
                    day = 14,
                    weightUiState = WeightUiState()
                )
            ),
            WeightUiState(
                week = "Week 3",
                flockUniqueID = "",
                actualWeight = "0",
                standard = "0.850",
                dateMeasured = DateUtils().weightDate(
                    date = LocalDate.now(),
                    day = 21,
                    weightUiState = WeightUiState()
                )
            )
        )

        val feedList = listOf(
            FeedUiState(
                flockUniqueID = "", week = "Week 1",
                standardConsumption =
                String.format(
                    "%.3f", 0.167 *
                            "100".toDouble()
                ),
                standardConsumptionPerBird = "0.167",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        "120".toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = LocalDate.now(),
                    day = 7,
                    feedUiState = FeedUiState()
                )
            ),
            FeedUiState(
                flockUniqueID = "", week = "Week 2",
                standardConsumption = String.format(
                    "%.3f",
                    0.375 * "110".toDouble()
                ),
                standardConsumptionPerBird = "0.375",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        "178".toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = LocalDate.now(),
                    day = 14,
                    feedUiState = FeedUiState()
                )
            ),
            FeedUiState(
                flockUniqueID = "", week = "Week 3",
                standardConsumption = String.format(
                    "%.3f",
                    0.60 * "150".toDouble()
                ),
                standardConsumptionPerBird = "0.60",
                actualConsumptionPerBird =
                String
                    .format(
                        "%.3f",
                        "200".toDouble()
                    ),
                feedingDate = DateUtils().feedDate(
                    date = LocalDate.now(),
                    day = 21,
                    feedUiState = FeedUiState()
                )
            )
        )

        val dummyAccountsSummaryList = listOf(
            AccountsSummary(
                flockUniqueID = "ID1",
                batchName = "Batch1",
                totalIncome = 1000.0,
                totalExpenses = 500.0,
                variance = 500.0
            ),
            AccountsSummary(
                flockUniqueID = "ID2",
                batchName = "Batch2",
                totalIncome = 2000.0,
                totalExpenses = 1000.0,
                variance = 1000.0
            ),
            AccountsSummary(
                flockUniqueID = "ID3",
                batchName = "Batch3",
                totalIncome = 3000.0,
                totalExpenses = 1500.0,
                variance = 1500.0
            ),
            AccountsSummary(
                flockUniqueID = "ID4",
                batchName = "Batch4",
                totalIncome = 4000.0,
                totalExpenses = 2000.0,
                variance = 2000.0
            ),
            AccountsSummary(
                flockUniqueID = "ID5",
                batchName = "Batch5",
                totalIncome = 5000.0,
                totalExpenses = 2500.0,
                variance = 2500.0
            ),
            AccountsSummary(
                flockUniqueID = "ID6",
                batchName = "Batch6",
                totalIncome = 6000.0,
                totalExpenses = 2500.0,
                variance = 3500.0
            )
        )

        runTest {
            dummyFlockUiStateList.forEach { flockUiState ->
                val expense = Expense(
                    flockUniqueID = flockUiState.getUniqueId(),
                    date = DateUtils().stringToLocalDateShortFormat(
                        DateUtils().dateToStringShortFormat(
                            DateUtils().stringToLocalDate(
                                flockUiState.getDate()
                            )
                        )
                    ),
                    expenseName = "Day Old Chicks",
                    supplier = flockUiState.breed,
                    costPerItem = flockUiState.cost.toDouble(),
                    quantity = flockUiState.quantity.toInt(),
                    totalExpense = flockUiState.totalCostOfBirds(),
                    cumulativeTotalExpense = flockUiState.totalCostOfBirds(),
                    notes = ""
                )
                dao.insertFlock(flockUiState.toFlock())
                dao.insertAccounts(dummyAccountsSummaryList[flockUiState.id - 1])
                dao.insertExpense(expense)
                vaccinationList.forEach { vaccinationState ->
                    dao.insertVaccination(
                        vaccinationState.copy(flockUniqueId = flockUiState.getUniqueId())
                            .toVaccination()
                    )
                }
                weightList.forEach { weightUiState ->
                    dao.insertWeight(
                        weightUiState.copy(flockUniqueID = flockUiState.getUniqueId()).toWeight()
                    )
                }
                feedList.forEach { feedUiState ->
                    dao.insertFeed(
                        feedUiState.copy(flockUniqueID = flockUiState.getUniqueId()).toFeed()
                    )
                }
                dummyAccountsSummaryList.forEach {
                    dao.insertAccounts(it)
                }
            }
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }


    @Test
    fun addFlockFlow_fromHomeScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()

        composeRule
            .onNodeWithContentDescription("FlockAddition")
            .performClick()

        composeRule
            .onNodeWithText("Breed")
            .performTextInput("Hybrid")

//        composeRule
//            .onNodeWithContentDescription("Hybrid", useUnmergedTree = true)
//            .performClick()

        composeRule
            .onNodeWithText("Batch name")
            .performTextInput("January batch")
        composeRule
            .onNodeWithText("Number of chicks placed")
            .performTextInput("350")
        composeRule
            .onNodeWithText("Price Per Bird")
            .performTextInput("16")
        composeRule
            .onNodeWithText("Donor flock")
            .performTextInput("5")

        composeRule
            .onNodeWithContentDescription("navigate to vaccination screen")
            .performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        TestCase.assertEquals(
            route,
            "${AddVaccinationsDestination.route}/{${AddVaccinationsDestination.flockIdArg}}"
        )
    }

    @Test
    fun detailsScreenFlowTest_toFlockHealthScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()

        composeRule
            .onNodeWithContentDescription("flockList")
            .performScrollToIndex(4)
            .performClick()

        composeRule
            .onNodeWithContentDescription("Health")
            .performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        TestCase.assertEquals(
            route,
            "${FlockHealthScreenDestination.route}/{${FlockHealthScreenDestination.flockIdArg}}"
        )
    }

    @Test
    fun detailsScreenFlowTest_toAddVaccinationsScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()

        composeRule
            .onNodeWithContentDescription("flockList")
            .performScrollToIndex(3)
            .performClick()

        composeRule
            .onNodeWithContentDescription("Vaccines")
            .performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        TestCase.assertEquals(
            route,
            "${AddVaccinationsDestination.route}/{${AddVaccinationsDestination.flockIdArg}}"
        )
    }

    @Test
    fun detailsScreenFlowTest_toFeedScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()

        composeRule
            .onNodeWithContentDescription("flockList")
            .performScrollToIndex(2)
            .performClick()

        composeRule
            .onNodeWithContentDescription("Feed")
            .performClick()

        composeRule
            .onNodeWithContentDescription("Feed list")
            .performScrollToIndex(1)
            .performClick()


        composeRule
            .onNodeWithText("Feed type")
            .performTextInput("Starter")


        composeRule
            .onNodeWithText("Quantity")
            .performTextInput("150")

        composeRule
            .onNodeWithText("Save", useUnmergedTree = true)
            .assertIsEnabled()

    }

    @Test
    fun detailsScreenFlowTest_toWeightScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()

        composeRule
            .onNodeWithContentDescription("flockList")
            .performScrollToIndex(1)
            .performClick()

        composeRule
            .onNodeWithContentDescription("Weights")
            .performClick()

        composeRule
            .onNodeWithContentDescription("Weight 1")
            .performClick()

        composeRule
            .onNodeWithText("Actual weight")
            .performTextInput("160")
        composeRule
            .onNodeWithText("Save", useUnmergedTree = true)
            .assertIsEnabled()
    }

    @Test
    fun accountsFlow_toAddIncome() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Accounts")
            .performClick()

        composeRule
            .onNodeWithContentDescription("Accounts list")
            .performScrollToIndex(2)
            .performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        TestCase.assertEquals(
            route,
            "${TransactionsScreenDestination.route}/{${TransactionsScreenDestination.accountIdArg}}"
        )

        composeRule
            .onNodeWithContentDescription("Add Income")
            .performClick()

        composeRule
            .onNodeWithText("Description")
            .performTextInput("Chicken Sales")
        composeRule
            .onNodeWithText("Customer")
            .performTextInput("Chilala")
        composeRule
            .onNodeWithText("Unit Price")
            .performTextInput("100")
        composeRule
            .onNodeWithText("Quantity")
            .performTextInput("124")
        composeRule
            .onNodeWithText("Notes")
            .performTextInput("Paid by cash")

        composeRule
            .onNodeWithContentDescription("save button", useUnmergedTree = true)
            .assertIsEnabled()
    }

    @Test
    fun accountsFlow_toAddExpense() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Accounts")
            .performClick()

        composeRule
            .onNodeWithContentDescription("Accounts list")
            .performScrollToIndex(3)
            .performClick()

        composeRule
            .onNodeWithContentDescription("horizontal pager")
            .performTouchInput {
                swipeLeft()
            }

        composeRule
            .onNodeWithContentDescription("Add Expense")
            .performClick()

        composeRule
            .onNodeWithText("Description")
            .performTextInput("Feed")
        composeRule
            .onNodeWithText("Supplier")
            .performTextInput("Novatek")
        composeRule
            .onNodeWithText("Unit Price")
            .performTextInput("505")
        composeRule
            .onNodeWithText("Quantity")
            .performTextInput("24")
        composeRule
            .onNodeWithText("Notes")
            .performTextInput("Feed for batch 2")

        composeRule
            .onNodeWithContentDescription("save button", useUnmergedTree = true)
            .assertIsEnabled()
    }

    @Test
    fun detailsScreenFlowTest_toFlockHealthScreen_toEditFlockScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()

        composeRule
            .onNodeWithContentDescription("flockList")
            .performScrollToIndex(4)
            .performClick()

        composeRule
            .onNodeWithContentDescription("Health")
            .performClick()

        composeRule
            .onNodeWithContentDescription("Edit flock fab")
            .performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        TestCase.assertEquals(
            route,
            "${EditFlockDestination.route}/{${EditFlockDestination.flockIdArg}}/{${EditFlockDestination.healthIdArg}}"
        )

    }

    @Test
    fun planner_screen_flow_test() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Planner")
            .performClick()
        composeRule
            .onNodeWithText("How many chicks would you like to order?")
            .performTextInput("1452")

        composeRule
            .onNodeWithText("Calculate", useUnmergedTree = true)
            .performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        TestCase.assertEquals(
            route,
            PlannerResultsDestination.route
        )
    }

    @Test
    fun overview_to_accountOverview_flowTest() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Overview")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Account Overview")
            .performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        TestCase.assertEquals(
            route,
            AccountOverviewDestination.route
        )
    }

    @Test
    fun overview_to_flockOverview_flowTest() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Overview")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Flock Overview")
            .performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        TestCase.assertEquals(
            route,
            FlockOverviewDestination.route
        )
    }

    /**
     * Should fail when userSignedIn is false
     */
    @Test
    fun tips_to_articlesList_flowTest() {

        composeRule
            .onNodeWithContentDescription("Tips")
            .performClick()

        composeRule
            .onNodeWithContentDescription("broodingBrooding")
            .performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        TestCase.assertEquals(
            route,
            TipsArticlesListDestination.routeWithArgs
        )
    }

    /**
     * Should pass when userSignedIn  is true and email verified is true
     * skipAccount = true
     */
    @Test
    fun tips_to_articlesList_flowTest_2() {
        composeRule
            .onNodeWithContentDescription("Tips")
            .performClick()

        composeRule
            .onNodeWithContentDescription("brooding")
            .performClick()

        val route = navController.currentBackStackEntry?.destination?.route
        TestCase.assertEquals(
            route,
            TipsArticlesListDestination.route
        )
    }

}