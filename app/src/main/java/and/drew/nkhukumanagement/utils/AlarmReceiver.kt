package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsDestination
import and.drew.nkhukumanagement.utils.Constants.BIG_TEXT_CONTENT
import and.drew.nkhukumanagement.utils.Constants.CONTENT_TEXT
import and.drew.nkhukumanagement.utils.Constants.FLOCK_ID
import and.drew.nkhukumanagement.utils.Constants.TITLE
import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(TITLE) ?: return
        val contentText = intent.getStringExtra(CONTENT_TEXT) ?: "No text"
        val bigText = intent.getStringExtra(BIG_TEXT_CONTENT) ?: return
        val flockID = intent.getIntExtra(FLOCK_ID, -1)
        val vaccinationHashcode = intent.getIntExtra(Constants.VACCINE_HASHCODE, 0)

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "${FlockDetailsDestination.uri}/$flockID".toUri()
        )
        Log.i("EXTRA_FLOCK_ID", flockID.toString())

        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(vaccinationHashcode, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                //Get activity that started the broadcast
                return
            }
            notificationBuilder.setContentTitle(title)
                .setContentText(contentText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                .setContentIntent(deepLinkPendingIntent)
            notify(vaccinationHashcode, notificationBuilder.build())
            println("ALARM WORKED, $contentText")
        }
    }
}