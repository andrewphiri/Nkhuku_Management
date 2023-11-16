package and.drew.nkhukumanagement.dependencyInjection

import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.utils.Constants
import and.drew.nkhukumanagement.utils.UserPreferencesSerializer
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesDataStore(@ApplicationContext context: Context): DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            produceFile = {
                context.dataStoreFile(Constants.DATA_STORE_FILE_NAME)
            },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )

}