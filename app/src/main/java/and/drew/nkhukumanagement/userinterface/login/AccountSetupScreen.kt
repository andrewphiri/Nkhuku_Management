package and.drew.nkhukumanagement.userinterface.login

import and.drew.nkhukumanagement.MainActivity
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.auth.SignInResult
import and.drew.nkhukumanagement.auth.SignInState
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.auth.UserUiState
import and.drew.nkhukumanagement.auth.isPasswordValid
import and.drew.nkhukumanagement.prefs.UserPrefsViewModel
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.userinterface.navigation.TabScreens
import and.drew.nkhukumanagement.utils.Tabs
import android.app.Activity.RESULT_OK
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

object AccountSetupDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.PersonOutline
    override val route: String
        get() = "account setup"
    override val resourceId: Int
        get() = R.string.account_setup
}

@Serializable object AccountSetupScreenNav

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountSetupScreen(
    modifier: Modifier = Modifier,
    userPrefsViewModel: UserPrefsViewModel,
    navigateToVerificationScreen: () -> Unit,
    navigateToHome: () -> Unit,
    state: SignInState,
    signInViewModel: SignInViewModel,
    googleAuthUiClient: GoogleAuthUiClient,
    authUiClient: AuthUiClient,
    isEmailVerified: Boolean,
    onClickForgotPassword: () -> Unit,
    onClickSkipAccountSetup: () -> Unit
) {

    MainAccountSetupScreen(
        modifier = modifier,
        navigateToVerificationScreen = {
            signInViewModel.setUserLoggedIn(true)
            navigateToVerificationScreen()
                                       },
        navigateToHome = {
            signInViewModel.setUserLoggedIn(true)
            navigateToHome()
                         },
        state = state,
        onSignInResult = { signInViewModel.onSignInResult(it) },
        resetState = { signInViewModel.resetState() },
        userSignInState = signInViewModel.userUiStateSignIn,
        userSignUpState = signInViewModel.userUiStateSignUp,
        updateSignInState = signInViewModel::updateUiStateSignIn,
        updateSignUpState = signInViewModel::updateUiStateSignUp,
        googleAuthUiClient = googleAuthUiClient,
        authUiClient = authUiClient,
        isEmailVerified = isEmailVerified,
        onClickForgotPassword = onClickForgotPassword,
        onClickSkipAccountSetup = onClickSkipAccountSetup,
        updateSkipAccountSetup = {
            userPrefsViewModel.updateSkipAccountSetup(it)
        },
        setEmailVerification = {
            signInViewModel.setEmailVerification(authUiClient.isEmailVerified())
        }
    )
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainAccountSetupScreen(
    modifier: Modifier = Modifier,
    navigateToVerificationScreen: () -> Unit,
    navigateToHome: () -> Unit,
    state: SignInState,
    onSignInResult: (SignInResult) -> Unit,
    resetState: () -> Unit,
    userSignInState: UserUiState,
    userSignUpState: UserUiState,
    updateSignInState: (UserUiState) -> Unit,
    updateSignUpState: (UserUiState) -> Unit,
    googleAuthUiClient: GoogleAuthUiClient,
    authUiClient: AuthUiClient,
    isEmailVerified: Boolean,
    onClickForgotPassword: () -> Unit,
    onClickSkipAccountSetup: () -> Unit,
    updateSkipAccountSetup: (Boolean) -> Unit,
    setEmailVerification: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoadingGoogleButton by remember { mutableStateOf(false) }
    var isLoadingEmailAndPasswordButtonSignIn by remember { mutableStateOf(false) }
    var isLoadingEmailAndPasswordButtonSignUp by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var isGoogleSignUpButtonEnabled by remember { mutableStateOf(true) }
    var isSignUpButtonEnabled by remember { mutableStateOf(true) }
    var isGoogleSignInButtonEnabled by remember { mutableStateOf(true) }
    var isSignInButtonEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Long
            )
        }

        if (state.signInError != null) {
            isSignInButtonEnabled = true
            isGoogleSignInButtonEnabled = true
            isSignUpButtonEnabled = true
            isGoogleSignUpButtonEnabled = true
            isLoadingEmailAndPasswordButtonSignIn = false
            isLoadingEmailAndPasswordButtonSignUp = false
            isLoadingGoogleButton = false
            onSignInResult(SignInResult(
            data = null, errorMessage = null
                ))
        }
    }

    val tabItems = listOf(TabScreens.SignIn, TabScreens.SignUp)
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        //pageCount
        2
    }

//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartIntentSenderForResult(),
//        onResult = { result ->
//            if (result.resultCode == RESULT_OK) {
//                coroutineScope.launch {
//                    val signInResult = googleAuthUiClient.signInWithIntent(
//                        intent = result.data ?: return@launch
//                    )
//                    onSignInResult(signInResult)
//                }
//            } else {
//                isLoadingGoogleButton = false
//                isLoadingEmailAndPasswordButtonSignIn = false
//                isLoadingEmailAndPasswordButtonSignUp = false
//            }
//        }
//    )

    LaunchedEffect(key1 = state.isSignInSuccessful, key2 = isEmailVerified ) {
        if (state.isSignInSuccessful) {
//            authUiClient.verifyEmail()
           // authUiClient.refreshEmail()
            if (isEmailVerified) {
                navigateToHome()
            } else {
                authUiClient.verifyEmail()
                navigateToVerificationScreen()
            }
            resetState()
        } else {
            isLoadingGoogleButton = false
            isLoadingEmailAndPasswordButtonSignIn = false
            isLoadingEmailAndPasswordButtonSignUp = false
        }
        // NotificationService().insertTokenOnFirstSignUp()
    }

    Scaffold(
        modifier = modifier
            .semantics { contentDescription = "login" }
            .padding(16.dp),
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Tabs(tabs = tabItems, pagerState = pagerState)

                AccountTabScreenContent(
                    pagerState = pagerState,
                    isLoadingGoogleButton = isLoadingGoogleButton,
                    onClickSignInGoogleButton = {
                        coroutineScope.launch {
                            isSignInButtonEnabled = false
                            isLoadingGoogleButton = true
                            isGoogleSignInButtonEnabled = false

                            val result = googleAuthUiClient.signIn()
                            result.data?.let { user ->
                                // Handle logged-in user
                                val signInResult = SignInResult(
                                    data = result.data, errorMessage = null
                                )
                                onSignInResult(signInResult)
                            } ?: run {
                                // Show error: result.errorMessage

                                val signInResult = SignInResult(
                                    data = null, errorMessage = result.errorMessage
                                )
                                onSignInResult(signInResult)

                            }

//                            val signInIntentSender = googleAuthUiClient.signIn()
//                            launcher.launch(
//                                IntentSenderRequest.Builder(
//                                    signInIntentSender ?: return@launch
//                                ).build()
//                            )
                        }
                    },
                    isLoadingEmailAndPasswordSignInButton = isLoadingEmailAndPasswordButtonSignIn,
                    isLoadingEmailAndPasswordSignUpButton = isLoadingEmailAndPasswordButtonSignUp,
                    onClickSignInEmailAndPasswordButton = {
                        coroutineScope.launch {
                            isGoogleSignInButtonEnabled = false
                            isLoadingEmailAndPasswordButtonSignIn = true
                            delay(2000)
                            val signInResult = authUiClient.signInUserWithEmailAndPassWord(
                                userSignInState.email,
                                userSignInState.password
                            )
                            onSignInResult(signInResult)
                            setEmailVerification()
                            isLoadingGoogleButton = false
                            isLoadingEmailAndPasswordButtonSignIn = false
                            isLoadingEmailAndPasswordButtonSignUp = false
                        }
                    },
                    userUiStateSignIn = userSignInState,
                    userUiStateSignUp = userSignUpState,
                    onValueChangedSignIn = updateSignInState,
                    onValueChangedSignUp = updateSignUpState,
                    onClickSignUpEmailAndPasswordButton = {
                        if (userSignUpState
                                .isPasswordValid(userSignUpState.password)
                        ) {
                            coroutineScope.launch {
                                isGoogleSignUpButtonEnabled = false
                                isLoadingEmailAndPasswordButtonSignUp = true
                                delay(2000)
                                val signInResult =
                                    authUiClient.createUserWithEmailAndPassWord(
                                        userSignInState.email,
                                        userSignInState.password
                                    )
                                onSignInResult(signInResult)
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.password_invalid_message) +
                                            "at least 8 characters",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    },
                    onClickSignUpGoogleButton = {
                        isLoadingGoogleButton = true
                        isSignUpButtonEnabled = false
                        coroutineScope.launch {
                            isSignInButtonEnabled = false
                            isLoadingGoogleButton = true
                            isGoogleSignInButtonEnabled = false

                            val result = googleAuthUiClient.signIn()
                            result.data?.let { user ->
                                // Handle logged-in user
                                val signInResult = SignInResult(
                                    data = result.data, errorMessage = null
                                )
                                onSignInResult(signInResult)
                            } ?: run {
                                // Show error: result.errorMessage

                                val signInResult = SignInResult(
                                    data = null, errorMessage = result.errorMessage
                                )
                                onSignInResult(signInResult)
                            }
                        }
                    },
                    onClickForgotPassword = onClickForgotPassword
                )
            }
            Text(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clickable(
                        onClick = {
                            updateSkipAccountSetup(true)
                            onClickSkipAccountSetup()
                        }),
                text = stringResource(R.string.skip)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountTabScreenContent(
    pagerState: PagerState,
    onClickSignInGoogleButton: () -> Unit,
    onClickSignInEmailAndPasswordButton: () -> Unit,
    isLoadingEmailAndPasswordSignInButton: Boolean,
    isLoadingEmailAndPasswordSignUpButton: Boolean,
    isLoadingGoogleButton: Boolean,
    userUiStateSignIn: UserUiState,
    userUiStateSignUp: UserUiState,
    onValueChangedSignIn: (UserUiState) -> Unit,
    onValueChangedSignUp: (UserUiState) -> Unit,
    onClickSignUpGoogleButton: () -> Unit,
    onClickSignUpEmailAndPasswordButton: () -> Unit,
    onClickForgotPassword: () -> Unit,
    isGoogleSignUpButtonEnabled: Boolean = true,
    isSignUpButtonEnabled: Boolean = true,
    isGoogleSignInButtonEnabled: Boolean = true,
    isSignInButtonEnabled: Boolean = true
) {
    HorizontalPager(
        modifier = Modifier,
        state = pagerState,
        pageSpacing = 0.dp,
        userScrollEnabled = !isLoadingGoogleButton || !isLoadingEmailAndPasswordSignInButton ||
                !isLoadingEmailAndPasswordSignUpButton,
        reverseLayout = false,
        beyondViewportPageCount = 0,
        pageSize = PageSize.Fill,
        pageContent = { page ->
            when (page) {
                0 -> {
                    onValueChangedSignUp(
                        userUiStateSignUp.copy(email = "", password = "")
                    )
                    SignInScreen(
                        onClickSignInWithGoogle = onClickSignInGoogleButton,
                        onClickSignInWithEmailAndPassword = onClickSignInEmailAndPasswordButton,
                        isLoadingSignInButton = isLoadingEmailAndPasswordSignInButton,
                        isLoadingGoogleButton = isLoadingGoogleButton,
                        userUiState = userUiStateSignIn,
                        onValueChanged = onValueChangedSignIn,
                        onClickForgotPassword = onClickForgotPassword,
                        isGoogleSignInButtonEnabled = isGoogleSignInButtonEnabled,
                        isSignInButtonEnabled = isSignInButtonEnabled
                    )
                }

                1 -> {
                    onValueChangedSignIn(
                        userUiStateSignIn.copy(email = "", password = "")
                    )
                    SignUpScreen(
                        onClickSignUpWithGoogle = onClickSignUpGoogleButton,
                        onValueChanged = onValueChangedSignUp,
                        onClickSignUpWithEmailAndPassword = onClickSignUpEmailAndPasswordButton,
                        userUiState = userUiStateSignUp,
                        isLoadingGoogleButton = isLoadingGoogleButton,
                        isLoadingSignUpButton = isLoadingEmailAndPasswordSignUpButton,
                        isGoogleButtonEnabled = isGoogleSignUpButtonEnabled,
                        isSignUpButtonEnabled = isSignUpButtonEnabled
                    )
                }
            }
        }
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AccountSetupPreview() {
    NkhukuManagementTheme {
//        AccountSetupScreen(navigateToHome = {})
    }
}