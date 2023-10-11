package and.drew.nkhukumanagement.utils

import and.drew.nkhukumanagement.data.Vaccination
import and.drew.nkhukumanagement.userinterface.flock.FlockUiState

interface AlarmScheduler {
    fun schedule(vaccination: Vaccination, flock: FlockUiState)
    fun cancelAlarm(vaccination: Vaccination)
}