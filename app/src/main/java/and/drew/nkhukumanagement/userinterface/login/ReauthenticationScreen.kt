package and.drew.nkhukumanagement.userinterface.login

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.GoogleAuthUiClient
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.auth.UserUiState
import and.drew.nkhukumanagement.auth.isValid
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.BaseSignInPassword
import and.drew.nkhukumanagement.utils.BaseSignInRow
import and.drew.nkhukumanagement.utils.ShowAlertDialog
import and.drew.nkhukumanagement.utils.ShowSuccessfulDialog
import and.drew.nkhukumanagement.utils.SignInGoogleButton
import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ReauthenticationScreenDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.PersonOutline
    override val route: String
        get() = "authenticate screen"
    override val resourceId: Int
        get() = R.string.confirm_account
}

@Composable
fun AuthenticateScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    googleAuthUiClient: GoogleAuthUiClient,
    authUiClient: AuthUiClient,
    signInViewModel: SignInViewModel,
) {
    val state by signInViewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var isLoadingGoogleButton by remember { mutableStateOf(false) }
    var isLoadingEmailAndPasswordButtonSignIn by remember { mutableStateOf(false) }
    var snackbarHostState = remember { SnackbarHostState() }
    var isCircularBarShowing by remember { mutableStateOf(false) }
    var isDeleteAlertDialogPromptShowing by remember { mutableStateOf(false) }
    var emailSignedIn by remember { mutableStateOf("") }
    emailSignedIn = authUiClient.signedInUser().email.toString()
    var signInIntent by rememberSaveable { mutableStateOf(Intent()) }
    var isAccountDeletedDialogSuccessShowing by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            snackbarHostState.showSnackbar(
                message = "Invalid email address or password. Try again",
                duration = SnackbarDuration.Long
            )
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                coroutineScope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    signInIntent = result.data ?: return@launch
                    signInViewModel.onSignInResult(signInResult)
                }
            }
        }
    )

    LaunchedEffect(key1 = state) {
        if (state.isSignInSuccessful) {
            isDeleteAlertDialogPromptShowing = true
        } else {
            isLoadingGoogleButton = false
            isLoadingEmailAndPasswordButtonSignIn = false
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(ReauthenticationScreenDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            AuthenticateCard(
                onClickSignInWithGoogle = {
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
                onClickSignInWithEmailAndPassword = {
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
                isLoadingGoogleButton = isLoadingGoogleButton,
                isLoadingSignInButton = isLoadingEmailAndPasswordButtonSignIn,
                userUiState = signInViewModel.userUiStateSignIn.copy(email = emailSignedIn),
                onValueChanged = signInViewModel::updateUiStateSignIn,
                onConfirmDelete = {
                    coroutineScope.launch {
                        isDeleteAlertDialogPromptShowing = false
                        isCircularBarShowing = true
                        isLoadingGoogleButton = false
                        isLoadingEmailAndPasswordButtonSignIn = false
                        val deleteAccount = authUiClient.deleteAccount(
                            email = emailSignedIn,
                            password = signInViewModel.userUiStateSignIn.password,
                            googleAuthUiClient = googleAuthUiClient,
                            intent = signInIntent,
                        )
                        delay(2000)
                        //Log.i("Email", signInViewModel.userUiStateSignIn.email)
                        if (deleteAccount) {
                            isAccountDeletedDialogSuccessShowing = true
                        } else {
                            snackbarHostState.showSnackbar(
                                message = "Action failed. Please try again.",
                                duration = SnackbarDuration.Long
                            )
                        }
                        isCircularBarShowing = false
                    }
                },
                onDismiss = { isDeleteAlertDialogPromptShowing = false },
                isAlertDialogShowing = isDeleteAlertDialogPromptShowing,
                isCircularIndicatorShowing = isCircularBarShowing,
                isSuccessAlertDialogShowing = isAccountDeletedDialogSuccessShowing,
                onDismissSuccessAlertDialog = {
                    signInViewModel.resetState()
                    signInViewModel.setUserLoggedIn(false)
                    signInViewModel.updateUiStateSignIn(UserUiState(email = "", password = ""))
                    isAccountDeletedDialogSuccessShowing = false
                }
            )
        }
    }
}

@Composable
fun AuthenticateCard(
    modifier: Modifier = Modifier,
    onClickSignInWithGoogle: () -> Unit,
    loadingText: String = "Deleting",
    defaultText: String = "Sign In",
    onClickSignInWithEmailAndPassword: () -> Unit,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    isLoadingSignInButton: Boolean,
    isLoadingGoogleButton: Boolean,
    userUiState: UserUiState,
    onValueChanged: (UserUiState) -> Unit,
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit,
    isAlertDialogShowing: Boolean,
    isCircularIndicatorShowing: Boolean,
    isSuccessAlertDialogShowing: Boolean,
    onDismissSuccessAlertDialog: () -> Unit,
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        if (isCircularIndicatorShowing) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        ShowSuccessfulDialog(
            modifier = Modifier.align(Alignment.Center),
            onDismissSuccessAlertDialog = onDismissSuccessAlertDialog,
            isSuccessAlertDialogShowing = isSuccessAlertDialogShowing,
            title = "Account Deleted"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isCircularIndicatorShowing) 0.5f else 1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                ShowAlertDialog(
                    onDismissAlertDialog = onDismiss,
                    onConfirm = onConfirmDelete,
                    confirmButtonText = "Yes",
                    dismissButtonText = "No",
                    isAlertDialogShowing = isAlertDialogShowing,
                    message = "Are you sure you want to delete your account?",
                    title = "Delete Account"
                )

                BaseSignInRow(
                    value = userUiState.email,
                    placeholder = "Email address",
                    readonly = true,
                    enabled = false,
                    onValueChanged = {
                        onValueChanged(userUiState.copy(email = it))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                BaseSignInPassword(
                    value = userUiState.password,
                    placeholder = "Password",
                    onValueChanged = {
                        onValueChanged(userUiState.copy(password = it))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isPasswordVisible = isPasswordVisible,
                    onPasswordVisibilityClicked = {
                        isPasswordVisible = !isPasswordVisible
                    }
                )

                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = userUiState.isValid() && !isLoadingSignInButton,
                    onClick = {
                        if (!isCircularIndicatorShowing) {
                            onClickSignInWithEmailAndPassword()
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(
                            end = 16.dp,
                            start = 16.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = if (isLoadingSignInButton) loadingText else defaultText
                        )

                        if (isLoadingSignInButton) {
                            Spacer(modifier = Modifier.width(16.dp))
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .height(16.dp)
                                    .width(16.dp),
                                strokeWidth = 2.dp,
                                color = progressIndicatorColor
                            )
                        }
                    }
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "or",
                    textAlign = TextAlign.Center
                )

                SignInGoogleButton(
                    text = "Sign in with Google",
                    loadingText = "Signing in",
                    contentDescription = "Sign in with Google",
                    icon = painterResource(R.drawable.ic_google_logo),
                    isLoading = isLoadingGoogleButton,
                    onClick = {
                        if (!isCircularIndicatorShowing) {
                            onClickSignInWithGoogle()
                        }
                    }
                )
            }
        }

    }
}

