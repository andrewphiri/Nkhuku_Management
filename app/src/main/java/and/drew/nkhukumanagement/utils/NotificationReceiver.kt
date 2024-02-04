package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.Vaccination
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_YES = "and.drew.nkhukumanagement.utils.ACTION_YES"
        const val ACTION_NO = "and.drew.nkhukumanagement.utils.ACTION_NO"
    }

    @Inject
    lateinit var flockRepository: FlockRepository

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
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
        val vaccineNotificationId = intent?.getIntExtra(Constants.VACCINE_NOTIFICATION_ID, -1) ?: -1

        if (action == ACTION_YES) {

            val uuid = UUID.fromString(notificationUUID)
            val vaccination = Vaccination(
                id = id,
                flockUniqueId = flockUniqueID,
                name = name,
                date = DateUtils().stringToLocalDate(date),
                hasVaccineBeenAdministered = true,
                notes = notes,
                notificationUUID = uuid
            )

            CoroutineScope(Dispatchers.IO).launch {
                flockRepository.updateVaccination(vaccination)
            }

            notificationManager.cancel(vaccineNotificationId)

        } else {
            notificationManager.cancel(vaccineNotificationId)
        }
    }

}