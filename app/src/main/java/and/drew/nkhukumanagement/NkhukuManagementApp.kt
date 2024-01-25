package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuNavHost
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.NavigationType
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * Top level composable that represents screens for the application
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NkhukuApp(
    modifier: Modifier = Modifier,
    navHostController: NavHostController = rememberNavController(),
    navigationType: NavigationType,
    contentType: ContentType,
    userPrefsViewModel: UserPrefsViewModel,
    isEmailVerified: Boolean,
    isUserSignedIn: Boolean,
    isAccountSetupSkipped: Boolean,
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

    Box(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = navigationType == NavigationType.NAVIGATION_RAIL
            ) {
                NavigationRailForApp(
                    navController = navHostController,
                    screens = screens,
                    isNavigationBarShowing = navigationBarShowing,
                )
            }
            Scaffold(
                bottomBar = {
                    AnimatedVisibility(
                        visible = navigationType == NavigationType.BOTTOM_NAVIGATION
                    ) {
                        BottomNavigationForApp(
                            navController = navHostController,
                            screens = screens,
                            isNavigationBarShowing = navigationBarShowing
                        )
                    }
                }
            ) { innerPadding ->
                NkhukuNavHost(
                    navController = navHostController,
                    modifier = Modifier.padding(innerPadding),
                    contentType = contentType,
                    userPrefsViewModel = userPrefsViewModel,
                    isEmailVerified = isEmailVerified,
                    isUserSignedIn = isUserSignedIn,
                    isAccountSetupSkipped = isAccountSetupSkipped
                )
            }
        }
    }
}

/**
 * Bottom navigation with 5 screens for the app
 */
@Composable
fun BottomNavigationForApp(
    navController: NavController,
    isNavigationBarShowing: Boolean = true,
    screens: List<NavigationBarScreens> = listOf()
) {
    if (isNavigationBarShowing) {
        NavigationBar {
            screens.forEach { screen ->
                NavigationBarItem(
                    modifier = Modifier.semantics { contentDescription = screen.route },
                    icon = {
                        Icon(screen.icon, contentDescription = "${screen.route} screen")
                    },
                    label = {
                        Text(
                            stringResource(screen.resourceId),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    selected = navController.currentDestination?.route == screen.route,
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
 * Navigation rail with 5 screens for the app
 */
@Composable
fun NavigationRailForApp(
    navController: NavController,
    isNavigationBarShowing: Boolean = true,
    screens: List<NavigationBarScreens> = listOf()
) {
    if (isNavigationBarShowing) {
        NavigationRail {
            Spacer(modifier = Modifier.weight(1f))
            screens.forEach { screen ->
                NavigationRailItem(
                    modifier = Modifier.semantics { contentDescription = screen.route },
                    alwaysShowLabel = false,
                    icon = {
                        Icon(screen.icon, contentDescription = "${screen.route} screen")
                    },
                    selected = navController.currentDestination?.route == screen.route,
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
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

/**
 * Bottom navigation with 5 screens for the app
 */
@Composable
fun NavigationDrawerContent(
    navController: NavController,
    isNavigationBarShowing: Boolean = true,
    screens: List<NavigationBarScreens> = listOf()
) {
    if (isNavigationBarShowing) {
        screens.forEach { screen ->
            NavigationDrawerItem(
                modifier = Modifier.semantics { contentDescription = screen.route },
                icon = {
                    Icon(screen.icon, contentDescription = "${screen.route} screen")
                },
                label = {
                    Text(
                        stringResource(screen.resourceId),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = navController.currentDestination?.route == screen.route,
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
    onClickFilter: () -> Unit = {},
    contentType: ContentType,
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
                if (title == "Home" || title == "Accounts" || title == "Planner" || title == "Overview" || title == "Tips") {
                    IconButton(
                        onClick = onClickSettings
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                } else {
                    if (contentType != ContentType.LIST_AND_DETAIL) {
                        IconButton(
                            onClick = onClickSettings
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                }
            }
        )
    }
}
