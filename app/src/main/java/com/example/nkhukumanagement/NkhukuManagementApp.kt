package com.example.nkhukumanagement

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nkhukumanagement.userinterface.navigation.NavigationBarScreens
import com.example.nkhukumanagement.userinterface.navigation.NkhukuNavHost

/**
 * Top level composable that represents screens for the appilication
 */
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NkhukuApp(navHostController: NavHostController = rememberNavController()){
//    var title by remember { mutableStateOf("") }
//    LaunchedEffect(navHostController.currentBackStackEntryFlow) {
//        navHostController.currentBackStackEntryFlow.collect {
//            title = it.destination.route ?: ""
//        }
//    }
    Scaffold(
//        topBar = {
//            FlockManagementTopAppBar(title = title)
//        },
        bottomBar = {
            BottomNavigationForApp(navHostController)
        }
    ) { innerPadding ->
        NkhukuNavHost(
            navController = navHostController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Bottom navigation with 5 screens for the app
 */
@Composable
fun BottomNavigationForApp(navController: NavController) {
    val items = listOf(
        NavigationBarScreens.Home,
        NavigationBarScreens.Accounts,
        NavigationBarScreens.Planner,
        NavigationBarScreens.Tips,
        NavigationBarScreens.Overview
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val navigationBarShowing = items.any { it.route == currentDestination?.route }
    var isIconSelected by remember { mutableStateOf(false) }

    if (navigationBarShowing) {
        NavigationBar() {
            items.forEach { screen ->
                screen.isIconSelected = isIconSelected
                NavigationBarItem(
                    icon = {
                        isIconSelected = currentDestination?.route == screen.route
                        if (isIconSelected){
                            Icon(screen.iconSelected, contentDescription = screen.route)
                        } else {
                            Icon(screen.icon, contentDescription = screen.route)
                        }
                    },
                    label = { Text(stringResource(screen.resourceId)) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    colors = NavigationBarItemDefaults.colors(indicatorColor = MaterialTheme.colorScheme.secondary),
                    onClick = {
                        navController.navigate(screen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
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
    navigateUp: () -> Unit = {}
) {
    if (canNavigateBack) {
        TopAppBar(
            modifier = modifier,
            title = { Text(title) },
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            }
        )
    } else {
        TopAppBar(title = { Text(title) }, modifier = modifier)
    }
}
