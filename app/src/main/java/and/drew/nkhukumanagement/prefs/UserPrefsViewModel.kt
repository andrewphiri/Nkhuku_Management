package and.drew.nkhukumanagement.prefs

import android.icu.util.Currency
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class UserPrefsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    companion object {
        private const val MILLIS = 5_000L
    }

    val initialPreferences = userPreferencesRepository.userPrefsFlow

    private val _skipAccountSetup: MutableLiveData<Boolean> = MutableLiveData(false)
    val skipAccountSetup: LiveData<Boolean> = _skipAccountSetup

    //This reads the skipAccountSetup in UserPreferences.
    //RunBlocking ensures that the latest variable is read.
    //Without runBlocking, AccountSetupScreen is shown for a split second and disappears when user chose to skip
    //Account setup.
    val skipAccount: StateFlow<Boolean> = userPreferencesRepository
        .userSkipAccount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            runBlocking { userPreferencesRepository.userSkipAccount.first() }
        )


    fun setSkipAccount(skipAccount: Boolean) {
        _skipAccountSetup.value = skipAccount
    }

    fun updateNotifications(receiveNotifications: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateReceiveNotifications(receiveNotifications)
        }
    }

    fun updateLanguageLocale(languageLocale: String) {
        viewModelScope.launch {
            userPreferencesRepository.updateLanguageLocale(languageLocale)
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