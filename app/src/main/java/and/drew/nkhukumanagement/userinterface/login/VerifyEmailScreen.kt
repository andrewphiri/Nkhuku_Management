package and.drew.nkhukumanagement.userinterface.login

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.ContentType
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object VerifyEmailDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Email
    override val route: String
        get() = "verify email"
    override val resourceId: Int
        get() = R.string.verify_email
}

@Composable
fun VerifyEmailScreen(
    modifier: Modifier = Modifier,
    authUiClient: AuthUiClient,
    canNavigateBack: Boolean = false,
    navigateToHome: () -> Unit,
    onClickSettings: () -> Unit,
    signInViewModel: SignInViewModel,
    contentType: ContentType
) {
    val emailVerified by signInViewModel.emailVerified.collectAsState()

//    Log.i("Email_Verified", authUiClient.isEmailVerified().toString())
    LaunchedEffect(key1 = emailVerified) {
        signInViewModel.setEmailVerification(
            emailVerified = authUiClient.isEmailVerified()
        )
    }

    MainVerifyEmailScreen(
        modifier = modifier,
        authUiClient = authUiClient,
        canNavigateBack = canNavigateBack,
        navigateToHome = navigateToHome,
        onClickSettings = onClickSettings,
        emailVerified = emailVerified,
        setEmailVerification = {
            signInViewModel.setEmailVerification(it)
        },
        contentType = contentType
    )
}

@Composable
fun MainVerifyEmailScreen(
    modifier: Modifier = Modifier,
    authUiClient: AuthUiClient,
    canNavigateBack: Boolean = false,
    navigateToHome: () -> Unit,
    onClickSettings: () -> Unit,
    emailVerified: Boolean,
    setEmailVerification: (Boolean) -> Unit,
    contentType: ContentType
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isCircularIndicatorShowing by remember { mutableStateOf(false) }
    var snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = emailVerified) {
        setEmailVerification(
            authUiClient.isEmailVerified()
        )
    }
    Scaffold(
        modifier = modifier,
        topBar = {
            FlockManagementTopAppBar(
                title = stringResource(VerifyEmailDestination.resourceId),
                canNavigateBack = canNavigateBack,
                onClickSettings = onClickSettings,
                contentType = contentType
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .alpha(if (isCircularIndicatorShowing) 0.5f else 1f)
        ) {
            if (isCircularIndicatorShowing) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.email_verification_sent_prompt),
                    textAlign = TextAlign.Center
                )
                Button(
                    enabled = !isCircularIndicatorShowing,
                    onClick = {
                        coroutineScope.launch {
                            authUiClient.verifyEmail()
                        }

                    },
                ) {
                    Text(
                        text = stringResource(R.string.resend_verification_email)
                    )
                }
            }

            FilledTonalButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = {
                    coroutineScope.launch {
                        setEmailVerification(
                            authUiClient.isEmailVerified()
                        )
                        isCircularIndicatorShowing = true
                        delay(2000)
                        if (emailVerified) {
                            navigateToHome()
                        } else {
                            isCircularIndicatorShowing = false
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.your_email_has_not_yet_been_verified_verify_your_email_then_proceed),
                                duration = SnackbarDuration.Long
                            )
                        }
//                        Log.i("Email_Verified1", authUiClient.isEmailVerified().toString())
                        isCircularIndicatorShowing = false
                    }

                }
            ) {
                Text(
                    text = stringResource(R.string.proceed)
                )
            }
        }
    }
}