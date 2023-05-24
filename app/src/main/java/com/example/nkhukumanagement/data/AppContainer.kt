package com.example.nkhukumanagement.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val flockRepository: FlockRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineFlockRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [OfflineFlockRepository]
     */
    override val flockRepository: FlockRepository by lazy {
        OfflineFlockRepository(FlockDatabase.getDatabase(context).flockDao())
    }
}