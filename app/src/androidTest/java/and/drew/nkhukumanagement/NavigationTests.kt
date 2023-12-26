package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuNavHost
import and.drew.nkhukumanagement.utils.Constants
import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

@HiltAndroidTest
class NavigationTests {
    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    //    @get:Rule(order = 1)
//    val composeRule = createComposeRule()
    lateinit var navController: TestNavHostController
    var context = ApplicationProvider.getApplicationContext<Context>()

    @Before
    fun setupNavHost() {
        hiltRule.inject()
        val screens = listOf(
            NavigationBarScreens.Home,
            NavigationBarScreens.Accounts,
            NavigationBarScreens.Planner,
            NavigationBarScreens.Tips,
            NavigationBarScreens.Overview
        )

        composeRule.activity.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            composeRule.mainClock.autoAdvance = false
            val currentDestination = navBackStackEntry.value?.destination
            Scaffold(
                bottomBar = {
                    BottomNavigationForApp(
                        navController = navController,
                        screens = screens,
                        currentDestination = currentDestination,
                        isNavigationBarShowing = true
                    )
                }
            ) { padding ->
                NkhukuNavHost(
                    modifier = Modifier.padding(padding),
                    navController = navController
                )
            }
        }
    }

    @After
    fun resetPreferences() {
        File(context.filesDir, Constants.DATA_STORE_FILE_NAME).deleteRecursively()
    }

    @Test
    fun verifyStartDestination() {
        composeRule
            .onNodeWithContentDescription("login")
            .assertIsDisplayed()
    }

    @Test
    fun verifyHomeScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithContentDescription("Home")
            .assertIsDisplayed()
    }

    @Test
    fun verifyAccountsScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule.mainClock.autoAdvance = false
        composeRule
            .onNodeWithContentDescription(NavigationBarScreens.Accounts.route)
            .performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "Accounts")
    }

    @Test
    fun verifyPlannerScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule.mainClock.autoAdvance = false

        composeRule
            .onNodeWithContentDescription(NavigationBarScreens.Planner.route)
            .performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "Planner")
    }

    @Test
    fun verifyTipsScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule.mainClock.autoAdvance = false
        composeRule
            .onNodeWithContentDescription(NavigationBarScreens.Tips.route)
            .performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "Tips")
    }

    @Test
    fun verifyOverviewScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule.mainClock.autoAdvance = false
        composeRule
            .onNodeWithContentDescription(NavigationBarScreens.Overview.route)
            .performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "Accounts")
    }

    @Test
    fun addNewFlockFlow() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
        composeRule.mainClock.autoAdvance = false
        composeRule.mainClock.advanceTimeBy(1000L)
        composeRule.mainClock.autoAdvance = true
//        composeRule.onNodeWithContentDescription("Home")
//            .performClick()
        composeRule
            .onNodeWithContentDescription("FlockAddition")
            .performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals("Add flock screen", route)
//        composeRule
//            .onNodeWithContentDescription("batch")
//            .performTextInput("January batch")
//        composeRule
//            .onNodeWithContentDescription("quantity")
//            .performTextInput("350")
//        composeRule
//            .onNodeWithContentDescription("price per bird")
//            .performTextInput("16")
//        composeRule
//            .onNodeWithContentDescription("donor flock")
//            .performTextInput("5")
//
//        composeRule
//            .onNodeWithContentDescription("navigate to vaccination screen")
//            .performClick()
//        val route = navController.currentBackStackEntry?.destination?.route
//        assertEquals(route,"Vaccination Screen")
    }
}