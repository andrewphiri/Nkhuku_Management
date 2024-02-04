package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsDestination
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class VaccinationConfirmationWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters
) : Worker(context, params) {

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val data = params.inputData
        val title = data.getString(Constants.TITLE_TWO)
        val contentText = data.getString(Constants.CONTENT_TEXT_TWO) ?: "No text"
        val bigText = data.getString(Constants.BIG_TEXT_CONTENT_TWO)
        val flockID = data.getInt(Constants.FLOCK_ID, -1)
        val vaccinationID = data.getInt(Constants.VACCINE_NOTIFICATION_ID, 0)

        val yesVaccineAdministeredIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_YES
            putExtra(Constants.VACCINE_NOTIFICATION_ID, vaccinationID)
            putExtra(Constants.VACCINATION_ID, data.getInt(Constants.VACCINATION_ID, 0))
            putExtra(
                Constants.VACCINATION_FLOCK_UNIQUE_ID,
                data.getString(Constants.VACCINATION_FLOCK_UNIQUE_ID)
            )
            putExtra(Constants.VACCINATION_NAME, data.getString(Constants.VACCINATION_NAME))
            putExtra(Constants.VACCINATION_DATE, data.getString(Constants.VACCINATION_DATE))
            putExtra(
                Constants.VACCINATION_NOTIFICATION_UUID,
                data.getString(Constants.VACCINATION_NOTIFICATION_UUID)
            )
            putExtra(
                Constants.VACCINATION_ADMINISTERED,
                data.getBoolean(Constants.VACCINATION_ADMINISTERED, false)
            )
            putExtra(Constants.VACCINATION_NOTES, data.getString(Constants.VACCINATION_NOTES))
        }

        val yesVaccinePendingIntent = PendingIntent.getBroadcast(
            context,
            vaccinationID,
            yesVaccineAdministeredIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val noVaccineAdministeredIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_NO
            putExtra(Constants.VACCINE_NOTIFICATION_ID, vaccinationID)
        }

        val noVaccinePendingIntent = PendingIntent.getBroadcast(
            context,
            vaccinationID,
            noVaccineAdministeredIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "${FlockDetailsDestination.uri}/$flockID".toUri()
        )

        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(vaccinationID, PendingIntent.FLAG_UPDATE_CURRENT)
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

            }
            notificationBuilder.setContentTitle(title)
                .setContentText(contentText)
                .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
                .setColor(Color(0xFF03DAC5).toArgb())
                .addAction(R.drawable.baseline_check, "Yes", yesVaccinePendingIntent)
                .addAction(R.drawable.baseline_close, "No", noVaccinePendingIntent)
                .setContentIntent(deepLinkPendingIntent)
            notify(vaccinationID, notificationBuilder.build())
        }

        return Result.success()
    }
}