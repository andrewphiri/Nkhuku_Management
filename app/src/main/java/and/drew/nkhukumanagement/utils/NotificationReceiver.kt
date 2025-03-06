package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.data.FlockDao
import and.drew.nkhukumanagement.data.FlockDatabase
import and.drew.nkhukumanagement.data.Vaccination
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID


class NotificationReceiver : BroadcastReceiver() {
    var INSTANCE: FlockDatabase? = null

    companion object {
        const val ACTION_YES = "and.drew.nkhukumanagement.utils.ACTION_YES"
        const val ACTION_NO = "and.drew.nkhukumanagement.utils.ACTION_NO"

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




    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            getDatabase(context)
        }
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val action = intent?.action
        val id = intent?.getIntExtra(Constants.VACCINATION_ID, 0) ?: -1
        val name = intent?.getStringExtra(Constants.VACCINATION_NAME) ?: ""
        val date = intent?.getStringExtra(Constants.VACCINATION_DATE)
        val flockUniqueID = intent?.getStringExtra(Constants.VACCINATION_FLOCK_UNIQUE_ID) ?: ""
        val notes = intent?.getStringExtra(Constants.VACCINATION_NOTES) ?: ""
        val vaccineAdministered =
            intent?.getBooleanExtra(Constants.VACCINATION_ADMINISTERED, false) ?: false
        val notificationUUID = intent?.getStringExtra(Constants.VACCINATION_NOTIFICATION_UUID)
        val notificationUUID2 = intent?.getStringExtra(Constants.VACCINATION_NOTIFICATION_UUID2)
        val method = intent?.getStringExtra(Constants.VACCINATION_NOTIFICATION_METHOD) ?: ""
        val vaccineNotificationId = intent?.getIntExtra(Constants.VACCINE_NOTIFICATION_ID, -1) ?: -1

        try {
            if (action == ACTION_YES) {
                val uuid = UUID.fromString(notificationUUID)
                val uuid2 = UUID.fromString(notificationUUID2)
                val vaccination = Vaccination(
                    id = id,
                    flockUniqueId = flockUniqueID,
                    name = name,
                    date = DateUtils().stringToLocalDate(date),
                    hasVaccineBeenAdministered = true,
                    notes = notes,
                    notificationUUID = uuid,
                    notificationUUID2 = uuid2,
                    method = method
                )

                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.flockDao()?.updateVaccination(vaccination)
                }

                notificationManager.cancel(vaccineNotificationId)

            } else {
                notificationManager.cancel(vaccineNotificationId)
            }
        } catch (e :Exception) {
            e.printStackTrace()
        }

    }

}