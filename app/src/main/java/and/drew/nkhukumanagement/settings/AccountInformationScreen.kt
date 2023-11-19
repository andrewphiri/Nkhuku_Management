package and.drew.nkhukumanagement.settings

import and.drew.nkhukumanagement.FlockManagementTopAppBar
import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.auth.AuthUiClient
import and.drew.nkhukumanagement.auth.SignInViewModel
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.userinterface.navigation.NkhukuDestinations
import and.drew.nkhukumanagement.utils.ShowAlertDialog
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

object AccountInformationDestination : NkhukuDestinations {
    override val icon: ImageVector
        get() = Icons.Default.Person
    override val route: String
        get() = "Account Information"
    override val resourceId: Int
        get() = R.string.account_information
}

@Composable
fun AccountInfoScreen(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = true,
    onNavigateUp: () -> Unit,
    authUiClient: AuthUiClient,
    navigateToSignInScreen: () -> Unit,
    signInViewModel: SignInViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var emailSignedIn by remember { mutableStateOf("") }
    emailSignedIn = authUiClient.signedInUser().email.toString()
    var title by remember { mutableStateOf(context.getString(R.string.add_account)) }
    title = if (emailSignedIn != "null")
        context.getString(AccountInformationDestination.resourceId) else context.getString(R.string.add_account)
    Log.i("EMAIL_SIGNED_IN", emailSignedIn)
    var isConfirmAlertDialogShowing by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            FlockManagementTopAppBar(
                title = title,
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            AccountInfoCard(
                email = emailSignedIn,
                onNavigateUp = onNavigateUp,
                onSignOut = {
                    coroutineScope.launch {
                        authUiClient.signOut()
                        signInViewModel.resetState()
                        signInViewModel.setUserLoggedIn(false)
                    }
                },
                onDeleteAccount = {
                    isConfirmAlertDialogShowing = true
                },
                isAlertDialogShowing = isConfirmAlertDialogShowing,
                onDismiss = { isConfirmAlertDialogShowing = false },
                onConfirmDelete = {
                    coroutineScope.launch {
                        authUiClient.deleteAccount()
                        signInViewModel.resetState()
                        signInViewModel.setUserLoggedIn(false)
                        isConfirmAlertDialogShowing = false
                    }
                },
                navigateToSignInScreen = navigateToSignInScreen
            )
        }
    }
}

@Composable
fun AccountInfoCard(
    modifier: Modifier = Modifier,
    email: String,
    onNavigateUp: () -> Unit,
    onSignOut: () -> Unit,
    onDeleteAccount: () -> Unit,
    isAlertDialogShowing: Boolean,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit,
    navigateToSignInScreen: () -> Unit,
) {
    if (email == "null") {
        Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No account added.",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            OutlinedButton(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 8.dp, end = 8.dp),
                onClick = navigateToSignInScreen,
                shape = CircleShape,
                border = BorderStroke(Dp.Hairline, color = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Add email account"
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                ShowAlertDialog(
                    onDismissAlertDialog = onDismiss,
                    onConfirm = onConfirmDelete,
                    confirmButtonText = "Yes",
                    dismissButtonText = "No",
                    isAlertDialogShowing = isAlertDialogShowing,
                    message = "Are you sure you want to delete your account?",
                    title = "Delete Account"
                )
                Text(
                    text = "Current email"
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    text = email,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    OutlinedButton(
                        border = BorderStroke(0.dp, color = Color.Transparent),
                        onClick = onNavigateUp,
                    ) {
                        Text(
                            text = "Back",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                        )
                    }
                    OutlinedButton(
                        border = BorderStroke(0.dp, color = Color.Transparent),
                        onClick = onSignOut,
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = TextUnit(16f, TextUnitType.Sp),
                            text = "Sign Out"
                        )
                    }
                }

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onDeleteAccount)
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    text = "Delete",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Red,
                )
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AccountInfoPreview() {
    NkhukuManagementTheme {
        AccountInfoCard(
            email = "abcf@gmail.com",
            onNavigateUp = {},
            onSignOut = {},
            onDeleteAccount = {},
            onDismiss = {},
            onConfirmDelete = {},
            isAlertDialogShowing = false,
            navigateToSignInScreen = {}
        )
    }
}