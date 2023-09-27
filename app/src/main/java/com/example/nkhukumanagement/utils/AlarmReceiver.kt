package com.example.nkhukumanagement.utils

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.example.nkhukumanagement.userinterface.flock.FlockDetailsDestination
import com.example.nkhukumanagement.userinterface.vaccination.VaccinationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(VaccinationViewModel.TITLE) ?: return
        val contentText = intent.getStringExtra(VaccinationViewModel.CONTENT_TEXT) ?: "No text"
        val bigText = intent.getStringExtra(VaccinationViewModel.BIG_TEXT_CONTENT) ?: return
        val flockID = intent.getIntExtra(VaccinationViewModel.FLOCK_ID, 0)

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "${FlockDetailsDestination.uri}/$flockID".toUri()
        )

        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
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
                return
            }
            notificationBuilder.setContentTitle(title)
                .setContentText(contentText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                .setContentIntent(deepLinkPendingIntent)
            notify(Constants.NOTIFICATION_ID, notificationBuilder.build())
            println("ALARM WORKED, $contentText")
        }
    }
}