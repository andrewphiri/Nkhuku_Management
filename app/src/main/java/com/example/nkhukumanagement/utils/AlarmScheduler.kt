package com.example.nkhukumanagement.utils

import com.example.nkhukumanagement.data.Vaccination
import com.example.nkhukumanagement.userinterface.flock.FlockUiState

interface AlarmScheduler {
    fun schedule(vaccination: Vaccination, flock: FlockUiState)
    fun cancelAlarm(vaccination: Vaccination)
}