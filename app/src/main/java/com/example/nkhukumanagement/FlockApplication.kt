package com.example.nkhukumanagement

import android.app.Application
import com.example.nkhukumanagement.data.AppContainer
import com.example.nkhukumanagement.data.AppDataContainer

class FlockApplication : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}