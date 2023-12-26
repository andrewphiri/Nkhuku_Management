package and.drew.nkhukumanagement.prefs

import android.icu.util.Currency
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserPrefsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val initialPreferences = userPreferencesRepository.userPrefsFlow

    fun updateNotifications(receiveNotifications: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateReceiveNotifications(receiveNotifications)
        }
    }

    fun updateSkipAccountSetup(skipAccount: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateSkipAccountSetup(skipAccount)
        }
    }

    fun updateCurrency(currency: Currency?, uLocale: String) {
        viewModelScope.launch {
            if (currency != null) {
                userPreferencesRepository.updateDefaultCurrency(currency, uLocale)
            }
        }
    }
}