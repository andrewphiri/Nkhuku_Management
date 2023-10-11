package and.drew.nkhukumanagement.userinterface.navigation

import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.userinterface.accounts.AccountsScreen
import and.drew.nkhukumanagement.userinterface.accounts.AddExpenseScreen
import and.drew.nkhukumanagement.userinterface.accounts.AddExpenseScreenDestination
import and.drew.nkhukumanagement.userinterface.accounts.AddIncomeScreen
import and.drew.nkhukumanagement.userinterface.accounts.AddIncomeScreenDestination
import and.drew.nkhukumanagement.userinterface.accounts.TransactionScreen
import and.drew.nkhukumanagement.userinterface.accounts.TransactionsScreenDestination
import and.drew.nkhukumanagement.userinterface.feed.FeedScreen
import and.drew.nkhukumanagement.userinterface.feed.FeedScreenDestination
import and.drew.nkhukumanagement.userinterface.flock.AddFlockDestination
import and.drew.nkhukumanagement.userinterface.flock.AddFlockScreen
import and.drew.nkhukumanagement.userinterface.flock.EditFlockDestination
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsDestination
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockEditScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockHealthScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockHealthScreenDestination
import and.drew.nkhukumanagement.userinterface.home.HomeScreen
import and.drew.nkhukumanagement.userinterface.login.AccountSetupDestination
import and.drew.nkhukumanagement.userinterface.login.AccountSetupScreen
import and.drew.nkhukumanagement.userinterface.overview.AccountOverviewDestination
import and.drew.nkhukumanagement.userinterface.overview.AccountOverviewScreen
import and.drew.nkhukumanagement.userinterface.overview.FlockOverviewDestination
import and.drew.nkhukumanagement.userinterface.overview.FlockOverviewScreen
import and.drew.nkhukumanagement.userinterface.overview.OverviewScreen
import and.drew.nkhukumanagement.userinterface.planner.PlannerResultScreen
import and.drew.nkhukumanagement.userinterface.planner.PlannerResultsDestination
import and.drew.nkhukumanagement.userinterface.planner.PlannerScreen
import and.drew.nkhukumanagement.userinterface.planner.PlannerViewModel
import and.drew.nkhukumanagement.userinterface.tips.ReadArticleDestination
import and.drew.nkhukumanagement.userinterface.tips.ReadArticleScreen
import and.drew.nkhukumanagement.userinterface.tips.TipsArticlesListDestination
import and.drew.nkhukumanagement.userinterface.tips.TipsArticlesListScreen
import and.drew.nkhukumanagement.userinterface.tips.TipsScreen
import and.drew.nkhukumanagement.userinterface.vaccination.AddVaccinationsDestination
import and.drew.nkhukumanagement.userinterface.vaccination.AddVaccinationsScreen
import and.drew.nkhukumanagement.userinterface.vaccination.VaccinationViewModel
import and.drew.nkhukumanagement.userinterface.weight.WeightScreen
import and.drew.nkhukumanagement.userinterface.weight.WeightScreenDestination
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NkhukuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vaccinationViewModel: VaccinationViewModel = hiltViewModel(),
    flockEntryViewModel: FlockEntryViewModel = hiltViewModel(),
    plannerViewModel: PlannerViewModel = viewModel(),
    googleAuthUiClient: GoogleAuthUiClient,
    authUiClient: AuthUiClient,

    ) {
    val coroutineScope = rememberCoroutineScope()
    val signInViewModel = viewModel<SignInViewModel>()
    LaunchedEffect(signInViewModel.userLoggedIn) {
        signInViewModel.setUserLoggedIn(loggedIn = googleAuthUiClient.getSignedInUser() != null)
    }
    val userSignedIn by signInViewModel.userLoggedIn.collectAsState()
    signInViewModel.setUserLoggedIn(
        loggedIn = googleAuthUiClient.getSignedInUser() != null
    )
    if (userSignedIn) {
        // User is signed in, show the HomeGraph
        NavHost(
            navController = navController,
            startDestination = GraphRoutes.HOME,
            modifier = modifier,
            route = GraphRoutes.ROOT
        ) {
            loginGraph(
                navController = navController,
                signInViewModel = signInViewModel,
                googleAuthUiClient = googleAuthUiClient,
                authUiClient = authUiClient
            )

            homeGraph(
                navController = navController,
                flockEntryViewModel = flockEntryViewModel,
                vaccinationViewModel = vaccinationViewModel,
                plannerViewModel = plannerViewModel,
                onClickSignOut = {
                    coroutineScope.launch {
                        googleAuthUiClient.signOut()
                        signInViewModel.resetState()
                        signInViewModel.setUserLoggedIn(false)
                    }.invokeOnCompletion { navController.navigate(GraphRoutes.AUTH) }
                }
            )
            detailsGraph(
                navController = navController,
                flockEntryViewModel = flockEntryViewModel,
            )
            accountDetailsGraph(
                navController = navController
            )
        }
    } else {
        // User is not signed in, show the LoginGraph
        NavHost(
            navController = navController,
            startDestination = GraphRoutes.AUTH,
            modifier = modifier,
            route = GraphRoutes.ROOT
        ) {
            loginGraph(
                navController = navController,
                signInViewModel = signInViewModel,
                googleAuthUiClient = googleAuthUiClient,
                authUiClient = authUiClient
            )

            homeGraph(
                navController = navController,
                flockEntryViewModel = flockEntryViewModel,
                vaccinationViewModel = vaccinationViewModel,
                plannerViewModel = plannerViewModel,
                onClickSignOut = {
                }
            )
            detailsGraph(
                navController = navController,
                flockEntryViewModel = flockEntryViewModel,
            )
            accountDetailsGraph(
                navController = navController
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.loginGraph(
    navController: NavHostController,
    signInViewModel: SignInViewModel,
    googleAuthUiClient: GoogleAuthUiClient,
    authUiClient: AuthUiClient
) {
    navigation(
        route = GraphRoutes.AUTH,
        startDestination = AccountSetupDestination.route
    ) {
        composable(
            route = AccountSetupDestination.route
        ) {
            val state by signInViewModel.state.collectAsState()
            AccountSetupScreen(
                navigateToHome = {
                    navController.navigate(route = GraphRoutes.HOME) {
                        popUpTo(GraphRoutes.AUTH) {
                            inclusive = true
                        }
                    }
                },
                googleAuthUiClient = googleAuthUiClient,
                authUiClient = authUiClient,
                signInViewModel = signInViewModel,
                state = state
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    flockEntryViewModel: FlockEntryViewModel,
    vaccinationViewModel: VaccinationViewModel,
    plannerViewModel: PlannerViewModel,
    onClickSignOut: () -> Unit
) {
    navigation(
        route = GraphRoutes.HOME,
        startDestination = NavigationBarScreens.Home.route
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
                vaccinationViewModel = vaccinationViewModel,
                onSignOut = onClickSignOut

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
            TipsScreen(
                navigateToArticlesListScreen = { title, id ->
                    navController.navigate("${TipsArticlesListDestination.route}/$title/$id")
                }
            )
        }
        composable(
            route = TipsArticlesListDestination.routeWithArgs,
            arguments = TipsArticlesListDestination.arguments
        ) {
            TipsArticlesListScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToReadArticle = { categoryId, articleId ->
                    navController.navigate("${ReadArticleDestination.route}/$categoryId/$articleId")
                }
            )

        }

        composable(
            route = ReadArticleDestination.routeWithArgs,
            arguments = ReadArticleDestination.arguments
        ) {
            ReadArticleScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(route = NavigationBarScreens.Overview.route) {
            OverviewScreen(
                navigateToAccountOverviewScreen = {
                    navController.navigate(route = AccountOverviewDestination.route)
                },
                navigateToFlockOverviewScreen = {
                    navController.navigate(FlockOverviewDestination.route)
                }
            )
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
        composable(route = AccountOverviewDestination.route) {
            AccountOverviewScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(route = FlockOverviewDestination.route) {
            FlockOverviewScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.detailsGraph(
    navController: NavHostController,
    flockEntryViewModel: FlockEntryViewModel,
) {
    navigation(
        route = GraphRoutes.DETAILS,
        startDestination = FlockDetailsDestination.routeWithArgs
    ) {
        composable(
            route = FlockDetailsDestination.routeWithArgs,
            arguments = FlockDetailsDestination.arguments,
            deepLinks = FlockDetailsDestination.deepLink
        ) {
            FlockDetailsScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                navigateToFlockHealthScreen = { id ->
                    navController.navigate("${FlockHealthScreenDestination.route}/$id")
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
            route = FlockHealthScreenDestination.routeWithArgs,
            arguments = FlockHealthScreenDestination.arguments
        ) {
            FlockHealthScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToFlockEditScreen = { flockId, healthId ->
                    navController.navigate("${EditFlockDestination.route}/$flockId/$healthId")
                }
            )
        }
        composable(
            route = EditFlockDestination.routeWithArgs,
            arguments = EditFlockDestination.arguments
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

