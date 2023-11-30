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

fun UserUiState.isPasswordValid(password: String): Boolean {
    return Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$")
        .matches(password)
}
