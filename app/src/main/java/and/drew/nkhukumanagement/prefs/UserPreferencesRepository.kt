package and.drew.nkhukumanagement.prefs

import and.drew.nkhukumanagement.UserPreferences
import android.icu.util.Currency
import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val userPrefsDataStore: DataStore<UserPreferences>
) {
    private val TAG: String = "UserPreferencesRepository"

    //Read user preferences
    val userPrefsFlow: Flow<UserPreferences> = userPrefsDataStore.data
        .map {
            it
        }
        .catch { exception ->
            //dataStore throws an IOExeption when an error is encountered when reading data
            if (exception is IOException) {
                Log.i(TAG, "Error reading preferences", exception)
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    val userSkipAccount: Flow<Boolean> = userPrefsDataStore.data
        .map {
            it.skipAccountSetup
        }
        .catch { exception ->
            //dataStore throws an IOExeption when an error is encountered when reading data
            if (exception is IOException) {
                Log.i(TAG, "Error reading preferences", exception)
                emit(UserPreferences.getDefaultInstance().skipAccountSetup)
            } else {
                throw exception
            }
        }

    /**
     * Enable/disable notifications
     */
    suspend fun updateReceiveNotifications(receiveNotifications: Boolean) {
        userPrefsDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setReceiveNotifications(receiveNotifications).build()
        }
    }

    /**
     * Skip account setup
     */
    suspend fun updateSkipAccountSetup(skipAccount: Boolean) {
        userPrefsDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setSkipAccountSetup(skipAccount).build()
        }
    }

    /**
     * Update language locale
     */
    suspend fun updateLanguageLocale(languageLocale: String) {
        userPrefsDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().setLanguageLocale(languageLocale).build()
        }
    }

    /**
     * Save Currency Selected
     */
    suspend fun updateDefaultCurrency(currency: Currency, uLocale: String) {
        userPrefsDataStore.updateData { currentPreferences ->
            currentPreferences.toBuilder()
                .setCurrencyCode(currency.currencyCode)
                .setSymbol(currency.symbol)
                .setDisplayName(currency.displayName)
                .setNumericCode(currency.numericCode)
                .setCurrencyLocale(uLocale)
                .build()
        }
    }
}