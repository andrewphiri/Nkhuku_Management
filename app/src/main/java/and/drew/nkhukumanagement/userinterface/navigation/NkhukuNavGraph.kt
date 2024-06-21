package and.drew.nkhukumanagement.userinterface.navigation

import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.settings.AccountInfoScreen
import and.drew.nkhukumanagement.settings.AccountInformationDestination
import and.drew.nkhukumanagement.settings.SettingsDestination
import and.drew.nkhukumanagement.settings.SettingsScreen
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
import and.drew.nkhukumanagement.userinterface.flock.EditEggsDestination
import and.drew.nkhukumanagement.userinterface.flock.EditFlockDestination
import and.drew.nkhukumanagement.userinterface.flock.EggsEditScreen
import and.drew.nkhukumanagement.userinterface.flock.EggsInventoryScreen
import and.drew.nkhukumanagement.userinterface.flock.EggsInventoryScreenDestination
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsDestination
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockEditScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockHealthScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockHealthScreenDestination
import and.drew.nkhukumanagement.userinterface.home.HomeScreen
import and.drew.nkhukumanagement.userinterface.login.AccountSetupDestination
import and.drew.nkhukumanagement.userinterface.login.AccountSetupScreen
import and.drew.nkhukumanagement.userinterface.login.AuthenticateScreen
import and.drew.nkhukumanagement.userinterface.login.ReauthenticationScreenDestination
import and.drew.nkhukumanagement.userinterface.login.ResetPasswordDestination
import and.drew.nkhukumanagement.userinterface.login.ResetPasswordScreen
import and.drew.nkhukumanagement.userinterface.login.VerifyEmailDestination
import and.drew.nkhukumanagement.userinterface.login.VerifyEmailScreen
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
import and.drew.nkhukumanagement.utils.ContentType
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.google.android.gms.auth.api.identity.Identity

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NkhukuNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userPrefsViewModel: UserPrefsViewModel,
    contentType: ContentType = ContentType.LIST_ONLY,
    isEmailVerified: Boolean,
    isUserSignedIn: Boolean,
    isAccountSetupSkipped: Boolean,
) {
    val appContext = LocalContext.current
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = appContext,
            oneTapClient = Identity.getSignInClient(appContext)
        )
    }
    val authUiClient by lazy {
        AuthUiClient(
            appContext,
            oneTapClient = Identity.getSignInClient(appContext)
        )
    }
    val vaccinationViewModel: VaccinationViewModel = hiltViewModel()
    val flockEntryViewModel: FlockEntryViewModel = hiltViewModel()
    val plannerViewModel: PlannerViewModel = hiltViewModel()
    val signInViewModel = viewModel<SignInViewModel>()
//
//        signInViewModel.setUserLoggedIn(loggedIn = googleAuthUiClient.getSignedInUser() != null)
//        signInViewModel.setEmailVerification(emailVerified = authUiClient.isEmailVerified())

    if ((isUserSignedIn && isEmailVerified) || isAccountSetupSkipped) {
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
                authUiClient = authUiClient,
                isEmailVerified = isEmailVerified,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )

            loginGraphVerification(
                navController = navController,
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                contentType = contentType
            )

            homeGraph(
                navController = navController,
                flockEntryViewModel = flockEntryViewModel,
                vaccinationViewModel = vaccinationViewModel,
                plannerViewModel = plannerViewModel,
                userPrefsViewModel = userPrefsViewModel,
                onClickSettings = {
                    navController.navigate(SettingsDestination.route)
                },
                googleAuthUiClient = googleAuthUiClient,
                contentType = contentType,
                isUserSignedIn = isUserSignedIn
            )
            detailsGraph(
                navController = navController,
                flockEntryViewModel = flockEntryViewModel,
                contentType = contentType
            )
            accountDetailsGraph(
                navController = navController,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
            settingsGraph(
                navController = navController,
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                googleAuthUiClient = googleAuthUiClient,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }

    } else if (isUserSignedIn) {
        NavHost(
            navController = navController,
            startDestination = GraphRoutes.VERIFICATION,
            modifier = modifier,
            route = GraphRoutes.ROOT
        ) {
            loginGraphVerification(
                navController = navController,
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                contentType = contentType
            )
            loginGraph(
                navController = navController,
                signInViewModel = signInViewModel,
                googleAuthUiClient = googleAuthUiClient,
                authUiClient = authUiClient,
                isEmailVerified = isEmailVerified,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )

            homeGraph(
                navController = navController,
                flockEntryViewModel = flockEntryViewModel,
                vaccinationViewModel = vaccinationViewModel,
                plannerViewModel = plannerViewModel,
                userPrefsViewModel = userPrefsViewModel,
                onClickSettings = {
                    navController.navigate(SettingsDestination.route)
                },
                googleAuthUiClient = googleAuthUiClient,
                contentType = contentType,
                isUserSignedIn = isUserSignedIn
            )
            detailsGraph(
                navController = navController,
                flockEntryViewModel = flockEntryViewModel,
                contentType = contentType
            )
            accountDetailsGraph(
                navController = navController,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
            settingsGraph(
                navController = navController,
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                googleAuthUiClient = googleAuthUiClient,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
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
                authUiClient = authUiClient,
                isEmailVerified = isEmailVerified,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )

            loginGraphVerification(
                navController = navController,
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                contentType = contentType
            )

            homeGraph(
                navController = navController,
                flockEntryViewModel = flockEntryViewModel,
                vaccinationViewModel = vaccinationViewModel,
                plannerViewModel = plannerViewModel,
                userPrefsViewModel = userPrefsViewModel,
                onClickSettings = {
                    navController.navigate(SettingsDestination.route)
                },
                googleAuthUiClient = googleAuthUiClient,
                contentType = contentType,
                isUserSignedIn = isUserSignedIn
            )
            detailsGraph(
                navController = navController,
                flockEntryViewModel = flockEntryViewModel,
                contentType = contentType
            )
            accountDetailsGraph(
                navController = navController,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
            settingsGraph(
                navController = navController,
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                googleAuthUiClient = googleAuthUiClient,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.loginGraph(
    navController: NavHostController,
    signInViewModel: SignInViewModel,
    googleAuthUiClient: GoogleAuthUiClient,
    authUiClient: AuthUiClient,
    isEmailVerified: Boolean,
    userPrefsViewModel: UserPrefsViewModel,
    contentType: ContentType
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
                userPrefsViewModel = userPrefsViewModel,
                navigateToVerificationScreen = {
                    navController.navigate(route = GraphRoutes.VERIFICATION) {
                        popUpTo(GraphRoutes.AUTH) {
                            inclusive = true
                        }
                    }
                },
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
                state = state,
                isEmailVerified = isEmailVerified,
                onClickForgotPassword = {
                    navController.navigate(route = ResetPasswordDestination.route)
                },
                onClickSkipAccountSetup = {
                    navController.navigate(route = GraphRoutes.HOME) {
                        popUpTo(GraphRoutes.AUTH) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = ResetPasswordDestination.route) {
            ResetPasswordScreen(
                onNavigateUp = {
                    navController.navigate(route = AccountSetupDestination.route) {
                        popUpTo(route = ResetPasswordDestination.route) {
                            inclusive = true
                        }
                    }
                },
                navigateToAccountSetupScreen = {
                    navController.navigate(route = AccountSetupDestination.route)
                },
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                contentType = contentType
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.loginGraphVerification(
    navController: NavHostController,
    signInViewModel: SignInViewModel,
    authUiClient: AuthUiClient,
    contentType: ContentType
) {
    navigation(
        route = GraphRoutes.VERIFICATION,
        startDestination = VerifyEmailDestination.route
    ) {
        composable(
            route = VerifyEmailDestination.route
        ) {
            VerifyEmailScreen(
                authUiClient = authUiClient,
                navigateToHome = {
                    navController.navigate(route = GraphRoutes.HOME) {
                        popUpTo(GraphRoutes.VERIFICATION) {
                            inclusive = true
                        }
                    }
                },
                signInViewModel = signInViewModel,
                onClickSettings = {
                    navController.navigate(SettingsDestination.route)
                },
                contentType = contentType
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
    userPrefsViewModel: UserPrefsViewModel,
    onClickSettings: () -> Unit,
    googleAuthUiClient: GoogleAuthUiClient,
    contentType: ContentType,
    isUserSignedIn: Boolean
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
                onClickSettings = onClickSettings,
                contentType = contentType,
                userPrefsViewModel = userPrefsViewModel,
            )
        }

        composable(route = NavigationBarScreens.Accounts.route) {
            AccountsScreen(
                navigateToTransactionsScreen = { id ->
                    navController.navigate(route = "${TransactionsScreenDestination.route}/$id")
                },
                onClickSettings = onClickSettings,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }

        composable(route = NavigationBarScreens.Planner.route) {
            PlannerScreen(
                navigateToResultsScreen = { navController.navigate(PlannerResultsDestination.route) },
                plannerViewModel = plannerViewModel,
                onClickSettings = onClickSettings,
                contentType = contentType
            )
        }

        composable(route = NavigationBarScreens.Tips.route) {
            TipsScreen(
                navigateToArticlesListScreen = { title, id ->
                    navController.navigate("${TipsArticlesListDestination.route}/$title/$id")
                },
                onClickSettings = onClickSettings,
                googleAuthUiClient = googleAuthUiClient,
                contentType = contentType,
                isUserSignedIn = isUserSignedIn,
                navigateToLoginScreen = {
                    navController.navigate(route = AccountSetupDestination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                        }
                        navController.popBackStack(
                            AccountSetupDestination.route,
                            inclusive = true
                        )
                    }
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
                },
                contentType = contentType
            )

        }

        composable(
            route = ReadArticleDestination.routeWithArgs,
            arguments = ReadArticleDestination.arguments
        ) {
            ReadArticleScreen(
                onNavigateUp = { navController.navigateUp() },
                contentType = contentType
            )
        }

        composable(route = NavigationBarScreens.Overview.route) {
            OverviewScreen(
                navigateToAccountOverviewScreen = {
                    navController.navigate(route = AccountOverviewDestination.route)
                },
                navigateToFlockOverviewScreen = {
                    navController.navigate(FlockOverviewDestination.route)
                },
                onClickSettings = onClickSettings,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
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
                flockEntryViewModel = flockEntryViewModel,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
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
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }
        composable(route = PlannerResultsDestination.route) {
            PlannerResultScreen(
                onNavigateUp = { navController.navigateUp() },
                plannerViewModel = plannerViewModel,
                contentType = contentType
            )
        }
        composable(route = AccountOverviewDestination.route) {
            AccountOverviewScreen(
                onNavigateUp = { navController.navigateUp() },
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }
        composable(route = FlockOverviewDestination.route) {
            FlockOverviewScreen(
                onNavigateUp = { navController.navigateUp() },
                contentType = contentType
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
    signInViewModel: SignInViewModel,
    authUiClient: AuthUiClient,
    googleAuthUiClient: GoogleAuthUiClient,
    userPrefsViewModel: UserPrefsViewModel,
    contentType: ContentType
) {
    navigation(
        route = GraphRoutes.SETTINGS,
        startDestination = SettingsDestination.route
    ) {
        composable(route = SettingsDestination.route) {
            SettingsScreen(
                onNavigateUp = { navController.navigateUp() },
                userPrefsViewModel = userPrefsViewModel,
                navigateToAccountInfoScreen = { navController.navigate(route = AccountInformationDestination.route) },
                contentType = contentType
            )
        }
        composable(route = AccountInformationDestination.route) {
            AccountInfoScreen(
                authUiClient = authUiClient,
                onNavigateUp = { navController.navigateUp() },
                navigateToSignInScreen = {
                    navController.navigate(route = AccountSetupDestination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                        }
                        navController.popBackStack(
                            AccountSetupDestination.route,
                            inclusive = true
                        )
                    }
                },
                signInViewModel = signInViewModel,
                onNavigateToConfirmAccountScreen = {
                    navController.navigate(route = ReauthenticationScreenDestination.route)
                },
                contentType = contentType,
            )
        }
        composable(route = ReauthenticationScreenDestination.route) {
            AuthenticateScreen(
                authUiClient = authUiClient,
                onNavigateUp = { navController.navigateUp() },
                googleAuthUiClient = googleAuthUiClient,
                signInViewModel = signInViewModel,
                contentType = contentType
            )
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.detailsGraph(
    navController: NavHostController,
    flockEntryViewModel: FlockEntryViewModel,
    contentType: ContentType
) {
    navigation(
        route = GraphRoutes.DETAILS,
        startDestination = FlockDetailsDestination.routeWithArgs
    ) {
        composable(
            route = FlockDetailsDestination.routeWithArgs,
            arguments = FlockDetailsDestination.arguments,
            deepLinks = FlockDetailsDestination.deepLink,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }
        ) {
            FlockDetailsScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                navigateToFlockHealthScreen = { id ->
                    navController.navigate(route = "${FlockHealthScreenDestination.route}/$id")
                },
                navigateToVaccinationScreen = { id ->
                    navController.navigate(route = "${AddVaccinationsDestination.route}/$id")
                },
                navigateToWeightScreen = { id ->
                    navController.navigate(route = "${WeightScreenDestination.route}/$id")
                },
                navigateToFeedScreen = { id ->
                    navController.navigate(route = "${FeedScreenDestination.route}/$id")
                },
                navigateToEggsInventoryScreen = { id ->
                    navController.navigate(route = "${EggsInventoryScreenDestination.route}/$id")
                },
                contentType = contentType
            )
        }
        composable(
            route = FlockHealthScreenDestination.routeWithArgs,
            arguments = FlockHealthScreenDestination.arguments,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }
        ) {
            FlockHealthScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToFlockEditScreen = { flockId, healthId ->
                    navController.navigate("${EditFlockDestination.route}/$flockId/$healthId")
                },
                contentType = contentType
            )
        }

        composable(
            route = EditFlockDestination.routeWithArgs,
            arguments = EditFlockDestination.arguments,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }
        ) {
            FlockEditScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                contentType = contentType
            )
        }
        composable(
            route = WeightScreenDestination.routeWithArgs,
            arguments = WeightScreenDestination.arguments,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }
        ) {
            WeightScreen(
                onNavigateUp = { navController.navigateUp() },
                contentType = contentType
            )
        }
        composable(
            route = FeedScreenDestination.routeWithArgs,
            arguments = FeedScreenDestination.arguments,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }
        ) {
            FeedScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                contentType = contentType
            )
        }

        composable(
            route = EditEggsDestination.routeWithArgs,
            arguments = EditEggsDestination.arguments,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }
        ) {
            EggsEditScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                contentType = contentType
            )
        }

        composable(
            route = EggsInventoryScreenDestination.routeWithArgs,
            arguments = EggsInventoryScreenDestination.arguments,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }
        ) {
            EggsInventoryScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToEggsEditScreen = {flockID, eggsID ->
                    navController.navigate(route = "${EditEggsDestination.route}/$flockID/$eggsID")
                },
                contentType = contentType
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.accountDetailsGraph(
    navController: NavHostController,
    userPrefsViewModel: UserPrefsViewModel,
    contentType: ContentType
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
                        "${AddExpenseScreenDestination.route}/$expenseId/$accountId"
                    )
                },
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }
        composable(
            route = AddIncomeScreenDestination.routeWithArgs,
            arguments = AddIncomeScreenDestination.arguments
        ) {
            AddIncomeScreen(
                onNavigateUp = { navController.navigateUp() },
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }
        composable(
            route = AddExpenseScreenDestination.routeWithArgs,
            arguments = AddExpenseScreenDestination.arguments
        ) {
            AddExpenseScreen(
                onNavigateUp = { navController.navigateUp() },
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }
    }
}

