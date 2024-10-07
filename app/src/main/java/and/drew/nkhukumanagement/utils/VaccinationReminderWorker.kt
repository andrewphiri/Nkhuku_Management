package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.userinterface.flock.FlockDetailsDestination
import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class VaccinationReminderWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters
) : ListenableWorker(context, params) {

    private val future = SettableFuture.create<Result>()
    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    override fun startWork(): ListenableFuture<Result> {
        if (future.isCancelled || this.isStopped) {
            return future // Return the canceled future
        } else {
            try {
                showNotification()
                //Log.i("NOTIFICATION_WORKED", "WORKED")
                future.set(Result.success())
            } catch (e: Exception) {
                // Handle error and set result to failure
                future.setException(e)
                future.set(Result.failure())
            }
        }

        return future
    }

    override fun onStopped() {
        super.onStopped()
        if (future.isCancelled || this.isStopped) {
            NotificationManagerCompat
                .from(context)
                .cancel(params.inputData.getInt(Constants.VACCINE_NOTIFICATION_ID, 0))
           // Log.i("NOTIFICATION_WORKED", "CANCELLED")
        }
    }

    fun showNotification() {
        val data = params.inputData
        val title = data.getString(Constants.TITLE)
        val contentText = data.getString(Constants.CONTENT_TEXT) ?: "No text"
        val bigText = data.getString(Constants.BIG_TEXT_CONTENT)
        val flockID = data.getInt(Constants.FLOCK_ID, -1)
        val vaccinationID = data.getInt(Constants.VACCINE_NOTIFICATION_ID, 0)


        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "${FlockDetailsDestination.uri}/$flockID".toUri()
        )

        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(vaccinationID, PendingIntent.FLAG_UPDATE_CURRENT)
        }

//        Log.i("NOTIFICATION_WORKED", "________")
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
                .setContentIntent(deepLinkPendingIntent)
            notify(vaccinationID, notificationBuilder.build())
        }
    }
}






//@HiltWorker
//class VaccinationReminderWorker @AssistedInject constructor(
//    @Assisted val context: Context,
//    @Assisted val params: WorkerParameters
//) : Worker(context, params) {
//
//    @Inject
//    lateinit var notificationBuilder: NotificationCompat.Builder
//    var vaccinationID = 0
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun doWork(): Result {
//        val data = params.inputData
//        val title = data.getString(Constants.TITLE)
//        val contentText = data.getString(Constants.CONTENT_TEXT) ?: "No text"
//        val bigText = data.getString(Constants.BIG_TEXT_CONTENT)
//        val flockID = data.getInt(Constants.FLOCK_ID, -1)
//        vaccinationID = data.getInt(Constants.VACCINE_NOTIFICATION_ID, 0)
//
//
//        val deepLinkIntent = Intent(
//            Intent.ACTION_VIEW,
//            "${FlockDetailsDestination.uri}/$flockID".toUri()
//        )
//
//        val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
//            addNextIntentWithParentStack(deepLinkIntent)
//            getPendingIntent(vaccinationID, PendingIntent.FLAG_UPDATE_CURRENT)
//        }
//
////        Log.i("NOTIFICATION_WORKED", "________")
//        with(NotificationManagerCompat.from(context)) {
//            if (ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//
//                //Get activity that started the broadcast
//
//            }
//            notificationBuilder.setContentTitle(title)
//                .setContentText(contentText)
//                .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
//                .setColor(Color(0xFF03DAC5).toArgb())
//                .setContentIntent(deepLinkPendingIntent)
//            notify(vaccinationID, notificationBuilder.build())
//        }
//
//        return Result.success()
//    }
//
//    override fun onStopped() {
//
//        super.onStopped()
//    }
//}