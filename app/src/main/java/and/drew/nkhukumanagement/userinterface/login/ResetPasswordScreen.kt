package and.drew.nkhukumanagement.userinterface.login

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.auth.UserUiState
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.BaseSignInRow
import and.drew.nkhukumanagement.utils.ShowSuccessfulDialog
import android.util.Log
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
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object ResetPasswordDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Password
    override val route: String
        get() = "reset password screen"
    override val resourceId: Int
        get() = R.string.reset_password
}

@Composable
fun ResetPasswordScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    authUiClient: AuthUiClient,
    signInViewModel: SignInViewModel,
    navigateToAccountSetupScreen: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isSendingResetLinkButtonLoading by remember { mutableStateOf(false) }
    var snackbarHostState = remember { SnackbarHostState() }
    var isCircularBarShowing by remember { mutableStateOf(false) }
    var isPasswordResetLinkDialogSuccessShowing by remember { mutableStateOf(false) }
    var isResetLinkSuccessfullySent by remember { mutableStateOf(false) }

//    LaunchedEffect(key1 = isResetLinkSuccessfullySent) {
//        if (isResetLinkSuccessfullySent) {
//            isPasswordResetLinkDialogSuccessShowing = true
//        } else {
//            snackbarHostState.showSnackbar(message = "Invalid email address. Please provide a valid" +
//                    " email address and try again.",
//                duration = SnackbarDuration.Long)
//        }
//
//    }

    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(ResetPasswordDestination.resourceId),
                canNavigateBack = canNavigateBack,
                navigateUp = {
                    signInViewModel.updateUiStateSignIn(UserUiState(email = "", password = ""))
                    onNavigateUp()
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            ResetPasswordCard(
                onClickResetPassword = {
                    isSendingResetLinkButtonLoading = true
                    coroutineScope.launch {
                        isResetLinkSuccessfullySent =
                            authUiClient.resetPassword(signInViewModel.userUiStateSignIn.email)

                        delay(2000)

                        Log.i("PASSWORD RESET LINK", isResetLinkSuccessfullySent.toString())
                        if (isResetLinkSuccessfullySent) {
                            isPasswordResetLinkDialogSuccessShowing = true
                        } else {
                            snackbarHostState.showSnackbar(
                                message = "Invalid email address. Please provide a valid" +
                                        " email address and try again.",
                                duration = SnackbarDuration.Long
                            )
                        }
                        isSendingResetLinkButtonLoading = false
                    }
                },
                isLoadingSignInButton = isSendingResetLinkButtonLoading,
                userUiState = signInViewModel.userUiStateSignIn,
                onValueChanged = signInViewModel::updateUiStateSignIn,
                isCircularIndicatorShowing = isCircularBarShowing,
                isSuccessAlertDialogShowing = isPasswordResetLinkDialogSuccessShowing,
                onDismissSuccessAlertDialog = {
                    signInViewModel.resetState()
                    signInViewModel.updateUiStateSignIn(UserUiState(email = "", password = ""))
                    isPasswordResetLinkDialogSuccessShowing = false
                    navigateToAccountSetupScreen()
                }
            )
        }
    }
}

@Composable
fun ResetPasswordCard(
    modifier: Modifier = Modifier,
    loadingText: String = "Sending link",
    defaultText: String = "Send password reset link",
    onClickResetPassword: () -> Unit,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    isLoadingSignInButton: Boolean,
    userUiState: UserUiState,
    onValueChanged: (UserUiState) -> Unit,
    isCircularIndicatorShowing: Boolean,
    isSuccessAlertDialogShowing: Boolean,
    onDismissSuccessAlertDialog: () -> Unit,
) {
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
            title = "Reset Link Sent",
            failureTitle = "Link Successfully Sent",
            isActionSuccessful = false,
            fileValidMessage = "A password reset link was successfully sent to your email address. Please check your " +
                    "inbox, spam or junk folder and follow the instructions provided. Attempt to log in when you are done."
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

                BaseSignInRow(
                    value = userUiState.email,
                    placeholder = "Email address",
                    onValueChanged = {
                        onValueChanged(userUiState.copy(email = it))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                FilledTonalButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = userUiState.email.isNotBlank() && !isLoadingSignInButton,
                    onClick = {
                        if (!isCircularIndicatorShowing) {
                            onClickResetPassword()
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
            }
        }

    }
}