package and.drew.nkhukumanagement.userinterface.navigation


import and.drew.nkhukumanagement.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.ui.graphics.vector.ImageVector

sealed class TabScreens(
    val title: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    @RequiresApi(Build.VERSION_CODES.O)
    object Income : TabScreens(
        title = "Income",
        resourceId = R.string.income,
        icon = Icons.Default.Money
    )

    @RequiresApi(Build.VERSION_CODES.O)
    object Expense : TabScreens(
        title = "Expenses",
        resourceId = R.string.expense,
        icon = Icons.Default.MoneyOff
    )

    object SignIn : TabScreens(
        title = "Sign In",
        resourceId = R.string.signin,
        icon = Icons.Default.Person
    )

    object SignUp : TabScreens(
        title = "Sign Up",
        resourceId = R.string.signup,
        icon = Icons.Default.PersonAdd
    )
}
