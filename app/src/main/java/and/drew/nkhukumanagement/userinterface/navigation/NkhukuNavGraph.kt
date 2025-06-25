package and.drew.nkhukumanagement.userinterface.navigation

import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.settings.AccountInfoScreen
import and.drew.nkhukumanagement.settings.AccountInformationScreenNav
import and.drew.nkhukumanagement.settings.SettingsDestination
import and.drew.nkhukumanagement.settings.SettingsScreen
import and.drew.nkhukumanagement.settings.SettingsScreenNav
import and.drew.nkhukumanagement.userinterface.accounts.AccountsScreen
import and.drew.nkhukumanagement.userinterface.accounts.AccountsScreenNav
import and.drew.nkhukumanagement.userinterface.accounts.AddExpenseScreen
import and.drew.nkhukumanagement.userinterface.accounts.AddExpenseScreenNav
import and.drew.nkhukumanagement.userinterface.accounts.AddIncomeScreen
import and.drew.nkhukumanagement.userinterface.accounts.AddIncomeScreenNav
import and.drew.nkhukumanagement.userinterface.accounts.TransactionScreen
import and.drew.nkhukumanagement.userinterface.accounts.TransactionsScreenNav
import and.drew.nkhukumanagement.userinterface.feed.FeedScreenNav
import and.drew.nkhukumanagement.userinterface.feed.FeedScreen
import and.drew.nkhukumanagement.userinterface.flock.AddFlockScreen
import and.drew.nkhukumanagement.userinterface.flock.AddFlockScreenNav
import and.drew.nkhukumanagement.userinterface.flock.EditEggsScreenNav
import and.drew.nkhukumanagement.userinterface.flock.EditFlockScreenNav
import and.drew.nkhukumanagement.userinterface.flock.EggsEditScreen
import and.drew.nkhukumanagement.userinterface.flock.EggsInventoryScreen
import and.drew.nkhukumanagement.userinterface.flock.EggsInventoryScreenNav
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsScreenNav
import and.drew.nkhukumanagement.userinterface.flock.FlockEditScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import and.drew.nkhukumanagement.userinterface.flock.FlockHealthScreen
import and.drew.nkhukumanagement.userinterface.flock.FlockHealthScreenNav
import and.drew.nkhukumanagement.userinterface.home.HomeScreen
import and.drew.nkhukumanagement.userinterface.home.HomeScreenNav
import and.drew.nkhukumanagement.userinterface.login.AccountSetupDestination
import and.drew.nkhukumanagement.userinterface.login.AccountSetupScreen
import and.drew.nkhukumanagement.userinterface.login.AccountSetupScreenNav
import and.drew.nkhukumanagement.userinterface.login.AuthenticateScreen
import and.drew.nkhukumanagement.userinterface.login.ReauthenticationScreenNav
import and.drew.nkhukumanagement.userinterface.login.ResetPasswordDestination
import and.drew.nkhukumanagement.userinterface.login.ResetPasswordScreen
import and.drew.nkhukumanagement.userinterface.login.ResetPasswordScreenNav
import and.drew.nkhukumanagement.userinterface.login.VerifyEmailScreen
import and.drew.nkhukumanagement.userinterface.login.VerifyEmailScreenNav
import and.drew.nkhukumanagement.userinterface.overview.AccountOverviewScreen
import and.drew.nkhukumanagement.userinterface.overview.AccountOverviewScreenNav
import and.drew.nkhukumanagement.userinterface.overview.FlockOverviewScreen
import and.drew.nkhukumanagement.userinterface.overview.FlockOverviewScreenNav
import and.drew.nkhukumanagement.userinterface.overview.OverviewScreen
import and.drew.nkhukumanagement.userinterface.overview.OverviewScreenNav
import and.drew.nkhukumanagement.userinterface.planner.PlannerResultScreen
import and.drew.nkhukumanagement.userinterface.planner.PlannerResultsScreenNav
import and.drew.nkhukumanagement.userinterface.planner.PlannerScreen
import and.drew.nkhukumanagement.userinterface.planner.PlannerScreenNav
import and.drew.nkhukumanagement.userinterface.planner.PlannerViewModel
import and.drew.nkhukumanagement.userinterface.tips.ReadArticleScreen
import and.drew.nkhukumanagement.userinterface.tips.ReadArticleScreenNav
import and.drew.nkhukumanagement.userinterface.tips.TipsArticlesListScreen
import and.drew.nkhukumanagement.userinterface.tips.TipsArticlesListScreenNav
import and.drew.nkhukumanagement.userinterface.tips.TipsScreen
import and.drew.nkhukumanagement.userinterface.tips.TipsScreenNav
import and.drew.nkhukumanagement.userinterface.vaccination.AddVaccinationsScreen
import and.drew.nkhukumanagement.userinterface.vaccination.AddVaccinationsScreenNav
import and.drew.nkhukumanagement.userinterface.vaccination.VaccinationViewModel
import and.drew.nkhukumanagement.userinterface.weight.WeightScreen
import and.drew.nkhukumanagement.userinterface.weight.WeightScreenNav
import and.drew.nkhukumanagement.utils.ContentType
import android.os.Build
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
import androidx.navigation.toRoute
import com.google.android.gms.auth.api.identity.Identity

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
            context = appContext
        )
    }
    val authUiClient by lazy {
        AuthUiClient(
            appContext
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
            startDestination = HomeGraph,
            modifier = modifier,
            route = RootGraph::class
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
                userPrefsViewModel = userPrefsViewModel,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                },
                contentType = contentType,
                vaccineViewModel = vaccinationViewModel
            )

            accountDetailsGraph(
                navController = navController,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                }
            )
            settingsGraph(
                navController = navController,
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                googleAuthUiClient = googleAuthUiClient,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
            overviewGraph(
                navController = navController,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                }
            )
            tipsGraph(
                navController = navController,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                },
                googleAuthUiClient = googleAuthUiClient,
                isUserSignedIn = isUserSignedIn
            )

            plannerGraph(
                navController = navController,
                plannerViewModel = plannerViewModel,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                }
            )
        }

    } else if (isUserSignedIn) {
        NavHost(
            navController = navController,
            startDestination = VerificationGraph::class,
            modifier = modifier,
            route = RootGraph::class
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
                userPrefsViewModel = userPrefsViewModel,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                },
                contentType = contentType,
                vaccineViewModel = vaccinationViewModel
                )

            accountDetailsGraph(
                navController = navController,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                }
            )
            settingsGraph(
                navController = navController,
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                googleAuthUiClient = googleAuthUiClient,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
            overviewGraph(
                navController = navController,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                }
            )
            tipsGraph(
                navController = navController,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                },
                googleAuthUiClient = googleAuthUiClient,
                isUserSignedIn = isUserSignedIn
            )

            plannerGraph(
                navController = navController,
                plannerViewModel = plannerViewModel,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                }
            )
        }

    } else {
        // User is not signed in, show the LoginGraph
        NavHost(
            navController = navController,
            startDestination = AuthGraph::class,
            modifier = modifier,
            route = RootGraph::class
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
                userPrefsViewModel = userPrefsViewModel,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                },
                contentType = contentType,
                vaccineViewModel = vaccinationViewModel
                )
            accountDetailsGraph(
                navController = navController,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                }
            )
            settingsGraph(
                navController = navController,
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                googleAuthUiClient = googleAuthUiClient,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
            overviewGraph(
                navController = navController,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                }
            )
            tipsGraph(
                navController = navController,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                },
                googleAuthUiClient = googleAuthUiClient,
                isUserSignedIn = isUserSignedIn
            )

            plannerGraph(
                navController = navController,
                plannerViewModel = plannerViewModel,
                contentType = contentType,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                }
            )
        }
    }
}

fun NavGraphBuilder.loginGraph(
    navController: NavHostController,
    signInViewModel: SignInViewModel,
    googleAuthUiClient: GoogleAuthUiClient,
    authUiClient: AuthUiClient,
    isEmailVerified: Boolean,
    userPrefsViewModel: UserPrefsViewModel,
    contentType: ContentType
) {
    navigation<AuthGraph>(
        startDestination = AccountSetupScreenNav
    ) {
        composable<AccountSetupScreenNav> {
            val state by signInViewModel.state.collectAsState()
            AccountSetupScreen(
                userPrefsViewModel = userPrefsViewModel,
                navigateToVerificationScreen = {
                    navController.navigate(route = VerificationGraph) {
                        popUpTo(AuthGraph) {
                            inclusive = true
                        }
                    }
                },
                navigateToHome = {
                    navController.navigate(route = HomeGraph) {
                        popUpTo(AuthGraph) {
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
                    navController.navigate(ResetPasswordScreenNav)
                },
                onClickSkipAccountSetup = {
                    navController.navigate(route = HomeGraph) {
                        popUpTo(AuthGraph) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable<ResetPasswordScreenNav> {
            ResetPasswordScreen(
                onNavigateUp = {
                    navController.navigate(route = AccountSetupScreenNav) {
                        popUpTo(route = ResetPasswordDestination.route) {
                            inclusive = true
                        }
                    }
                },
                navigateToAccountSetupScreen = {
                    navController.navigate(route = AccountSetupScreenNav)
                },
                signInViewModel = signInViewModel,
                authUiClient = authUiClient,
                contentType = contentType
            )
        }
    }
}

fun NavGraphBuilder.loginGraphVerification(
    navController: NavHostController,
    signInViewModel: SignInViewModel,
    authUiClient: AuthUiClient,
    contentType: ContentType
) {
    navigation<VerificationGraph>(
        startDestination = VerifyEmailScreenNav
    ) {
        composable<VerifyEmailScreenNav> {
            VerifyEmailScreen(
                authUiClient = authUiClient,
                navigateToHome = {
                    navController.navigate(route = HomeGraph) {
                        popUpTo(VerificationGraph) {
                            inclusive = true
                        }
                    }
                },
                signInViewModel = signInViewModel,
                onClickSettings = {
                    navController.navigate(SettingsScreenNav)
                },
                contentType = contentType
            )
        }
    }
}


fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
    signInViewModel: SignInViewModel,
    authUiClient: AuthUiClient,
    googleAuthUiClient: GoogleAuthUiClient,
    userPrefsViewModel: UserPrefsViewModel,
    contentType: ContentType
) {
    navigation<SettingsGraph>(
        startDestination = SettingsScreenNav
    ) {
        composable<SettingsScreenNav> {
            SettingsScreen(
                onNavigateUp = { navController.navigateUp() },
                userPrefsViewModel = userPrefsViewModel,
                navigateToAccountInfoScreen = { navController.navigate(route = AccountInformationScreenNav) },
                contentType = contentType
            )
        }
        composable<AccountInformationScreenNav> {
            AccountInfoScreen(
                authUiClient = authUiClient,
                onNavigateUp = { navController.navigateUp() },
                navigateToSignInScreen = {
                    navController.navigate(route = AccountSetupScreenNav) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                        }
                        navController.popBackStack(
                            AccountSetupScreenNav,
                            inclusive = true
                        )
                    }
                },
                signInViewModel = signInViewModel,
                onNavigateToConfirmAccountScreen = {
                    navController.navigate(route = ReauthenticationScreenNav)
                },
                contentType = contentType,
            )
        }
        composable<ReauthenticationScreenNav> {
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

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    flockEntryViewModel: FlockEntryViewModel,
    contentType: ContentType,
    userPrefsViewModel: UserPrefsViewModel,
    onClickSettings: () -> Unit,
    vaccineViewModel: VaccinationViewModel
) {
    navigation<HomeGraph>(
        startDestination = HomeScreenNav
    ) {
        composable<HomeScreenNav> {
            HomeScreen(
                navigateToAddFlock = {
                    navController.navigate(AddFlockScreenNav)
                },
                navigateToFlockDetails = { id ->
                    navController.navigate(FlockDetailsScreenNav(id))
                },
                flockEntryViewModel = flockEntryViewModel,
                onClickSettings = onClickSettings,
                contentType = contentType,
                userPrefsViewModel = userPrefsViewModel,
            )
        }

        composable<AddFlockScreenNav> {
            AddFlockScreen(
                onNavigateUp = {
                    navController.navigateUp()
                },
                navigateToVaccinationsScreen = {
                    val id = it.id
                    navController.navigate(route = AddVaccinationsScreenNav(id))
                },
                flockEntryViewModel = flockEntryViewModel,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }
        composable<AddVaccinationsScreenNav> { navBackStackEntry ->
            val args = navBackStackEntry.toRoute<AddVaccinationsScreenNav>()
            AddVaccinationsScreen(
                navigateBack = {
                    navController.navigate(HomeScreenNav) {
                        navController.popBackStack(
                            HomeScreenNav,
                            inclusive = true
                        )
                    }
                },
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType,
                flockId = args.flockId,
                vaccinationViewModel = vaccineViewModel
            )
        }
        composable<FlockDetailsScreenNav>(
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
            val args= it.toRoute<FlockDetailsScreenNav>()
            FlockDetailsScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                navigateToFlockHealthScreen = { id ->
                    navController.navigate(route = FlockHealthScreenNav(id))
                },
                navigateToVaccinationScreen = { id ->
                    navController.navigate(route = AddVaccinationsScreenNav(id))
                },
                navigateToWeightScreen = { id ->
                    navController.navigate(route = WeightScreenNav(id))
                },
                navigateToFeedScreen = { id ->
                    navController.navigate(route = FeedScreenNav(id))
                },
                navigateToEggsInventoryScreen = { eggsId, flockId ->
                    navController.navigate(route = EggsInventoryScreenNav(flockId = flockId, eggId = eggsId))
                },
                contentType = contentType,
                flockId = args.flockId
            )
        }
        composable<FlockHealthScreenNav>(
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
            val args = it.toRoute<FlockHealthScreenNav>()
            FlockHealthScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToFlockEditScreen = { flockId, healthId ->
                    navController.navigate(EditFlockScreenNav(flockId, healthId))
                },
                contentType = contentType,
                flockID = args.flockId
            )
        }

        composable<EditFlockScreenNav>(
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
            val args = it.toRoute<EditFlockScreenNav>()
            FlockEditScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                contentType = contentType,
                flockId = args.flockId,
                healthId = args.healthId
            )
        }
        composable<WeightScreenNav>(
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
            val args = it.toRoute<WeightScreenNav>()
            WeightScreen(
                onNavigateUp = { navController.navigateUp() },
                contentType = contentType,
                flockId = args.flockId
            )
        }
        composable<FeedScreenNav>(
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
            val args = it.toRoute<FeedScreenNav>()
            FeedScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                contentType = contentType,
                flockId = args.flockId
            )
        }

        composable<EditEggsScreenNav>(
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
            val args = it.toRoute<EditEggsScreenNav>()
            EggsEditScreen(
                onNavigateUp = { navController.navigateUp() },
                flockEntryViewModel = flockEntryViewModel,
                contentType = contentType,
                flockID = args.flockId,
                eggsId = args.eggsId
            )
        }

        composable<EggsInventoryScreenNav>(
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
            val args = it.toRoute<EggsInventoryScreenNav>()
            EggsInventoryScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToEggsEditScreen = {flockID, eggsID ->
                    navController.navigate(route = EditEggsScreenNav(flockID, eggsID))
                },
                contentType = contentType,
                flockID = args.flockId,
                eggId = args.eggId
            )
        }
    }
}


fun NavGraphBuilder.accountDetailsGraph(
    navController: NavHostController,
    userPrefsViewModel: UserPrefsViewModel,
    contentType: ContentType,
    onClickSettings: () -> Unit
) {
    navigation<AccountsGraph>(
        startDestination = AccountsScreenNav
    ) {

        composable<AccountsScreenNav> {
            AccountsScreen(
                navigateToTransactionsScreen = { id ->
                    navController.navigate(route = TransactionsScreenNav(id))
                },
                onClickSettings = onClickSettings,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }
        composable<TransactionsScreenNav> {
            val args = it.toRoute<TransactionsScreenNav>()
            TransactionScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToAddIncomeScreen = { incomeId, accountId ->
                    navController.navigate(AddIncomeScreenNav(incomeId = incomeId, accountId = accountId))
                },
                navigateToAddExpenseScreen = { expenseId, accountId ->
                    navController.navigate(
                        AddExpenseScreenNav(expenseId = expenseId, accountId = accountId)
                    )
                },
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType,
                accountID = args.accountId
            )
        }
        composable<AddIncomeScreenNav> {
            val args = it.toRoute<AddIncomeScreenNav>()
            AddIncomeScreen(
                onNavigateUp = { navController.navigateUp() },
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType,
                incomeID = args.incomeId,
                accountID = args.accountId
            )
        }
        composable<AddExpenseScreenNav> {
            val args = it.toRoute<AddExpenseScreenNav>()
            AddExpenseScreen(
                onNavigateUp = { navController.navigateUp() },
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType,
                expenseID = args.expenseId,
                accountID = args.accountId
            )
        }
    }
}

fun NavGraphBuilder.overviewGraph(
    navController: NavHostController,
    userPrefsViewModel: UserPrefsViewModel,
    contentType: ContentType,
    onClickSettings: () -> Unit
) {
    navigation<OverviewGraph>(
        startDestination = OverviewScreenNav
    ) {
        composable<OverviewScreenNav> {
            OverviewScreen(
                navigateToAccountOverviewScreen = {
                    navController.navigate(route = AccountOverviewScreenNav)
                },
                navigateToFlockOverviewScreen = {
                    navController.navigate(FlockOverviewScreenNav)
                },
                onClickSettings = onClickSettings,
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }

        composable<AccountOverviewScreenNav> {
            AccountOverviewScreen(
                onNavigateUp = { navController.navigateUp() },
                userPrefsViewModel = userPrefsViewModel,
                contentType = contentType
            )
        }
        composable<FlockOverviewScreenNav> {
            FlockOverviewScreen(
                onNavigateUp = { navController.navigateUp() },
                contentType = contentType
            )
        }
    }
}

fun NavGraphBuilder.tipsGraph(
    navController: NavHostController,
    contentType: ContentType,
    onClickSettings: () -> Unit,
    googleAuthUiClient: GoogleAuthUiClient,
    isUserSignedIn: Boolean
) {
    navigation<TipsGraph>(
        startDestination = TipsScreenNav
    ) {
        composable<TipsScreenNav> {
            TipsScreen(
                navigateToArticlesListScreen = { title, id ->
                    navController.navigate(TipsArticlesListScreenNav(id, title))
                },
                onClickSettings = onClickSettings,
                googleAuthUiClient = googleAuthUiClient,
                contentType = contentType,
                isUserSignedIn = isUserSignedIn,
                navigateToLoginScreen = {
                    navController.navigate(route = AccountSetupScreenNav) {
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
        composable<TipsArticlesListScreenNav> {
            val args = it.toRoute<TipsArticlesListScreenNav>()
            TipsArticlesListScreen(
                onNavigateUp = { navController.navigateUp() },
                navigateToReadArticle = { categoryId, articleId ->
                    navController.navigate(ReadArticleScreenNav(categoryId, articleId))
                },
                contentType = contentType,
                categoryId = args.categoryId,
                articleIdCategory = args.articleIdCategory
            )

        }

        composable<ReadArticleScreenNav> {
            val args = it.toRoute<ReadArticleScreenNav>()
            ReadArticleScreen(
                onNavigateUp = { navController.navigateUp() },
                contentType = contentType,
                articleId = args.articleId,
                categoryId = args.categoryId
            )
        }
    }
}

fun NavGraphBuilder.plannerGraph(
    navController: NavHostController,
    plannerViewModel: PlannerViewModel,
    onClickSettings: () -> Unit,
    contentType: ContentType
) {
    navigation<PlannerGraph>(
        startDestination = PlannerScreenNav
    ) {
        composable<PlannerScreenNav> {
            PlannerScreen(
                navigateToResultsScreen = { navController.navigate(PlannerResultsScreenNav) },
                plannerViewModel = plannerViewModel,
                onClickSettings = onClickSettings,
                contentType = contentType
            )
        }
        composable<PlannerResultsScreenNav> {
            PlannerResultScreen(
                onNavigateUp = { navController.navigateUp() },
                plannerViewModel = plannerViewModel,
                contentType = contentType
            )
        }
    }
}

