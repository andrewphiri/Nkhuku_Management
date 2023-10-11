package and.drew.nkhukumanagement.dependencyInjection

import and.drew.nkhukumanagement.data.FlockDao
import and.drew.nkhukumanagement.data.FlockDatabase
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to provide instances of [FlockDao] and [FlockDatabase]
 */

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideFlockDao(flockDatabase: FlockDatabase) : FlockDao {
        return flockDatabase.flockDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : FlockDatabase {
       return Room.databaseBuilder(
           context = context,
           FlockDatabase::class.java,
           "flock_database")
            .fallbackToDestructiveMigration()
            .build()
    }
}