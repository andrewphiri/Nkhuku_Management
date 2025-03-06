package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.data.FlockDatabase
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
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.SettableFuture
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltWorker
class VaccinationReminderWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    var INSTANCE: FlockDatabase? = null

    companion object {
        @Volatile
        private var INSTANCE: FlockDatabase? = null

        fun getDatabase(context: Context): FlockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use application context
                    FlockDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private val database: FlockDatabase by lazy { getDatabase(context) }


    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
            val vaccinationID = params.inputData.getInt(Constants.VACCINE_NOTIFICATION_ID, 0)

            try {
                database.flockDao().getAllVaccinationItems().collectLatest { vaccinations ->
                    val vaccination = vaccinations.find { it.id == vaccinationID }
                    if (vaccination != null) {
                        showNotification()
                    }
                }
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
        return Result.success()
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