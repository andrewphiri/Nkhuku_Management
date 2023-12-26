package and.drew.nkhukumanagement.DI

import and.drew.nkhukumanagement.UserPreferences
import and.drew.nkhukumanagement.dependencyInjection.DataStoreModule
import and.drew.nkhukumanagement.prefs.UserPreferencesSerializer
import and.drew.nkhukumanagement.utils.Constants
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton
import kotlin.random.Random

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class]
)
object TestModules {
    @Singleton
    @Provides
    fun provideFakePreferences(
        @ApplicationContext context: Context
    ): DataStore<UserPreferences> {
        val random = Random.nextInt() // generating here
        return DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            produceFile = {
                context.dataStoreFile("{ $Constants.DATA_STORE_FILE_NAME }-$random")
            },
            corruptionHandler = null,
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        )
    }
}