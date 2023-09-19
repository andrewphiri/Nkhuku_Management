package com.example.nkhukumanagement.userinterface.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.nkhukumanagement.OverviewScreen
import com.example.nkhukumanagement.OverviewViewModel
import com.example.nkhukumanagement.userinterface.accounts.AccountsScreen
import com.example.nkhukumanagement.userinterface.accounts.AddExpenseScreen
import com.example.nkhukumanagement.userinterface.accounts.AddExpenseScreenDestination
import com.example.nkhukumanagement.userinterface.accounts.AddIncomeScreen
import com.example.nkhukumanagement.userinterface.accounts.AddIncomeScreenDestination
import com.example.nkhukumanagement.userinterface.accounts.ExpenseViewModel
import com.example.nkhukumanagement.userinterface.accounts.IncomeViewModel
import com.example.nkhukumanagement.userinterface.accounts.TransactionScreen
import com.example.nkhukumanagement.userinterface.accounts.TransactionsScreenDestination
import com.example.nkhukumanagement.userinterface.feed.FeedScreen
import com.example.nkhukumanagement.userinterface.feed.FeedScreenDestination
import com.example.nkhukumanagement.userinterface.flock.AddFlockDestination
import com.example.nkhukumanagement.userinterface.flock.AddFlockScreen
import com.example.nkhukumanagement.userinterface.flock.EditFlockDestination
import com.example.nkhukumanagement.userinterface.flock.FlockDetailsDestination
import com.example.nkhukumanagement.userinterface.flock.FlockDetailsScreen
import com.example.nkhukumanagement.userinterface.flock.FlockEditScreen
import com.example.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import com.example.nkhukumanagement.userinterface.home.HomeScreen
import com.example.nkhukumanagement.userinterface.planner.PlannerResultScreen
import com.example.nkhukumanagement.userinterface.planner.PlannerResultsDestination
import com.example.nkhukumanagement.userinterface.planner.PlannerScreen
import com.example.nkhukumanagement.userinterface.planner.PlannerViewModel
import com.example.nkhukumanagement.userinterface.tips.TipsScreen
import com.example.nkhukumanagement.userinterface.vaccination.AddVaccinationsDestination
import com.example.nkhukumanagement.userinterface.vaccination.AddVaccinationsScreen
import com.example.nkhukumanagement.userinterface.vaccination.VaccinationViewModel
import com.example.nkhukumanagement.userinterface.weight.WeightScreen
import com.example.nkhukumanagement.userinterface.weight.WeightScreenDestination

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NkhukuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vaccinationViewModel: VaccinationViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel = hiltViewModel(),
    plannerViewModel: PlannerViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = NavigationBarScreens.Home.route,
        modifier = modifier,
        route = GraphRoutes.HOME
    ) {
        composable(route = NavigationBarScreens.Home.route) {
            HomeScreen(
                navigateToAddFlock = {
                    navController.navigate(AddFlockDestination.route)
                },
                navigateToFlockDetails = { id ->
                    navController.navigate("${FlockDetailsDestination.route}/$id")
                },
                flockEntryViewModel = flockEntryViewModel,
                vaccinationViewModel = vaccinationViewModel
            )
        }

        composable(route = NavigationBarScreens.Accounts.route) {
            AccountsScreen(
                navigateToTransactionsScreen = { id ->
                    navController.navigate(route = "${TransactionsScreenDestination.route}/$id")
                }
            )
        }

        composable(route = NavigationBarScreens.Planner.route) {
            PlannerScreen(
                navigateToResultsScreen = { navController.navigate(PlannerResultsDestination.route) },
                plannerViewModel = plannerViewModel
            )
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
                navigateBack = {
                    navController.navigate(NavigationBarScreens.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                        }
                        navController.popBackStack(
                            NavigationBarScreens.Home.route,
                            inclusive = true
                        )
                    }
                },
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                vaccinationViewModel = vaccinationViewModel
            )
        }
        composable(route = PlannerResultsDestination.route) {
            PlannerResultScreen(
                onNavigateUp = { navController.navigateUp() },
                plannerViewModel = plannerViewModel
            )
        }
        detailsGraph(
            navController = navController,
            flockEntryViewModel = flockEntryViewModel,
        )
        accountDetailsGraph(
            navController = navController
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.detailsGraph(
    navController: NavHostController,
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
                },
                navigateToWeightScreen = { id ->
                    navController.navigate(route = "${WeightScreenDestination.route}/$id")
                },
                navigateToFeedScreen = { id ->
                    navController.navigate(route = "${FeedScreenDestination.route}/$id")
                }
            )
        }
        composable(
            route = EditFlockDestination.routeWithArgs,
            arguments = FlockDetailsDestination.arguments
        ) {
            FlockEditScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel
            )
        }
        composable(
            route = WeightScreenDestination.routeWithArgs,
            arguments = WeightScreenDestination.arguments
        ) {
            WeightScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel
            )
        }
        composable(
            route = FeedScreenDestination.routeWithArgs,
            arguments = FeedScreenDestination.arguments
        ) {
            FeedScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.accountDetailsGraph(
    navController: NavHostController
) {
    navigation(
        route = GraphRoutes.ACCOUNT_DETAILS,
        startDestination = TransactionsScreenDestination.route
    ) {
        composable(
            route = TransactionsScreenDestination.routeWithArgs,
            arguments = TransactionsScreenDestination.arguments
        ) {
            TransactionScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToAddIncomeScreen = { incomeId, accountId ->
                    navController.navigate("${AddIncomeScreenDestination.route}/$incomeId/$accountId")
                },
                navigateToAddExpenseScreen = { expenseId, accountId ->
                    navController.navigate(
                        "${AddExpenseScreenDestination.route}/$expenseId/$accountId")
                }
            )
        }
        composable(
            route = AddIncomeScreenDestination.routeWithArgs,
            arguments = AddIncomeScreenDestination.arguments
        ) {
            AddIncomeScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = AddExpenseScreenDestination.routeWithArgs,
            arguments = AddExpenseScreenDestination.arguments
        ) {
            AddExpenseScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}

