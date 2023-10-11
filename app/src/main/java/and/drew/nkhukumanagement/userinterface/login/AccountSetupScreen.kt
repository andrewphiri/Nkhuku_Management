package and.drew.nkhukumanagement.userinterface.login

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.auth.SignInState
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.auth.UserUiState
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.userinterface.navigation.TabScreens
import and.drew.nkhukumanagement.utils.Tabs
import android.app.Activity.RESULT_OK
import android.os.Build
import android.widget.Toast
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
    navigateToHome: () -> Unit,
    state: SignInState,
    signInViewModel: SignInViewModel,
    googleAuthUiClient: GoogleAuthUiClient,
    authUiClient: AuthUiClient
) {
    val context = LocalContext.current
    var isLoadingGoogleButton by remember { mutableStateOf(false) }
    var isLoadingEmailAndPasswordButtonSignIn by remember { mutableStateOf(false) }
    var isLoadingEmailAndPasswordButtonSignUp by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()

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

    val coroutineScope = rememberCoroutineScope()

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

    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            navigateToHome()
            signInViewModel.resetState()
        } else {
            isLoadingGoogleButton = false
            isLoadingEmailAndPasswordButtonSignIn = false
            isLoadingEmailAndPasswordButtonSignUp = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize().padding(16.dp),
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
                    isLoadingGoogleButton = true
                    coroutineScope.launch {
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
                    isLoadingEmailAndPasswordButtonSignIn = true
                    coroutineScope.launch {
                        delay(2000)
                        val signInResult = authUiClient.signInUserWithEmailAndPassWord(
                            signInViewModel.userUiStateSignIn.email,
                            signInViewModel.userUiStateSignIn.password
                        )
                        signInViewModel.onSignInResult(signInResult)
                    }
                },
                userUiStateSignIn = signInViewModel.userUiStateSignIn,
                userUiStateSignUp = signInViewModel.userUiStateSignUp,
                onValueChangedSignIn = signInViewModel::updateUiStateSignIn,
                onValueChangedSignUp = signInViewModel::updateUiStateSignUp,
                onClickSignUpEmailAndPasswordButton = {
                    isLoadingEmailAndPasswordButtonSignUp = true
                    coroutineScope.launch {
                        delay(2000)
                        val signInResult =
                            authUiClient.createUserWithEmailAndPassWord(
                                signInViewModel.userUiStateSignUp.email,
                                signInViewModel.userUiStateSignUp.password
                            )
                        signInViewModel.onSignInResult(signInResult)
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
                }
            )
        }
        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding()
                .clickable(onClick = navigateToHome),
            text = "Skip"
        )
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
    onClickSignUpEmailAndPasswordButton: () -> Unit
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
                    SignInScreen(
                        onClickSignInWithGoogle = onClickSignInGoogleButton,
                        onClickSignInWithEmailAndPassword = onClickSignInEmailAndPasswordButton,
                        isLoadingSignInButton = isLoadingEmailAndPasswordSignInButton,
                        isLoadingGoogleButton = isLoadingGoogleButton,
                        userUiState = userUiStateSignIn,
                        onValueChanged = onValueChangedSignIn
                    )
                }

                1 -> {
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