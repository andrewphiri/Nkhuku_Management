package and.drew.nkhukumanagement.auth

import android.content.Context
import android.content.Intent
import android.util.Log
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
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth

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
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    suspend fun deleteAccount(
        email: String?, password: String,
        googleAuthUiClient: GoogleAuthUiClient, intent: Intent?
    ): Boolean {
        //val googleAccount = GoogleSignIn.getLastSignedInAccount(context)

        val account = if (intent != Intent()) intent?.let {
            googleAuthUiClient.signInGetCredential(
                it
            )
        } else null
        val idToken = account?.googleIdToken
//        val userSignInMethods = auth.currentUser?.email?.let { auth.fetchSignInMethodsForEmail(it).result.signInMethods }
        //val userSignInMethods = email?.let { fetchSignInMethods(it)?.signInMethods }

        val signInMethod = if (idToken != null)
            GoogleAuthProvider.getCredential(idToken, null).signInMethod else ""

        Log.i("Email_Provider", signInMethod)

        try {
            if (signInMethod == "google.com") {
                Log.i("Email", email.toString())
                val googleCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.currentUser?.reauthenticate(googleCredential)?.addOnSuccessListener { task ->
                    auth.currentUser?.delete()
                }
            } else {
                val credential = email?.let { EmailAuthProvider.getCredential(it, password) }
                if (credential != null) {
                    auth.currentUser?.reauthenticate(credential)?.addOnSuccessListener { task ->
                        auth.currentUser?.delete()
                    }
                }
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            return false
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
            auth.currentUser?.sendEmailVerification()?.await()
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