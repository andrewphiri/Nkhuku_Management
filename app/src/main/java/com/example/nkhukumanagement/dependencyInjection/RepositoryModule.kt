package com.example.nkhukumanagement.dependencyInjection

import com.example.nkhukumanagement.data.FlockRepository
import com.example.nkhukumanagement.data.FlockRepositoryImplementation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module to provide instances of an interface [FlockRepository]
 * Annotated with @Binds.
 */
@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindFlockRepository(impl: FlockRepositoryImplementation) : FlockRepository
}