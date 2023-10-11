package and.drew.nkhukumanagement.dependencyInjection

import and.drew.nkhukumanagement.data.FlockRepository
import and.drew.nkhukumanagement.data.FlockRepositoryImplementation
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