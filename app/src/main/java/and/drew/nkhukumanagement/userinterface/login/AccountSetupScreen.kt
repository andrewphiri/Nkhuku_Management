package and.drew.nkhukumanagement.userinterface.login

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.auth.SignInState
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.auth.UserUiState
import and.drew.nkhukumanagement.auth.isPasswordValid
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.userinterface.navigation.TabScreens
import and.drew.nkhukumanagement.utils.Tabs
import android.app.Activity.RESULT_OK
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AccountSetupDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.PersonOutline
    override val route: String
        get() = "account setup"
    override val resourceId: Int
        get() = R.string.account_setup
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AccountSetupScreen(
    modifier: Modifier = Modifier,
    navigateToVerificationScreen: () -> Unit,
    navigateToHome: () -> Unit,
    state: SignInState,
    signInViewModel: SignInViewModel,
    googleAuthUiClient: GoogleAuthUiClient,
    authUiClient: AuthUiClient,
    isEmailVerified: Boolean,
    onClickForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoadingGoogleButton by remember { mutableStateOf(false) }
    var isLoadingEmailAndPasswordButtonSignIn by remember { mutableStateOf(false) }
    var isLoadingEmailAndPasswordButtonSignUp by remember { mutableStateOf(false) }
    var snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->

            snackbarHostState.showSnackbar(
                message = "Invalid username or password. Try again",
                duration = SnackbarDuration.Long
            )
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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                coroutineScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    signInViewModel.onSignInResult(signInResult)
                }
            }
        }
    )

    LaunchedEffect(key1 = state) {
        if (state.isSignInSuccessful) {
//            authUiClient.verifyEmail()
            if (isEmailVerified) {
                navigateToHome()
            } else {
                authUiClient.verifyEmail()
                navigateToVerificationScreen()
            }
            signInViewModel.resetState()
        } else {
            isLoadingGoogleButton = false
            isLoadingEmailAndPasswordButtonSignIn = false
            isLoadingEmailAndPasswordButtonSignUp = false
        }
    }

    Scaffold(modifier = Modifier.padding(16.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Box(
            modifier = Modifier.fillMaxSize().padding(it),
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
                            isLoadingGoogleButton = true
                            val signInIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signInIntentSender ?: return@launch
                                ).build()
                            )
                        }
                    },
                    isLoadingEmailAndPasswordSignInButton = isLoadingEmailAndPasswordButtonSignIn,
                    isLoadingEmailAndPasswordSignUpButton = isLoadingEmailAndPasswordButtonSignUp,
                    onClickSignInEmailAndPasswordButton = {
                        coroutineScope.launch {
                            isLoadingEmailAndPasswordButtonSignIn = true
                            delay(2000)
                            val signInResult = authUiClient.signInUserWithEmailAndPassWord(
                                signInViewModel.userUiStateSignIn.email,
                                signInViewModel.userUiStateSignIn.password
                            )
                            signInViewModel.onSignInResult(signInResult)


                            isLoadingGoogleButton = false
                            isLoadingEmailAndPasswordButtonSignIn = false
                            isLoadingEmailAndPasswordButtonSignUp = false
                        }
                    },
                    userUiStateSignIn = signInViewModel.userUiStateSignIn,
                    userUiStateSignUp = signInViewModel.userUiStateSignUp,
                    onValueChangedSignIn = signInViewModel::updateUiStateSignIn,
                    onValueChangedSignUp = signInViewModel::updateUiStateSignUp,
                    signInViewModel = signInViewModel,
                    onClickSignUpEmailAndPasswordButton = {
                        if (signInViewModel.userUiStateSignUp
                                .isPasswordValid(signInViewModel.userUiStateSignUp.password)
                        ) {
                            coroutineScope.launch {
                                isLoadingEmailAndPasswordButtonSignUp = true
                                delay(2000)
                                val signInResult =
                                    authUiClient.createUserWithEmailAndPassWord(
                                        signInViewModel.userUiStateSignUp.email,
                                        signInViewModel.userUiStateSignUp.password
                                    )
                                signInViewModel.onSignInResult(signInResult)


                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Password should contain letters, numbers and symbols and should be " +
                                            "at least 8 characters",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                    },
                    onClickSignUpGoogleButton = {
                        isLoadingGoogleButton = true
                        coroutineScope.launch {
                            val signUpIntentSender = googleAuthUiClient.signIn()
                            launcher.launch(
                                IntentSenderRequest.Builder(
                                    signUpIntentSender ?: return@launch
                                ).build()
                            )
                        }
                    },
                    onClickForgotPassword = onClickForgotPassword
                )
            }
            Text(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .clickable(onClick = navigateToHome),
                text = "Skip"
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
    signInViewModel: SignInViewModel,
    onValueChangedSignIn: (UserUiState) -> Unit,
    onValueChangedSignUp: (UserUiState) -> Unit,
    onClickSignUpGoogleButton: () -> Unit,
    onClickSignUpEmailAndPasswordButton: () -> Unit,
    onClickForgotPassword: () -> Unit
) {
    HorizontalPager(
        modifier = Modifier,
        state = pagerState,
        pageSpacing = 0.dp,
        userScrollEnabled = !isLoadingGoogleButton || !isLoadingEmailAndPasswordSignInButton ||
                !isLoadingEmailAndPasswordSignUpButton,
        reverseLayout = false,
        beyondBoundsPageCount = 0,
        pageSize = PageSize.Fill,
        pageContent = { page ->
            when (page) {
                0 -> {
                    signInViewModel.updateUiStateSignUp(
                        userUiStateSignUp.copy(email = "", password = "")
                    )
                    SignInScreen(
                        onClickSignInWithGoogle = onClickSignInGoogleButton,
                        onClickSignInWithEmailAndPassword = onClickSignInEmailAndPasswordButton,
                        isLoadingSignInButton = isLoadingEmailAndPasswordSignInButton,
                        isLoadingGoogleButton = isLoadingGoogleButton,
                        userUiState = userUiStateSignIn,
                        onValueChanged = onValueChangedSignIn,
                        onClickForgotPassword = onClickForgotPassword
                    )
                }

                1 -> {
                    signInViewModel.updateUiStateSignIn(
                        userUiStateSignIn.copy(email = "", password = "")
                    )
                    SignUpScreen(
                        onClickSignUpWithGoogle = onClickSignUpGoogleButton,
                        onValueChanged = onValueChangedSignUp,
                        onClickSignUpWithEmailAndPassword = onClickSignUpEmailAndPasswordButton,
                        userUiState = userUiStateSignUp,
                        isLoadingGoogleButton = isLoadingGoogleButton,
                        isLoadingSignUpButton = isLoadingEmailAndPasswordSignUpButton,
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