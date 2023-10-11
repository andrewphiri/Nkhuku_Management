package and.drew.nkhukumanagement.userinterface.login

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.auth.UserUiState
import and.drew.nkhukumanagement.auth.isValid
import and.drew.nkhukumanagement.ui.theme.NkhukuManagementTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onClickSignUpWithGoogle: () -> Unit,
    loadingText: String = "Signing up",
    defaultText: String = "Sign up",
    onClickSignUpWithEmailAndPassword: () -> Unit,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    isLoadingSignUpButton: Boolean = false,
    isLoadingGoogleButton: Boolean,
    userUiState: UserUiState,
    onValueChanged: (UserUiState) -> Unit,
) {
    Column(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

//                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
//                    BaseSignInRow(
//                        modifier = Modifier.weight(1f),
//                        value = "",
//                        placeholder = "First name",
//                        onValueChanged = {},
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
//                    )
//                    BaseSignInRow(
//                        modifier = Modifier.weight(1f),
//                        value = "",
//                        placeholder = "Last name",
//                        onValueChanged = {},
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
//                    )
//                }

            BaseSignInRow(
                value = userUiState.email,
                placeholder = "Email address",
                onValueChanged = {
                    onValueChanged(userUiState.copy(email = it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            BaseSignInRow(
                value = userUiState.password,
                placeholder = "Password",
                onValueChanged = {
                    onValueChanged(userUiState.copy(password = it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

//                BaseSignInRow(
//                    value = "",
//                    placeholder = "Retype Password",
//                    onValueChanged = {},
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
//                )


            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = userUiState.isValid() && !isLoadingSignUpButton,
                onClick = onClickSignUpWithEmailAndPassword
            ) {
                Row(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = if (isLoadingSignUpButton) loadingText else defaultText
                    )

                    if (isLoadingSignUpButton) {
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
                text = "Sign up with Google",
                loadingText = "Signing up",
                isLoading = isLoadingGoogleButton,
                contentDescription = "Sign up with Google",
                icon = painterResource(R.drawable.ic_google_logo),
                onClick = onClickSignUpWithGoogle
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
    NkhukuManagementTheme {
//        SignUpScreen()
    }
}