package com.example.nkhukumanagement.dependencyInjection

import android.content.Context
import com.example.nkhukumanagement.FlockApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provide for instances of FlockApplication that will be used
     * to inject context in VaccinationViewModel
     */
    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext context: Context): FlockApplication {
        return context as FlockApplication
    }

}