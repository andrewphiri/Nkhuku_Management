package and.drew.nkhukumanagement.utils

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationService: FirebaseMessagingService() {
    val auth = Firebase.auth
    override fun onNewToken(token: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                sendRegistrationToServer(token)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("FAILED_TOKEN", "Token failed")
            }
        }
    }



    private fun sendRegistrationToServer(token: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userID = auth.currentUser?.uid

        val tokenData = hashMapOf(
            "myToken" to token
        )
        if (userID != null) {
            firestore.collection("FCMTokens")
                .document(userID)
                .set(tokenData)
                .addOnSuccessListener {
                Log.d("Token Success", "Token saved to Firestore successfully")
            }
                .addOnFailureListener { e ->
                    Log.e("Token Failure", "Error saving token to Firestore", e)
                }
        }
    }

    fun insertTokenOnFirstSignUp() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FETCHING TOKEN FAILED", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    sendRegistrationToServer(token)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.i("FAILED_TOKEN", "Token failed")
                }
            }
        })
    }
}