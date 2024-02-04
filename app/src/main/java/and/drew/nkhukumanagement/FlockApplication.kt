package and.drew.nkhukumanagement

import and.drew.nkhukumanagement.utils.Constants
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FlockApplication : BaseFlockApplication(), Configuration.Provider {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.channel_name)
            val descriptionText = applicationContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
                lightColor = Color.YELLOW
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

}