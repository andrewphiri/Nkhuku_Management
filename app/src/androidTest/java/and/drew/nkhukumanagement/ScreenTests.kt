package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.data.AccountsSummary
import and.drew.nkhukumanagement.data.Flock
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.accounts.MainAccountsScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsDestination
import and.drew.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import and.drew.nkhukumanagement.userinterface.flock.MainAddFlockScreen
import and.drew.nkhukumanagement.userinterface.home.MainHomeScreen
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuNavHost
import and.drew.nkhukumanagement.utils.ContentType
import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@HiltAndroidTest
class ScreenTests {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()
    val context = ApplicationProvider.getApplicationContext<Context>()
    lateinit var navController: TestNavHostController
    lateinit var viewModel: FlockEntryViewModel
    lateinit var userPrefsViewModel: UserPrefsViewModel

    @Before
    fun init() {
        hiltRule.inject()
        navController = TestNavHostController(context)
        navController.navigatorProvider.addNavigator(ComposeNavigator())

    }

    @Test
    fun homeScreenTest() {
        val dummyFlockList = listOf(
            Flock(
                1,
                "ID1",
                "Batch1",
                "Breed1",
                LocalDate.now(),
                100,
                2.0,
                100,
                1,
                0,
                R.drawable.add_flock_placeholder,
                0,
                true
            ),
            Flock(
                2,
                "ID2",
                "Batch2",
                "Breed2",
                LocalDate.now(),
                200,
                3.0,
                200,
                2,
                0,
                R.drawable.add_flock_placeholder,
                0,
                true
            ),
            Flock(
                3,
                "ID3",
                "Batch3",
                "Breed3",
                LocalDate.now(),
                300,
                4.0,
                300,
                3,
                0,
                R.drawable.add_flock_placeholder,
                0,
                true
            ),
            Flock(
                4,
                "ID4",
                "Batch4",
                "Breed4",
                LocalDate.now(),
                100,
                2.0,
                100,
                1,
                0,
                R.drawable.add_flock_placeholder,
                0,
                true
            ),
            Flock(
                5,
                "ID5",
                "Batch5",
                "Breed5",
                LocalDate.now(),
                200,
                3.0,
                200,
                2,
                0,
                R.drawable.add_flock_placeholder,
                0,
                true
            ),
            Flock(
                6,
                "ID6",
                "Batch6",
                "Breed6",
                LocalDate.now(),
                300,
                4.0,
                300,
                3,
                0,
                R.drawable.add_flock_placeholder,
                0,
                true
            )
        )

        composeRule.setContent {
            MainHomeScreen(
                navigateToAddFlock = {},
                navigateToFlockDetails = {},
                onClickSettings = {},
                deleteFlock = {},
                resetFlock = {},
                onClose = {},
                flocks = dummyFlockList,
                contentType = ContentType.LIST_ONLY
            )

        }
        composeRule
            .onNodeWithContentDescription("FlockAddition")
            .assertExists()

        composeRule
            .onNodeWithContentDescription("flockList")
            .performScrollToIndex(4)
            .assertExists()
    }

    @Test
    fun addFlockScreen() {
        composeRule.activity.setContent {
            viewModel = hiltViewModel()
            MainAddFlockScreen(
                onNavigateUp = {},
                navigateToVaccinationsScreen = {},
                onItemValueChange = viewModel::updateUiState,
                flockUiState = viewModel.flockUiState,
                canNavigateBack = true,
                currencySymbol = "ZMW",
                contentType = ContentType.LIST_ONLY
            )
        }

        composeRule
            .onNodeWithContentDescription("breed options")
            .performClick()


        composeRule
            .onNodeWithContentDescription("Ross", useUnmergedTree = true)
            .performClick()

        composeRule
            .onNodeWithContentDescription("batch")
            .performTextInput("January batch")
        composeRule
            .onNodeWithContentDescription("quantity")
            .performTextInput("350")
        composeRule
            .onNodeWithContentDescription("price per bird")
            .performTextInput("16")
        composeRule
            .onNodeWithContentDescription("donor flock")
            .performTextInput("5")

        composeRule
            .onNodeWithContentDescription("navigate to vaccination screen")
            .performClick()
            .assertExists()
    }
    @Test
    fun detailsScreenTest() {
        composeRule.activity.setContent {
            userPrefsViewModel = hiltViewModel()
            NkhukuNavHost(
                navController = navController,
                userPrefsViewModel = userPrefsViewModel,
                isAccountSetupSkipped = false,
                isEmailVerified = false,
                isUserSignedIn = false
            )
        }
        composeRule
            .onNodeWithText("Skip")
            .performClick()

        composeRule
            .onNodeWithContentDescription("flockList")
            .performScrollToIndex(4)
            .performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "${FlockDetailsDestination.route}/{${FlockDetailsDestination.flockId}}")
    }

    @Test
    fun accountScreenTest() {
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
            )
        )

        composeRule.activity.setContent {
            MainAccountsScreen(
                accountsSummaryList = dummyAccountsSummaryList,
                navigateToTransactionsScreen = {},
                canNavigateBack = false,
                onClickSettings = {},
                currencyLocale = "ZMW"
            )
        }

        composeRule
            .onNodeWithContentDescription("Accounts list")
            .performScrollToIndex(3)
            .assertIsDisplayed()
    }
}