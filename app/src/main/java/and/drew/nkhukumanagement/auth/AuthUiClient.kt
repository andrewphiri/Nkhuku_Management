package and.drew.nkhukumanagement.auth

import and.drew.nkhukumanagement.MainActivity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.SignInMethodQueryResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException


class AuthUiClient(
    context: Context
) {
    private val auth = Firebase.auth
    private val credentialManager = CredentialManager.create(context)

    suspend fun createUserWithEmailAndPassWord(email: String, password: String): SignInResult {
        return try {
            val user = createNewAccountWithEmailAndPassword(email, password).await().user
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
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    fun createNewAccountWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { }
    }

    suspend fun signInUserWithEmailAndPassWord(email: String, password: String): SignInResult {
        return try {
            val user = signIn(email, password).await().user
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
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    fun signIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {}
    }

    suspend fun signOut() {
        try {
            auth.signOut()
            credentialManager.clearCredentialState(request = ClearCredentialStateRequest())
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    suspend fun deleteAccount(
        email: String?,
        password: String,
        googleAuthUiClient: GoogleAuthUiClient,
    ): Boolean {
        val user = Firebase.auth.currentUser ?: return false

        return try {
            // Determine how the user signed in (Google or Email/Password)
            val signInMethods = email?.let {
                Firebase.auth.fetchSignInMethodsForEmail(it).await().signInMethods
            }

            if (signInMethods?.contains("google.com") == true) {
                // Reauthenticate using Google
                val signInResult = googleAuthUiClient.signIn()
                val idToken = signInResult.data?.let { Firebase.auth.currentUser?.getIdToken(true)?.await()?.token }
                val googleCredential = GoogleAuthProvider.getCredential(idToken, null)
                user.reauthenticate(googleCredential).await()
            } else if (email != null) {
                // Reauthenticate using Email/Password
                val credential = EmailAuthProvider.getCredential(email, password)
                user.reauthenticate(credential).await()
            } else {
                return false
            }

            // Delete account after successful re-auth
            user.delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            false
        }
    }


    suspend fun fetchSignInMethods(email: String?): SignInMethodQueryResult? {
        return email?.let { auth.fetchSignInMethodsForEmail(it).await() }
    }

    fun signedInUser(): User {
        val userSignedIn = auth.currentUser
        return User(
            email = userSignedIn?.email,
            userId = userSignedIn?.uid,
            username = userSignedIn?.displayName
        )
    }

    suspend fun verifyEmail() {
        try {
            auth.currentUser?.reload()?.await()
            if (!isEmailVerified()) {
                auth.currentUser?.sendEmailVerification()?.await()
            } else {
                isEmailVerified()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun isEmailVerified(): Boolean {
        return try {
            auth.currentUser?.reload()
            auth.currentUser?.isEmailVerified == true
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            auth.currentUser?.isEmailVerified == true
        }
    }

    suspend fun isEmailVerifiedNonGoogle(): Boolean {
        return try {
            auth.currentUser?.reload()?.await()
            auth.currentUser?.isEmailVerified == true
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            auth.currentUser?.isEmailVerified == true
        }
    }

    fun refreshEmail() {
        try {
            auth.currentUser?.reload()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        var emailSentSuccessfully = false
        try {
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                emailSentSuccessfully = true
            }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    if (e is CancellationException) throw e
                }.await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
        return emailSentSuccessfully
    }

    suspend fun isResetPasswordLinkSentSuccessfully(email: String) {
        resetPassword(email)
    }
}