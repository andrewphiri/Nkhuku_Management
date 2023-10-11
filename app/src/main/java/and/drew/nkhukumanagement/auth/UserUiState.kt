package and.drew.nkhukumanagement.auth

data class UserUiState(
    val email: String = "",
    val password: String = "",
    val isEnabled: Boolean = false
)

fun UserUiState.isValid(): Boolean {
    return email.isNotBlank() &&
            password.length >= 8
}
