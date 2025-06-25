package and.drew.nkhukumanagement.auth

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.credentials.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException
import and.drew.nkhukumanagement.R
import android.credentials.GetCredentialException
import android.util.Log
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

class GoogleAuthUiClient(
    private val context: Context,
    private val auth: FirebaseAuth = Firebase.auth
) {
    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): SignInResult {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_ID))
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(
                    googleIdTokenCredential.idToken, null
                )

                val user = auth.signInWithCredential(firebaseCredential).await().user
                SignInResult(
                    data = user?.run {
                        User(
                            userId = uid,
                            username = displayName,
                            email = email
                        )
                    },
                    errorMessage = null
                )
            } else {
                SignInResult(null, "Unexpected credential type")
            }

        } catch (e: androidx.credentials.exceptions.GetCredentialException) {
            SignInResult(null, e.message)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(null, e.message)
        }
    }

    suspend fun handleSignIn(result: GetCredentialResponse): SignInResult {
        return try {
            val credential = result.credential

            when (credential) {
                is PublicKeyCredential -> {
                    // Optional: handle passkeys
                    SignInResult(null, "Passkey sign-in not implemented.")
                }

                is PasswordCredential -> {
                    val username = credential.id
                    val password = credential.password

                    val authResult = auth.signInWithEmailAndPassword(username, password).await()

                    SignInResult(
                        data = authResult.user?.run {
                            User(uid, displayName, email)
                        },
                        errorMessage = null
                    )
                }

                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            val idToken = googleIdTokenCredential.idToken

                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            val authResult = auth.signInWithCredential(firebaseCredential).await()

                            SignInResult(
                                data = authResult.user?.run {
                                    User(uid, displayName, email)
                                },
                                errorMessage = null
                            )
                        } catch (e: GoogleIdTokenParsingException) {
                            Log.e("GoogleAuth", "Invalid Google ID token", e)
                            SignInResult(null, "Invalid Google ID token.")
                        }
                    } else {
                        Log.e("GoogleAuth", "Unexpected custom credential type: ${credential.type}")
                        SignInResult(null, "Unexpected credential type.")
                    }
                }

                else -> {
                    Log.e("GoogleAuth", "Unknown credential type")
                    SignInResult(null, "Unknown credential type.")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(null, e.message)
        }
    }


    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(request = ClearCredentialStateRequest())
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): User? = auth.currentUser?.run {
        User(
            userId = uid,
            username = displayName,
            email = email
        )
    }
}
