package com.example.nkhukumanagement

import android.os.Build
import android.text.Spannable.Factory
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.nkhukumanagement.home.HomeViewModel
import com.example.nkhukumanagement.userinterface.flock.FlockEntryViewModel
import com.example.nkhukumanagement.userinterface.flock.VaccinationViewModel

object AppViewModelProviders {
    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {
        //initializer for FlockEntryViewModel
        initializer {
            FlockEntryViewModel(flockApplication().container.flockRepository)
        }

        initializer {
            HomeViewModel(flockApplication().container.flockRepository)
        }
        initializer {
            VaccinationViewModel(flockApplication().container.flockRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance
 * of FlockApplication
 */
fun CreationExtras.flockApplication(): FlockApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FlockApplication)