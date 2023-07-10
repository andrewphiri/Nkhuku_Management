package com.example.nkhukumanagement.userinterface.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.nkhukumanagement.AccountsScreen
import com.example.nkhukumanagement.home.HomeScreen
import com.example.nkhukumanagement.OverviewScreen
import com.example.nkhukumanagement.PlannerScreen
import com.example.nkhukumanagement.TipsScreen
import com.example.nkhukumanagement.userinterface.flock.AddFlockDestination
import com.example.nkhukumanagement.userinterface.flock.AddFlockScreen
import com.example.nkhukumanagement.userinterface.flock.AddVaccinationsDestination
import com.example.nkhukumanagement.userinterface.flock.AddVaccinationsScreen
import com.example.nkhukumanagement.userinterface.flock.EditFlockDestination
import com.example.nkhukumanagement.userinterface.flock.EditFlockViewModel
import com.example.nkhukumanagement.userinterface.flock.FlockDetailsDestination
import com.example.nkhukumanagement.userinterface.flock.FlockDetailsScreen
import com.example.nkhukumanagement.userinterface.flock.FlockDetailsViewModel
import com.example.nkhukumanagement.userinterface.flock.FlockEditScreen
import com.example.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import com.example.nkhukumanagement.userinterface.flock.FlockUiState
import com.example.nkhukumanagement.userinterface.flock.VaccinationViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NkhukuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vaccinationViewModel: VaccinationViewModel = hiltViewModel()

) {
    val flockEntryViewModel: FlockEntryViewModel = hiltViewModel()
    NavHost(
        navController = navController,
        startDestination = NavigationBarScreens.Home.route,
        modifier = modifier,
        route = GraphRoutes.HOME
    ) {
        composable(route = NavigationBarScreens.Home.route) {
            HomeScreen(
                navigateToAddFlock = { navController.navigate(AddFlockDestination.route)
                },
                navigateToFlockDetails = { id ->
                    navController.navigate("${FlockDetailsDestination.route}/$id")
                },
                flockEntryViewModel = flockEntryViewModel,
                vaccinationViewModel = vaccinationViewModel
            )
        }

        composable(route = NavigationBarScreens.Accounts.route) {
            AccountsScreen()
        }

        composable(route = NavigationBarScreens.Planner.route) {
            PlannerScreen()
        }

        composable(route = NavigationBarScreens.Tips.route) {
            TipsScreen()
        }

        composable(route = NavigationBarScreens.Overview.route) {
            OverviewScreen()
        }
        composable(AddFlockDestination.route) {
            AddFlockScreen(
                onNavigateUp = {
                    navController.navigateUp()
                },
                navigateToVaccinationsScreen = {
                    val id = it.id
                    navController.navigate(route = "${AddVaccinationsDestination.route}/$id")
                                               },
                viewModel = flockEntryViewModel
            )
        }
        composable(
            route = AddVaccinationsDestination.routeWithArgs,
            arguments = AddVaccinationsDestination.argument
        ) { navBackStackEntry ->
            AddVaccinationsScreen(
                navigateBack = { navController.navigate(NavigationBarScreens.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = false
                    }
                    navController.popBackStack(NavigationBarScreens.Home.route, inclusive = true)
                    }
                 },
                onNavigateUp = {navController.navigateUp()},
                flockEntryViewModel = flockEntryViewModel,
                vaccinationViewModel = vaccinationViewModel
            )
        }
        detailsGraph(navController = navController,
            flockEntryViewModel = flockEntryViewModel,
            )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.detailsGraph (navController: NavHostController,
                                  flockEntryViewModel: FlockEntryViewModel,
                                  ) {
    navigation(
        route = GraphRoutes.DETAILS,
        startDestination = FlockDetailsDestination.route
    ) {
        composable(
            route = FlockDetailsDestination.routeWithArgs,
            arguments = FlockDetailsDestination.arguments
        ) {
            FlockDetailsScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                navigateToFlockEdit = { id ->
                   navController.navigate("${EditFlockDestination.route}/$id")
                },
                navigateToVaccinationScreen = { id ->
                    navController.navigate("${AddVaccinationsDestination.route}/$id")
                }
            )
        }
        composable(
            route = EditFlockDestination.routeWithArgs,
        arguments = FlockDetailsDestination.arguments
        ) {
            FlockEditScreen(
                onNavigateUp = {navController.navigateUp()},
                flockEntryViewModel = flockEntryViewModel
            )
        }
    }
}
