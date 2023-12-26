package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuNavHost
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * Top level composable that represents screens for the application
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NkhukuApp(
    navHostController: NavHostController = rememberNavController(),
    googleAuthUiClient: GoogleAuthUiClient,
    authUiClient: AuthUiClient,
    userPrefsViewModel: UserPrefsViewModel
) {

    val screens = listOf(
        NavigationBarScreens.Home,
        NavigationBarScreens.Accounts,
        NavigationBarScreens.Planner,
        NavigationBarScreens.Tips,
        NavigationBarScreens.Overview
    )
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navigationBarShowing = screens.any { it.route == currentDestination?.route }
    Scaffold(
        bottomBar = {
            BottomNavigationForApp(
                navController = navHostController,
                screens = screens,
                isNavigationBarShowing = navigationBarShowing
            )
        }
    ) { innerPadding ->
        NkhukuNavHost(
            navController = navHostController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

/**
 * Bottom navigation with 5 screens for the app
 */
@Composable
fun BottomNavigationForApp(
    navController: NavController,
    isNavigationBarShowing: Boolean = true,
    screens: List<NavigationBarScreens> = listOf(),
    currentDestination: NavDestination? = navController.currentDestination
) {

    var isIconSelected by remember { mutableStateOf(false) }


    if (isNavigationBarShowing) {
        NavigationBar(
        ) {
            screens.forEach { screen ->
                screen.isIconSelected = isIconSelected
                NavigationBarItem(
                    modifier = Modifier.semantics { contentDescription = screen.route },
                    icon = {
                        isIconSelected = currentDestination?.route == screen.route
                        if (isIconSelected) {
                            Icon(screen.iconSelected, contentDescription = "${screen.route} screen")
                        } else {
                            Icon(screen.icon, contentDescription = "${screen.route} screen")
                        }
                    },
                    label = {
                        Text(
                            stringResource(screen.resourceId),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(NavigationBarScreens.Home.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

/**
 * Appbar to display title
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlockManagementTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    onClickRemove: () -> Unit = {},
    isRemoveShowing: Boolean = false,
    onClickAdd: () -> Unit = {},
    isAddShowing: Boolean = false,
    isDoneShowing: Boolean = false,
    isDoneEnabled: Boolean = false,
    isFilterButtonEnabled: Boolean = true,
    onSaveToDatabase: () -> Unit = {},
    onClickSettings: () -> Unit = {},
    onClickFilter: () -> Unit = {}
) {
    if (canNavigateBack) {
        TopAppBar(
            modifier = modifier,
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(),
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
            actions = {

                if (isRemoveShowing) {
                    IconButton(
                        onClick = onClickRemove
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remove Vaccination"
                        )
                    }
                }

                if (isAddShowing) {
                    IconButton(
                        onClick = onClickAdd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Vaccination"
                        )
                    }
                }
                if (isDoneShowing) {
                    IconButton(
                        enabled = isDoneEnabled,
                        onClick = onSaveToDatabase
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Done"
                        )
                    }
                }
            }
        )
    } else {
        TopAppBar(title = { Text(title) }, modifier = modifier,
            actions = {
                if (title == "Home" || title == "Accounts") {
                    IconButton(
                        onClick = onClickFilter,
                        enabled = isFilterButtonEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter list"
                        )
                    }
                }

                IconButton(
                    onClick = onClickSettings
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }


            }
        )
    }
}
