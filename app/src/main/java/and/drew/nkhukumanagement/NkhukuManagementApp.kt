package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.userinterface.accounts.AccountsScreenNav
import and.drew.nkhukumanagement.userinterface.home.HomeScreenNav
import and.drew.nkhukumanagement.userinterface.navigation.AccountsGraph
import and.drew.nkhukumanagement.userinterface.navigation.HomeGraph
import and.drew.nkhukumanagement.userinterface.navigation.NavigationBarRoutes
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuNavHost
import and.drew.nkhukumanagement.userinterface.navigation.OverviewGraph
import and.drew.nkhukumanagement.userinterface.navigation.PlannerGraph
import and.drew.nkhukumanagement.userinterface.navigation.TipsGraph
import and.drew.nkhukumanagement.userinterface.overview.OverviewScreenNav
import and.drew.nkhukumanagement.userinterface.planner.PlannerScreenNav
import and.drew.nkhukumanagement.userinterface.tips.TipsScreenNav
import and.drew.nkhukumanagement.utils.ContentType
import and.drew.nkhukumanagement.utils.NavigationType
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

/**
 * Top level composable that represents screens for the application
 */
@Composable
fun NkhukuApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    navigationType: NavigationType,
    contentType: ContentType,
    userPrefsViewModel: UserPrefsViewModel,
    isEmailVerified: Boolean,
    isUserSignedIn: Boolean,
    isAccountSetupSkipped: Boolean,
    unitPreference: String = "",
    bagSize: String = ""
) {

    val navBarRoutes = listOf(
        NavigationBarRoutes(name = "Home", route = HomeGraph, icon = Icons.Outlined.Home, resourceId = R.string.home),
        NavigationBarRoutes("Accounts", route =AccountsGraph, icon =  Icons.Outlined.Money, resourceId = R.string.accounts),
        NavigationBarRoutes(name ="Planner", route =PlannerGraph, icon = Icons.Outlined.Calculate, resourceId = R.string.planner),
        NavigationBarRoutes(name ="Tips", route =TipsGraph,icon =  Icons.Outlined.TipsAndUpdates, resourceId = R.string.tips),
        NavigationBarRoutes(name ="Overview", route =OverviewGraph, icon = Icons.Outlined.PieChart, resourceId = R.string.overview),
    )

    val navBackStack = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStack.value?.destination

    val screens = listOf(
        HomeScreenNav,
        AccountsScreenNav,
        PlannerScreenNav,
        TipsScreenNav,
        OverviewScreenNav
    )

    val navigationBarShowing = currentDestination
        ?.hierarchy
        ?.any {
            it.hasRoute(HomeScreenNav::class) ||
                    it.hasRoute(AccountsScreenNav::class) ||
                    it.hasRoute(PlannerScreenNav::class) ||
                    it.hasRoute(TipsScreenNav::class) ||
                    it.hasRoute(OverviewScreenNav::class)
        } == true

    Box(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.CenterVertically),
                visible = navigationType == NavigationType.NAVIGATION_RAIL
            ) {
                NavigationRailForApp(
                    navController = navController,
                    navBarRoutes = navBarRoutes,
                    isNavigationBarShowing = navigationBarShowing,
                    currentDestination = currentDestination
                )
            }
            Scaffold(
                modifier = Modifier.fillMaxSize(),

                bottomBar = {
//                    AnimatedVisibility(
//                        visible = navigationType == NavigationType.BOTTOM_NAVIGATION
//                    ) {
//                        BottomNavigationForApp(
//                            navController = navController,
//                            navBarRoutes = navBarRoutes,
//                            isNavigationBarShowing = navigationBarShowing,
//                            currentDestination = currentDestination
//                        )
//                    }
                }
            ) { innerPadding ->
                NkhukuNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    contentType = contentType,
                    userPrefsViewModel = userPrefsViewModel,
                    isEmailVerified = isEmailVerified,
                    isUserSignedIn = isUserSignedIn,
                    isAccountSetupSkipped = isAccountSetupSkipped,
                    unitPreference = unitPreference,
                    bagSize = bagSize
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
    currentDestination: NavDestination? = null,
    navBarRoutes: List<NavigationBarRoutes<out Any>> = listOf(),
) {

    if (isNavigationBarShowing) {
        NavigationBar{
            navBarRoutes.forEach { screen ->
              currentDestination?.hierarchy?.any { it.hasRoute(screen.route::class) }?.let {
                  NavigationBarItem(
                      modifier = Modifier.semantics { contentDescription = screen.name },
                      icon = {
                          Icon(
                              screen.icon,
                              contentDescription = "${screen.name} screen",
                              tint = if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                          )
                      },
                      label = {
                          Text(
                              text = screen.name,
                              style = MaterialTheme.typography.titleSmall,
                              fontSize = TextUnit(value = 10f, TextUnitType.Sp),
                              textAlign = TextAlign.Center,
                              color = if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                          )
                      },
                      selected = it,
                      onClick = {
                          navController.navigate(screen.route) {
                              // Pop up to the start destination of the graph to
                              // avoid building up a large stack of destinations
                              // on the back stack as users select items
                              popUpTo(navController.graph.startDestinationId) {
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
}

/**
 * Navigation rail with 5 screens for the app
 */
@Composable
fun NavigationRailForApp(
    modifier: Modifier = Modifier,
    navController: NavController,
    isNavigationBarShowing: Boolean = true,
    navBarRoutes: List<NavigationBarRoutes<out Any>> = listOf(),
    currentDestination: NavDestination? = null,
) {
    if (isNavigationBarShowing) {
        Box(
            modifier = modifier
                .fillMaxHeight()
                .padding(start = 8.dp, end = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            NavigationRail(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface),
            ){
               Column(
                   modifier = Modifier.fillMaxHeight(),
                  horizontalAlignment = Alignment.CenterHorizontally,
                   verticalArrangement = Arrangement.Center
               ) {
                   navBarRoutes.forEach { screen ->
                       currentDestination?.hierarchy?.any { it.hasRoute(screen.route::class) }?.let {
                           NavigationRailItem(
                               modifier = Modifier.semantics { contentDescription = screen.name },
                               icon = {
                                   Icon(
                                       screen.icon,
                                       contentDescription = "${screen.name} screen",
                                       tint = if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                       )
                               },
                               label = {
                                   Text(
                                       text = screen.name,
                                       style = MaterialTheme.typography.titleSmall,
                                       fontSize = TextUnit(value = 10f, TextUnitType.Sp),
                                       textAlign = TextAlign.Center,
                                       color = if (it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                   )
                               },
                               selected = it,
                               onClick = {
                                   navController.navigate(screen.route) {
                                       // Pop up to the start destination of the graph to
                                       // avoid building up a large stack of destinations
                                       // on the back stack as users select items
                                       popUpTo(navController.graph.startDestinationId) {
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
        }
    }
}

/**
 * Bottom navigation with 5 screens for the app
 */
//@Composable
//fun NavigationDrawerContent(
//    navController: NavController,
//    isNavigationBarShowing: Boolean = true,
//    navBarRoutes: List<NavigationBarRoutes<out Any>> = listOf()
//) {
//    if (isNavigationBarShowing) {
//        screens.forEach { screen ->
//            NavigationDrawerItem(
//                modifier = Modifier.semantics { contentDescription = screen.route },
//                icon = {
//                    Icon(screen.icon, contentDescription = "${screen.route} screen")
//                },
//                label = {
//                    Text(
//                        stringResource(screen.resourceId),
//                        style = MaterialTheme.typography.labelSmall
//                    )
//                },
//                selected = navController.currentDestination?.route == screen.route,
//                onClick = {
//                    navController.navigate(screen.route) {
//                        // Pop up to the start destination of the graph to
//                        // avoid building up a large stack of destinations
//                        // on the back stack as users select items
//                        popUpTo(NavigationBarScreens.Home.route) {
//                            saveState = true
//                        }
//                        // Avoid multiple copies of the same destination when
//                        // reselecting the same item
//                        launchSingleTop = true
//                        // Restore state when reselecting a previously selected item
//                        restoreState = true
//                    }
//                }
//            )
//        }
//    }
//}

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
    showExportButton: Boolean = false,
    isExportButtonEnabled: Boolean = false,
    onClickExportAsExcel: () -> Unit = {},
    onClickExportAsPDF: () -> Unit = {}
) {
    if (canNavigateBack) {
        TopAppBar(
            modifier = modifier,
            windowInsets = WindowInsets(0.dp),
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

                if (showExportButton) {
                    IconButton(
                        modifier = Modifier.padding(8.dp),
                        onClick = onClickExportAsExcel,
                        enabled = isExportButtonEnabled
                    ) {
                       Icon(
                           painter = painterResource(R.drawable.xlsx_icon),
                           contentDescription = "Export to Excel" ,
                           tint = Color.Unspecified
                       )
                    }

                    IconButton(
                        modifier = Modifier.padding(8.dp),
                        onClick = onClickExportAsPDF,
                        enabled = isExportButtonEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PictureAsPdf,
                            contentDescription = "Export as PDF" ,
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        )
    } else {
        TopAppBar(
            title = { Text(title) },
            modifier = modifier,
            windowInsets = WindowInsets(0.dp),
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
