package and.drew.nkhukumanagement.dependencyInjection

import and.drew.nkhukumanagement.R
import and.drew.nkhukumanagement.utils.Constants
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.calculate)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            val name = context.getString(R.string.channel_name)
//            val descriptionText = context.getString(R.string.channel_description)
//            val importance = NotificationManager.IMPORTANCE_DEFAULT
//            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//                enableLights(true)
//                enableVibration(true)
//            }
//            //Register the channel with the system
//        notificationManager.createNotificationChannel(channel)
//        return notificationManager


    }
}
