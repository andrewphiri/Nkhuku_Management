package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuNavHost
import and.drew.nkhukumanagement.utils.Constants
import android.content.Context
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
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

    lateinit var navController: TestNavHostController
    var context = ApplicationProvider.getApplicationContext<Context>()
    lateinit var userPrefsViewModel: UserPrefsViewModel

    @Before
    fun setupNavHost() {
        hiltRule.inject()
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
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry.value?.destination?.route
            val navigationBarShowing = screens.any { it.route == currentDestination }
            userPrefsViewModel = hiltViewModel()

            androidx.compose.material3.Scaffold(
                bottomBar = {
                    BottomNavigationForApp(
                        navController = navController,
                        screens = screens,
                        isNavigationBarShowing = navigationBarShowing
                    )
                }
            ) { pad ->
                NkhukuNavHost(
                    modifier = Modifier.padding(pad),
                    navController = navController,
                    userPrefsViewModel = userPrefsViewModel,
                    isAccountSetupSkipped = false,
                    isEmailVerified = false,
                    isUserSignedIn = false
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
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "Home")
    }

    @Test
    fun verifyAccountsScreen() {
        composeRule
            .onNodeWithText("Skip")
            .performClick()
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
        composeRule
            .onNodeWithContentDescription(NavigationBarScreens.Overview.route)
            .performClick()
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "Overview")
    }

}