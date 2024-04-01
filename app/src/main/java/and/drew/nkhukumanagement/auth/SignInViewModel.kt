package and.drew.nkhukumanagement.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private val _userLoggedIn = MutableStateFlow(false)
    val userLoggedIn = _userLoggedIn.asStateFlow()

    private val _emailVerified = MutableStateFlow(false)
     val emailVerified = _emailVerified.asStateFlow()

//    val emailVerified: StateFlow<Boolean> =
//        _emailVerified
//            .stateIn(
//                scope = viewModelScope,
//                started = SharingStarted.WhileSubscribed(),
//                runBlocking { _emailVerified.first() }
//            )

    var userUiStateSignIn by mutableStateOf(UserUiState())
        private set

    var userUiStateSignUp by mutableStateOf(UserUiState())
        private set

    /**
     * Update the UserUiState with the passed in value
     */
    fun updateUiStateSignIn(userState: UserUiState) {
        userUiStateSignIn = userState.copy(isEnabled = userState.isValid())
    }

    /**
     * Update the UserUiState with the passed in value
     */
    fun updateUiStateSignUp(userState: UserUiState) {
        userUiStateSignUp = userState.copy(isEnabled = userState.isValid())
    }

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun setUserLoggedIn(loggedIn: Boolean) {
        _userLoggedIn.value = loggedIn
    }

    fun setEmailVerification(emailVerified: Boolean) {
        _emailVerified.value = emailVerified
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}