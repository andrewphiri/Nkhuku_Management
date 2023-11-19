package and.drew.nkhukumanagement.userinterface.login

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.auth.UserUiState
import and.drew.nkhukumanagement.auth.isValid
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
import and.drew.nkhukumanagement.utils.BaseSignInPassword
import and.drew.nkhukumanagement.utils.BaseSignInRow
import and.drew.nkhukumanagement.utils.SignInGoogleButton
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onClickSignInWithGoogle: () -> Unit,
    loadingText: String = "Signing in",
    defaultText: String = "Sign In",
    onClickSignInWithEmailAndPassword: () -> Unit,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    isLoadingSignInButton: Boolean = false,
    isLoadingGoogleButton: Boolean,
    userUiState: UserUiState,
    onValueChanged: (UserUiState) -> Unit,
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier) {
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

            Text(
                modifier = Modifier.align(Alignment.End),
                text = "Forgot password?"
            )

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = userUiState.isValid() && !isLoadingSignInButton,
                onClick = onClickSignInWithEmailAndPassword
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
                onClick = onClickSignInWithGoogle
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    NkhukuManagementTheme {
//        SignInScreen(onClickSignIn = {})
    }
}