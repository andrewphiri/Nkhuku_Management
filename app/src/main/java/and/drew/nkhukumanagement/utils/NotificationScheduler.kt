package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.userinterface.flock.FlockUiState

interface NotificationScheduler {
    fun schedule(vaccination: Vaccination, flock: FlockUiState)
    fun schedule(vaccination: Vaccination, flock: FlockUiState, notificationID: Int)
    fun cancelNotification(vaccination: Vaccination)
}